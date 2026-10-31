package com.example.obd2linkbackend.passkey.service.impl;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Base64;
import java.util.HexFormat;

import org.springframework.stereotype.Service;

import com.example.obd2linkbackend.passkey.exception.ChallengeVerificationException;
import com.example.obd2linkbackend.passkey.mapper.AuthChallengeMapper;
import com.example.obd2linkbackend.passkey.mapper.PassKeysMapper;
import com.example.obd2linkbackend.passkey.model.entity.AuthChallengeEntity;
import com.example.obd2linkbackend.passkey.model.entity.PassKeyEntity;
import com.example.obd2linkbackend.passkey.model.enums.ChallengePurpose;
import com.example.obd2linkbackend.passkey.service.WebAuthnService;
import com.example.obd2linkbackend.user.mapper.SessionMapper;
import com.example.obd2linkbackend.user.mapper.UserMapper;
import com.example.obd2linkbackend.user.model.dto.request.LoginReqDto;
import com.example.obd2linkbackend.user.model.dto.request.UserReqDto;
import com.example.obd2linkbackend.user.model.dto.response.LoginResDto;
import com.example.obd2linkbackend.user.model.entity.SessionEntity;
import com.example.obd2linkbackend.user.model.entity.UserEntity;
import com.example.obd2linkbackend.user.service.SessionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.webauthn4j.data.AuthenticationParameters;
import com.webauthn4j.data.PublicKeyCredentialCreationOptions;
import com.webauthn4j.data.PublicKeyCredentialParameters;
import com.webauthn4j.data.PublicKeyCredentialRequestOptions;
import com.webauthn4j.data.PublicKeyCredentialRpEntity;
import com.webauthn4j.data.PublicKeyCredentialType;
import com.webauthn4j.data.UserVerificationRequirement;
import com.webauthn4j.data.client.Origin;
import com.webauthn4j.data.client.challenge.Challenge;
import com.webauthn4j.data.client.challenge.DefaultChallenge;
import com.webauthn4j.data.extension.authenticator.AuthenticationExtensionsAuthenticatorOutputs;
import com.webauthn4j.server.ServerProperty;
import com.webauthn4j.verifier.exception.VerificationException;
import com.webauthn4j.data.PublicKeyCredentialUserEntity;
import com.webauthn4j.data.RegistrationData;
import com.webauthn4j.data.AuthenticationData;
import com.webauthn4j.data.RegistrationParameters;
import com.webauthn4j.data.attestation.authenticator.AAGUID;
import com.webauthn4j.data.attestation.authenticator.AttestedCredentialData;
import com.webauthn4j.data.attestation.authenticator.COSEKey;
import com.webauthn4j.data.attestation.statement.COSEAlgorithmIdentifier;
import com.webauthn4j.data.attestation.statement.NoneAttestationStatement;
import com.webauthn4j.WebAuthnManager;
import com.webauthn4j.converter.util.ObjectConverter;
import com.webauthn4j.credential.CredentialRecord;
import com.webauthn4j.credential.CredentialRecordImpl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class WebAuthnServiceImpl implements WebAuthnService{

    private final ObjectMapper objectMapper;
    private final AuthChallengeMapper authChallengeMapper;
    private final UserMapper userMapper;
    private final PassKeysMapper passKeysMapper;
    private final SessionService sessionService;
    private final SessionMapper sessionMapper;

    // クライアントが使用する公開鍵の種類の指定と、アルゴリズムを指定
    private static final List<PublicKeyCredentialParameters> PUB_KEY_CRED_PATAMS =
        List.of(new PublicKeyCredentialParameters(
            PublicKeyCredentialType.PUBLIC_KEY,
            COSEAlgorithmIdentifier.ES256
        ));

    /**
     * パスキー登録用のチャレンジと設定情報を生成し、DBへ一時保存する
     * ユーザー端末でパスキーを作成する際に必要な情報をまとめて返却
    **/
    @Transactional 
    public PublicKeyCredentialCreationOptions generateRegistrationChallenge(UserReqDto userReqDto){
        // リクエストごとのチャレンジを生成
        Challenge challenge = new DefaultChallenge();
        byte[] challengeBytes = challenge.getValue();

        /**
         * パスキー登録時に端末へ渡すユーザー情報を作成
         * コンストラクターの第一引数はbyteの配列指定のため、バイト配列のIDを生成
        **/
        byte[] userIdBytes = new byte[32];
        new SecureRandom().nextBytes(userIdBytes);
        PublicKeyCredentialUserEntity user = new PublicKeyCredentialUserEntity(userIdBytes, userReqDto.getEmail(), userReqDto.getDisplayName());

        // ユーザーにパスキー登録してもらう時のサーバー側情報を定義
        PublicKeyCredentialRpEntity rp = new PublicKeyCredentialRpEntity("localhost", "OBD2 Link");

        // チャレンジをDBへ保存
        // 引数の「registrationData」はDBへJSONで保存したいための処理
        try{
            Instant now = Instant.now();
            AuthChallengeEntity entity = AuthChallengeEntity.create(userIdBytes, challengeBytes, ChallengePurpose.REGISTRATION, objectMapper.writeValueAsString(userReqDto), now.plus(Duration.ofMinutes(5)));
            authChallengeMapper.insertChallenge(entity);
        }catch(JsonProcessingException ex){
            throw new RuntimeException(ex);
        }

        // サーバー側、公開鍵、ユーザー、チャレンジの4つの情報をまとめたオブジェクトをコントローラーへレスポンス
        return new PublicKeyCredentialCreationOptions(rp, user, challenge, PUB_KEY_CRED_PATAMS);
    }

    /**
     * ユーザー端末から受け取ったパスキー登録情報とDBのチャレンジを検証する
     * 検証に成功した場合、ユーザー情報と公開鍵（パスキー）をDBへ本登録する
    **/
    @Transactional
    public void verifyChallenge(String registrationResponseJSON){
        // clientDataJSONからチャレンジを取り出す
        byte[] challengeBytes;
        try{
            JsonNode registrationResponse = objectMapper.readTree(registrationResponseJSON);
            byte[] clientDataJson = Base64.getUrlDecoder().decode(
                registrationResponse.get("response").get("clientDataJSON").asText());
            JsonNode clientData = objectMapper.readTree(clientDataJson);
            challengeBytes = Base64.getUrlDecoder().decode(clientData.get("challenge").asText());
        }catch(IOException ex){
            throw new IllegalArgumentException("clientDataJSONの形式が正しくありません", ex);
        }
        
        // DBから未期限切れのチャレンジを取得
        AuthChallengeEntity challenge = authChallengeMapper.selectChallenge(challengeBytes, ChallengePurpose.REGISTRATION);
        if(challenge == null){
            throw new ChallengeVerificationException("チャレンジが見つからないか、期限切れです");
        }
        
        // サーバーで生成したチャレンジと、クライアント側のチャレンジを検証
        WebAuthnManager webAuthnManager = WebAuthnManager.createNonStrictWebAuthnManager();

        // ユーザー端末から受け取ったチャレンジ含むJSONを解析
        RegistrationData registrationData = webAuthnManager.parseRegistrationResponseJSON(registrationResponseJSON);

        // サーバー側の検証条件作成
        ServerProperty serverProperty = new ServerProperty(
            new Origin("http://localhost:8080"),
            "localhost",
            new DefaultChallenge(challenge.getChallenge())
        );

        RegistrationParameters registrationParameters = new RegistrationParameters(
            serverProperty,
            PUB_KEY_CRED_PATAMS,
            true, // 生体認証を必須に
            true
        ); // ユーザー操作を必須
        
        try{
            // ユーザーから届いたwebauthnとサーバーで保存してあるチャレンジを検証
            webAuthnManager.verify(registrationData, registrationParameters);
            // 検証成功後、使用済みチャレンジを削除
            int deleteCount = authChallengeMapper.deleteChallenge(challenge.getId());
            // チャレンジが削除されたか確認
            if(deleteCount != 1){
                throw new ChallengeVerificationException("チャレンジは既に使用されています");
            }

            // 検証に成功したので、usersテーブルへユーザー情報を保存
            UserReqDto userReqDto = objectMapper.readValue(challenge.getPendingUserData(), UserReqDto.class);
            UserEntity userEntity = UserEntity.create(userReqDto.getEmail(), userReqDto.getDisplayName());
            userMapper.registerUser(userEntity);

            // PassKeyEntityに保存するデータの階層を掘り下げる
            var attestationObject = registrationData.getAttestationObject();
            var authenticatorData = attestationObject.getAuthenticatorData();
            var attestedCredentialData = authenticatorData.getAttestedCredentialData();

            // credentialIdを取り出す
            byte[] credentialId = attestedCredentialData.getCredentialId();
            // userMapper.registerUser()で作成したユーザーの主キーを取得
            Integer generatedUserId = userEntity.getId();
            // 公開鍵を取り出す
            ObjectConverter objectConverter = new ObjectConverter();
            byte[] publicKey = objectConverter.getCborConverter().writeValueAsBytes(attestedCredentialData.getCOSEKey());
            // 認証回数を取り出す
            Long signCount = authenticatorData.getSignCount();
            
            PassKeyEntity passKeyEntity = PassKeyEntity.create(credentialId, // 公開鍵に対応した一意の識別番号
                generatedUserId, // usersテーブルの主キーを参照する外部キー
                publicKey, // ユーザーの端末で作成された公開鍵
                signCount, // 認証回数のカウント
                "パスキー"); // 端末名称
            passKeysMapper.registerPassKey(passKeyEntity);

        }catch(VerificationException ex){
            // 検証に失敗した場合の例外処理
            throw new ChallengeVerificationException("パスキーが正しくありません");
        }catch(JsonProcessingException ex){
            // ユーザー情報が存在しなかった場合の例外処理
            throw new RuntimeException("ユーザー登録に失敗しました");
        }
    }

    /**
     * ユーザーからリクエスト時に受領したメールアドレスを元にCredentialIDと公開鍵を探す
     */
    @Transactional 
    public PublicKeyCredentialRequestOptions generateLoginChallenge(LoginReqDto loginReqDto){
        // ユーザーに検証してもらうチャレンジを生成
        Challenge challenge = new DefaultChallenge();
        byte[] challengeBytes = challenge.getValue();

        PublicKeyCredentialRequestOptions options = new PublicKeyCredentialRequestOptions(challenge, 300000L, "localhost", null, UserVerificationRequirement.REQUIRED, null);
        
        // 生成したチャレンジをDBへ保存
        Instant now = Instant.now();
        AuthChallengeEntity entity = AuthChallengeEntity.create(null, challengeBytes, ChallengePurpose.LOGIN, null, now.plus(Duration.ofMinutes(5)));
        authChallengeMapper.insertChallenge(entity);

        return options;
    }

    /**
     * サーバー側が発行したチャレンジの検証
     */
    @Transactional 
    public LoginResDto verifyLoginChallenge(String loginResponseJSON){
        // clientDataJSONからチャレンジを取り出す
        byte[] challengeBytes;
        try{
            JsonNode loginResponse = objectMapper.readTree(loginResponseJSON);
            byte[] clientDataJson = Base64.getUrlDecoder().decode(
                loginResponse.get("response").get("clientDataJSON").asText()
            );
            JsonNode clientData = objectMapper.readTree(clientDataJson);
            challengeBytes = Base64.getUrlDecoder().decode(
                clientData.get("challenge").asText()
            );
        }catch(IOException ex){
            throw new IllegalArgumentException("clientDataJSONの形式が正しくありません", ex);
        }

        // ユーザーが検証したチャレンジと、DBの"LOGIN"と一致するレコードを取得
        AuthChallengeEntity challenge = authChallengeMapper.selectChallenge(challengeBytes, ChallengePurpose.LOGIN);
        if(challenge == null){
            throw new ChallengeVerificationException("チャレンジが見つからないか、期限切れです");
        }

        // サーバーで生成したチャレンジと、クライアント側のチャレンジを検証
        WebAuthnManager webAuthnManager = WebAuthnManager.createNonStrictWebAuthnManager();
        // サーバー側の検証条件作成
        ServerProperty serverProperty = new ServerProperty(
            new Origin("http://localhost:8080"),
            "localhost",
            new DefaultChallenge(challenge.getChallenge())
        );
        // ユーザー端末から受け取ったJSONを解析
        AuthenticationData authenticationData =
            webAuthnManager.parseAuthenticationResponseJSON(loginResponseJSON);
        // credentialIdを取り出す
        byte[] credentialId = authenticationData.getCredentialId();
        // credentialIdを元にDBからパスキー情報を取得
        PassKeyEntity passKey = passKeysMapper.selectByCredentialId(credentialId);
        if(passKey == null){
            throw new ChallengeVerificationException("登録済みのパスキーが見つかりません");
        }

        ObjectConverter objectConverter = new ObjectConverter();
        // サーバーに保存してあった公開鍵を取得
        COSEKey coseKey = objectConverter.getCborConverter()
            .readValue(passKey.getPublicKey(), COSEKey.class);
        
        //　公開鍵とCredentialIdをwebauthn4jの型にまとめる
        AttestedCredentialData attestedCredentialData = new AttestedCredentialData(
            AAGUID.NULL,
            passKey.getCredentialId(),
            coseKey
        );

        // passKeyのDBから取得した情報をまとめて検証用レコードを作成
        CredentialRecord credentialRecord = new CredentialRecordImpl(
            new NoneAttestationStatement(),
            null,
            null,
            null,
            passKey.getSignCount(),
            attestedCredentialData,
            new AuthenticationExtensionsAuthenticatorOutputs<>(),
            null,
            null,
            null
        );

        AuthenticationParameters authenticationParameters = new AuthenticationParameters(
            serverProperty,
            credentialRecord,
            null,
            true,
            true
        );

        webAuthnManager.verify(authenticationData, authenticationParameters);

        // セッション管理のためのtokenを発行
        String token = sessionService.generateToken();
        // トークンの有効期限を設定
        Instant expiresAt = Instant.now().plus(Duration.ofDays(30));

        // トークンをセキュリティ向上のためハッシュ化
        try{
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(token.getBytes(StandardCharsets.UTF_8));
            String hexFormat = HexFormat.of().formatHex(bytes);
            SessionEntity sessionEntity = SessionEntity.create(
                passKey.getUserId(),
                hexFormat,
                expiresAt
            );
            sessionMapper.insertSession(sessionEntity);
        }catch(NoSuchAlgorithmException ex){
            throw new IllegalStateException("ハッシュアルゴリズムが見つかりません");
        }

        // 一度検証したチャレンジは再利用しないため、削除処理実行
        int deleteCount = authChallengeMapper.deleteChallenge(challenge.getId());
        // チャレンジが削除されたか確認
        if(deleteCount != 1){
            throw new ChallengeVerificationException("チャレンジは既に使用されています");
        }

        // 認証機から返されたカウンターを保存
        long signCount = authenticationData.getAuthenticatorData().getSignCount();
        int updatedCount = passKeysMapper.updateSignCount(credentialId, signCount);
        if(updatedCount != 1){
            throw new ChallengeVerificationException("パスキー情報の更新に失敗しました");
        }

        // ユーザーへレスポンスするDTOを設定
        LoginResDto loginResDto = new LoginResDto();
        loginResDto.setToken(token);
        loginResDto.setExpiresAt(expiresAt);
        return loginResDto;
    }
}
