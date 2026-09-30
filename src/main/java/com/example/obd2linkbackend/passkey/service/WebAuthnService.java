package com.example.obd2linkbackend.passkey.service;

import com.example.obd2linkbackend.user.model.dto.request.UserReqDto;

import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;

public interface WebAuthnService {
    
    @Transactional 
    void generateRegistrationChallenge(HttpSession session, UserReqDto userReqDto);
}
