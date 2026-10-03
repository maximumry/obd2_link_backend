package com.example.obd2linkbackend.passkey.service;

import com.example.obd2linkbackend.user.model.dto.request.UserReqDto;
import com.webauthn4j.data.PublicKeyCredentialCreationOptions;

import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;

public interface WebAuthnService {

    @Transactional 
    PublicKeyCredentialCreationOptions generateRegistrationChallenge(UserReqDto dto, HttpSession session);
}
