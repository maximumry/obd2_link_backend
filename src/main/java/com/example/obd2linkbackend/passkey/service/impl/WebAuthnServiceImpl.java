package com.example.obd2linkbackend.passkey.service.impl;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Base64;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.obd2linkbackend.passkey.exception.ChallengeVerificationException;
import com.example.obd2linkbackend.passkey.mapper.AuthChallengeMapper;
import com.example.obd2linkbackend.passkey.mapper.PassKeysMapper;
import com.example.obd2linkbackend.passkey.model.entity.AuthChallengeEntity;
import com.example.obd2linkbackend.passkey.model.entity.PassKeyEntity;
import com.example.obd2linkbackend.passkey.model.enums.ChallengePurpose;
import com.example.obd2linkbackend.passkey.service.WebAuthnService;
import com.example.obd2linkbackend.user.mapper.UserMapper;
import com.example.obd2linkbackend.user.model.dto.request.UserReqDto;
import com.example.obd2linkbackend.user.model.entity.UserEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.std.StdKeySerializers.Default;
import com.webauthn4j.data.PublicKeyCredentialCreationOptions;
import com.webauthn4j.data.PublicKeyCredentialParameters;
import com.webauthn4j.data.PublicKeyCredentialRpEntity;
import com.webauthn4j.data.PublicKeyCredentialType;
import com.webauthn4j.data.client.Origin;
import com.webauthn4j.data.client.challenge.Challenge;
import com.webauthn4j.data.client.challenge.DefaultChallenge;
import com.webauthn4j.server.ServerProperty;
import com.webauthn4j.verifier.exception.VerificationException;
import com.webauthn4j.data.PublicKeyCredentialUserEntity;
import com.webauthn4j.data.RegistrationData;
import com.webauthn4j.data.RegistrationParameters;
import com.webauthn4j.data.attestation.statement.COSEAlgorithmIdentifier;
import com.webauthn4j.WebAuthnManager;
import com.webauthn4j.converter.util.ObjectConverter;

import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class WebAuthnServiceImpl implements WebAuthnService{

    private final ObjectMapper objectMapper;
    private final AuthChallengeMapper authChallengeMapper;
    private final UserMapper userMapper;
    private final PassKeysMapper passKeysMapper;

    // クライアントが使用する公開鍵の種類の指定と、アルゴリズムを指定
    private static final List<PublicKeyCredentialParameters> PUB_KEY_CRED_PATAMS =
        List.of(new PublicKeyCredentialParameters(
            PublicKeyCredentialType.PUBLIC_KEY,
            COSEAlgorithmIdentifier.ES256
        ));

    @Transactional 
    public PublicKeyCredentialCreationOptions generateRegistrationChallenge(UserReqDto userReqDto, HttpSession session){
        // リクエストごとのチャレンジを生成
        Challenge challenge = new DefaultChallenge();
        byte[] challengeBytes = challenge.getValue();

        // 後で検証するため、チャレンジを一時保存
        session.setAttribute("webauthn_challenge", challenge);

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
        }catch(JsonProcessingException e){
            throw new RuntimeException(e);
        }

        // サーバー側、公開鍵、ユーザー、チャレンジの4つの情報をまとめたオブジェクトをコントローラーへレスポンス
        return new PublicKeyCredentialCreationOptions(rp, user, challenge, PUB_KEY_CRED_PATAMS);
    }

    @Transactional
    public void verifyChallenge(String registrationResponseJSON, HttpSession session){
        // clientDataJSONからチャレンジを取り出す
        byte[] challengeBytes;
        try{
            JsonNode registrationResponse = objectMapper.readTree(registrationResponseJSON);
            byte[] clientDataJson = Base64.getUrlDecoder().decode(
                registrationResponse.get("response").get("clientDataJSON").asText());
            JsonNode clientData = objectMapper.readTree(clientDataJson);
            challengeBytes = Base64.getUrlDecoder().decode(clientData.get("challenge").asText());
        }catch(IOException e){
            throw new IllegalArgumentException("clientDataJSONの形式が正しくありません", e);
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
            true); // ユーザー操作を必須
        
        try{
            // ユーザーから届いたwebauthnとサーバーで保存してあるチャレンジを検証
            webAuthnManager.verify(registrationData, registrationParameters);
            // 検証成功後、使用済みチャレンジを削除
            authChallengeMapper.deleteChallenge(challenge.getId());

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
}
