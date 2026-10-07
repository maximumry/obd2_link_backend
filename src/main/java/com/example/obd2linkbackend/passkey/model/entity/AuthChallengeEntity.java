package com.example.obd2linkbackend.passkey.model.entity;

import java.time.Instant;

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
    private String purpose;

    /**
     * 登録時のメールアドレス
     */
    private String registrationData;

    /**
     * 有効期限(５分設定)
     */
    private Instant expiresAt;
    
}
