package com.example.obd2linkbackend.user.service;

import org.springframework.stereotype.Service;

import com.example.obd2linkbackend.user.model.entity.SessionEntity;

@Service 
public interface SessionService {
    
    String generateToken();

    SessionEntity findValidSession(String token);
}
