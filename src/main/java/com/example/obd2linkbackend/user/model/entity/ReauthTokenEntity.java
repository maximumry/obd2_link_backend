package com.example.obd2linkbackend.user.model.entity;

import java.time.Instant;

import com.example.obd2linkbackend.passkey.model.enums.ReauthPurpose;

import lombok.Data;

@Data 
public class ReauthTokenEntity {

    private Integer id;

    private Integer userId;

    private Long sessionId;

    private String tokenHash;

    private ReauthPurpose purpose;

    private String targetId;

    private Instant expiresAt;
    
}
