package com.example.obd2linkbackend.passkey.service.impl;

import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Base64;

import org.springframework.stereotype.Service;

import com.example.obd2linkbackend.passkey.mapper.AuthChallengeMapper;
import com.example.obd2linkbackend.passkey.model.entity.AuthChallengeEntity;
import com.example.obd2linkbackend.passkey.model.enums.ChallengePurpose;
import com.example.obd2linkbackend.passkey.service.WebAuthnService;
import com.example.obd2linkbackend.user.model.dto.request.UserReqDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.std.StdKeySerializers.Default;
import com.webauthn4j.data.PublicKeyCredentialCreationOptions;
import com.webauthn4j.data.PublicKeyCredentialParameters;
import com.webauthn4j.data.PublicKeyCredentialRpEntity;
import com.webauthn4j.data.PublicKeyCredentialType;
import com.webauthn4j.data.client.challenge.Challenge;
import com.webauthn4j.data.client.challenge.DefaultChallenge;
import com.webauthn4j.data.PublicKeyCredentialUserEntity;
import com.webauthn4j.data.attestation.statement.COSEAlgorithmIdentifier;

import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class WeuAuthnServiceImpl implements WebAuthnService{

    private final ObjectMapper objectMapper;
    private final AuthChallengeMapper authChallengeMapper;

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

        // クライアントが使用する公開鍵の種類の指定と、アルゴリズムを指定
        List<PublicKeyCredentialParameters> pubKeyCredParams = List.of(new PublicKeyCredentialParameters(
            PublicKeyCredentialType.PUBLIC_KEY,
            COSEAlgorithmIdentifier.ES256));

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
        return new PublicKeyCredentialCreationOptions(rp, user, challenge, pubKeyCredParams);
    }
}
