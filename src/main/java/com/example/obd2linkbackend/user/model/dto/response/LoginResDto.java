package com.example.obd2linkbackend.user.model.dto.response;

import java.time.Instant;

import lombok.Data;

@Data 
public class LoginResDto {

    /**
     * トークン
     */
    private String token;

    /**
     * 有効期限
     */
    private Instant expiresAt;
    
}
