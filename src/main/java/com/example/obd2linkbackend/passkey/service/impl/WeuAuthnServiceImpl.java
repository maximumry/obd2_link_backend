package com.example.obd2linkbackend.passkey.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.obd2linkbackend.user.model.dto.request.UserReqDto;
import com.fasterxml.jackson.databind.ser.std.StdKeySerializers.Default;
import com.webauthn4j.data.PublicKeyCredentialCreationOptions;
import com.webauthn4j.data.PublicKeyCredentialParameters;
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
public class WeuAuthnServiceImpl {

    @Transactional 
    public String generateRegistrationChallenge(HttpSession session, UserReqDto userReqDto){
        Challenge challenge = new DefaultChallenge();
        byte[] challengeBytes = challenge.getValue();

        // 後で検証するため、チャレンジを一時保存
        session.setAttribute("webauthn_challenge", challenge);

        // パスキー登録時に端末へ渡すユーザー情報を作成
        PublicKeyCredentialUserEntity user = new PublicKeyCredentialUserEntity(challengeBytes, userReqDto.getEmail(), userReqDto.getDisplayName());

        // クライアントが使用する公開鍵のアルゴリズムを「ES256」に指定
        List<PublicKeyCredentialParameters> pubKeyCredParams = List.of(new PublicKeyCredentialParameters(
            PublicKeyCredentialType.PUBLIC_KEY,
            COSEAlgorithmIdentifier.ES256));

        PublicKeyCredentialCreationOptions options = new PublicKeyCredentialCreationOptions("localhost", "OBD2 Link", user, pubKeyCredParams);

    }
    
}
