package com.example.obd2linkbackend.user.model.entity;

import java.time.Instant;

import lombok.Data;

@Data 
public class SessionEntity {

    /**
     * セッションテーブルの主キー
     */
    private Long sessionId;

    /**
     * usersテーブルの主キーを参照する外部キー
     */
    private Integer userId;

    /**
     * トークンのハッシュ化
     */
    private String tokenHash;

    /**
     * 作製日時
     */
    private Instant expiresAt;

    public static SessionEntity create(Integer userId, String tokenHash, Instant expiresAt){
        SessionEntity entity = new SessionEntity();
        entity.userId = userId;
        entity.tokenHash = tokenHash;
        entity.expiresAt = expiresAt;
        return entity;
    }
    
}
