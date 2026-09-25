package com.example.globelink.user.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.globelink.user.model.dto.request.UserReqDto;
import com.example.globelink.user.service.RegistrationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping ("/auth/register")
@RequiredArgsConstructor 
public class RegistrationController {

    private final RegistrationService registrationService;

    @PostMapping("/options")
    public void registerUser(@RequestBody UserReqDto userReqDto){
        registrationService.registerUser(userReqDto);
    }
    
}
