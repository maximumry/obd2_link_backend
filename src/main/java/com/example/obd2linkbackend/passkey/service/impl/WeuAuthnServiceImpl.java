package com.example.obd2linkbackend.passkey.service.impl;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Base64;

import org.springframework.stereotype.Service;

import com.example.obd2linkbackend.passkey.service.WebAuthnService;
import com.example.obd2linkbackend.user.model.dto.request.UserReqDto;
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

    @Transactional 
    public PublicKeyCredentialCreationOptions generateRegistrationChallenge(UserReqDto userReqDto, HttpSession session){
        // リクエストごとのチャレンジを生成
        Challenge challenge = new DefaultChallenge();
        byte[] challengeBytes = challenge.getValue();

        // 後で検証するため、チャレンジを一時保存
        session.setAttribute("webauthn_challenge", challenge);

        /**
         * パスキー登録時に端末へ渡すユーザー情報を作成
         * コンストラクターの第一引数はbyteの配列指定のため変換してる
        **/
        byte[] userIdBytes = ByteBuffer.allocate(4).putLong(userReqDto.getId()).array();
        PublicKeyCredentialUserEntity user = new PublicKeyCredentialUserEntity(userIdBytes, userReqDto.getEmail(), userReqDto.getDisplayName());

        // クライアントが使用する公開鍵の種類の指定と、アルゴリズムを指定
        List<PublicKeyCredentialParameters> pubKeyCredParams = List.of(new PublicKeyCredentialParameters(
            PublicKeyCredentialType.PUBLIC_KEY,
            COSEAlgorithmIdentifier.ES256));

        // ユーザーにパスキー登録してもらう時のサーバー側情報を定義
        PublicKeyCredentialRpEntity rp = new PublicKeyCredentialRpEntity("localhost", "OBD2 Link");

        /**
         * のちにチャレンジが成功してるか検証のために、チャレンジとuserIdをDBへ保存
         * そのためにBase64URLへ変換
         */
        bytesToEncode(userIdBytes);
        bytesToEncode(challengeBytes);

        // サーバー側、公開鍵、ユーザー、チャレンジの4つの情報をまとめたオブジェクトをコントローラーへレスポンス
        return new PublicKeyCredentialCreationOptions(rp, user, challenge, pubKeyCredParams);
    }
    
    private String bytesToEncode(byte[] bytes){
        return Base64.getUrlEncoder()
            .withoutPadding()
            .encodeToString(bytes);
    }
}
