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
import com.example.obd2linkbackend.passkey.model.entity.AuthChallengeEntity;
import com.example.obd2linkbackend.passkey.model.enums.ChallengePurpose;
import com.example.obd2linkbackend.passkey.service.WebAuthnService;
import com.example.obd2linkbackend.user.model.dto.request.UserReqDto;
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
import com.webauthn4j.data.PublicKeyCredentialUserEntity;
import com.webauthn4j.data.RegistrationData;
import com.webauthn4j.data.RegistrationParameters;
import com.webauthn4j.data.attestation.statement.COSEAlgorithmIdentifier;
import com.webauthn4j.WebAuthnManager;

import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class WeuAuthnServiceImpl implements WebAuthnService{

    private final ObjectMapper objectMapper;
    private final AuthChallengeMapper authChallengeMapper;

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
        RegistrationData RegistrationData = webAuthnManager.parseRegistrationResponseJSON(registrationResponseJSON);

        // サーバー側の検証条件作成
        ServerProperty serverProperty = new ServerProperty(
            new Origin("https://example.com"),
            "example.com",
            new DefaultChallenge(challenge.getChallenge())
        );

        RegistrationParameters registrationParameters = new RegistrationParameters(
            serverProperty,
            PUB_KEY_CRED_PATAMS,
            true, // 生体認証を必須に
            true); // ユーザー操作を必須
            
        Challenge serverChallenge = new DefaultChallenge(challenge.getChallenge());
        boolean matched = MessageDigest.isEqual(challengeBytes, serverChallenge.getValue());
        if(!matched){
            throw new ChallengeVerificationException("チャレンジが一致しません");
        }

        // 
    }
}
