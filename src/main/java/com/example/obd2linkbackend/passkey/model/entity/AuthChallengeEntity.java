package com.example.obd2linkbackend.passkey.model.entity;

import java.time.Instant;

import com.example.obd2linkbackend.passkey.model.enums.ChallengePurpose;

import lombok.Data;

@Data 
public class AuthChallengeEntity {

    /**
     * チャレンジ成功時にユーザーを特定するためのID
     */
    private Integer id;

    /**
     * UUID(仮ユーザーID)
     */
    private byte[] userId;

    /**
     * 生成したchallenge
     */
    private byte[] challenge;

    /**
     * 用途
     */
    private ChallengePurpose purpose;

    /**
     * 登録時のユーザー情報
     */
    private String registrationData;

    /**
     * 有効期限(５分設定)
     */
    private Instant expiresAt;

    public static AuthChallengeEntity create(byte[] userId, byte[] challenge, ChallengePurpose purpose, String registrationData, Instant expiresAt){
        AuthChallengeEntity entity = new AuthChallengeEntity();
        entity.userId = userId;
        entity.challenge = challenge;
        entity.purpose = purpose;
        entity.registrationData = registrationData;
        entity.expiresAt = expiresAt;
        return  entity;
    }
    
}
