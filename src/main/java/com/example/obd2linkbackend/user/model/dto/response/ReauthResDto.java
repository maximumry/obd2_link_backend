package com.example.obd2linkbackend.user.model.dto.response;

import java.time.Instant;

import lombok.Data;

@Data 
public class ReauthResDto {

    private String token;

    private Instant expiresAt;
    
}
