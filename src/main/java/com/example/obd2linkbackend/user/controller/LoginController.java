package com.example.obd2linkbackend.user.controller;

import org.springframework.web.bind.annotation.RestController;

import com.example.obd2linkbackend.passkey.service.WebAuthnService;
import com.example.obd2linkbackend.user.model.dto.request.LoginReqDto;
import com.webauthn4j.data.PublicKeyCredentialRequestOptions;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;


@RestController 
@RequiredArgsConstructor 
@RequestMapping("/auth/login")
public class LoginController {

    private final WebAuthnService webAuthnService;

    @PostMapping("/options")
    public PublicKeyCredentialRequestOptions generateLoginOptions(@Valid @RequestBody LoginReqDto dto) {
        return webAuthnService.generateLoginChallenge(dto);
    }
    
    @PostMapping("/verify")
    public void veryfyLogin(@RequestBody String loginResponseJSON) {
        webAuthnService.verifyLoginChallenge(loginResponseJSON);
    }
    
    
}
