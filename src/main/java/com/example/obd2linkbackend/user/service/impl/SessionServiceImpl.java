package com.example.obd2linkbackend.user.service.impl;

import org.springframework.stereotype.Service;

import com.example.obd2linkbackend.user.mapper.SessionMapper;
import com.example.obd2linkbackend.user.model.entity.SessionEntity;
import com.example.obd2linkbackend.user.service.SessionService;

import lombok.RequiredArgsConstructor;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HexFormat;

@Service
@RequiredArgsConstructor 
public class SessionServiceImpl implements SessionService {

    private final SecureRandom secureRandom = new SecureRandom();
    private final SessionMapper sessionMapper;

    @Override 
    public String generateToken(){
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);

        return Base64.getUrlEncoder()
            .withoutPadding()
            .encodeToString(bytes);
    }

    @Override 
    public SessionEntity findValidSession(String token){
        try{
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(token.getBytes(StandardCharsets.UTF_8));
            String tokenHash = HexFormat.of().formatHex(hash);

            return sessionMapper.findValidSession(tokenHash);
        }catch(NoSuchAlgorithmException ex){
            throw new IllegalStateException("ハッシュアルゴリズムが見つかりませんでした");
        }
    }
    
}
