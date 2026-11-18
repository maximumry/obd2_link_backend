package com.example.obd2linkbackend.user.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.obd2linkbackend.auth.CustomUserDetails;
import com.example.obd2linkbackend.passkey.service.WebAuthnService;
import com.example.obd2linkbackend.user.model.dto.request.ReauthOptionsReqDto;
import com.example.obd2linkbackend.user.model.dto.response.ReauthResDto;
import com.webauthn4j.data.PublicKeyCredentialRequestOptions;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController 
@RequestMapping("/auth/reauth")
@RequiredArgsConstructor 
public class ReauthController {

    private final WebAuthnService webAuthnService;

    @PostMapping("/options")
    public PublicKeyCredentialRequestOptions generateReauthOptions(@AuthenticationPrincipal CustomUserDetails user, @Valid @RequestBody ReauthOptionsReqDto dto){
        return webAuthnService.generateReauthChallenge(user, dto);
    }

    @PostMapping("/verify")
    public ReauthResDto verifyReauth(@AuthenticationPrincipal CustomUserDetails user, @RequestBody String reauthResponseJSON){
        return webAuthnService.verifyReauthChallenge(user, reauthResponseJSON);
    }
    
}
