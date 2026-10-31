package com.example.obd2linkbackend.passkey.service;

import com.example.obd2linkbackend.user.model.dto.request.LoginReqDto;
import com.example.obd2linkbackend.user.model.dto.request.UserReqDto;
import com.example.obd2linkbackend.user.model.dto.response.LoginResDto;
import com.webauthn4j.data.PublicKeyCredentialCreationOptions;
import com.webauthn4j.data.PublicKeyCredentialRequestOptions;

import jakarta.transaction.Transactional;

public interface WebAuthnService {

    @Transactional 
    PublicKeyCredentialCreationOptions generateRegistrationChallenge(UserReqDto dto);

    @Transactional 
    void verifyChallenge(String registrationResponseJSON);

    @Transactional 
    PublicKeyCredentialRequestOptions generateLoginChallenge(LoginReqDto dto);

    @Transactional 
    LoginResDto verifyLoginChallenge(String loginResponseJSON);
}
