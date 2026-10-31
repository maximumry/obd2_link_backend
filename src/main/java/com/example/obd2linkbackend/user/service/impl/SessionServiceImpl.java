package com.example.obd2linkbackend.user.service.impl;

import org.springframework.stereotype.Service;

import com.example.obd2linkbackend.user.service.SessionService;

import java.security.SecureRandom;
import java.util.Base64;

@Service
public class SessionServiceImpl implements SessionService {

    private final SecureRandom secureRandom = new SecureRandom();

    public String generateToken(){
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);

        return Base64.getUrlEncoder()
            .withoutPadding()
            .encodeToString(bytes);
    }
    
}
