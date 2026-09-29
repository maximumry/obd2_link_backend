package com.example.obd2linkbackend.user.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.obd2linkbackend.passkey.service.WebAuthnService;
import com.example.obd2linkbackend.user.model.dto.request.UserReqDto;
import com.example.obd2linkbackend.user.service.RegistrationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping ("/auth/register")
@RequiredArgsConstructor 
public class RegistrationController {

    private final RegistrationService registrationService;
    private final WebAuthnService webAuthnService;

    @PostMapping("/options")
    public void registerUser(@Valid @RequestBody UserReqDto userReqDto){
        
        registrationService.registerUser(userReqDto);
    }
    
}
