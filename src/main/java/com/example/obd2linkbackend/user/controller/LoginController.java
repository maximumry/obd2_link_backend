package com.example.obd2linkbackend.user.controller;

import org.springframework.web.bind.annotation.RestController;

import com.example.obd2linkbackend.user.model.dto.request.LoginReqDto;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController 
@RequiredArgsConstructor 
public class LoginController {

    @PostMapping("/options")
    public String generateLoginOptions(@Valid @RequestBody LoginReqDto dto) {
        //TODO: process POST request
        
        return entity;
    }
    
    
}
