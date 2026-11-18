package com.example.obd2linkbackend.user.model.entity;

import java.time.Instant;

import com.example.obd2linkbackend.passkey.model.enums.ReauthPurpose;

import lombok.Data;

@Data 
public class ReauthTokenEntity {

    private Long id;

    private Integer userId;

    private Long sessionId;

    private String tokenHash;

    private ReauthPurpose purpose;

    private String targetId;

    private Instant expiresAt;

    public static ReauthTokenEntity create(Integer userId, Long sessionId, String tokenHash, ReauthPurpose purpose, String targetId, Instant expiresAt){
        ReauthTokenEntity entity = new ReauthTokenEntity();
        entity.userId = userId;
        entity.sessionId = sessionId;
        entity.tokenHash = tokenHash;
        entity.purpose = purpose;
        entity.targetId = targetId;
        entity.expiresAt = expiresAt;
        return entity;
    }
    
}
