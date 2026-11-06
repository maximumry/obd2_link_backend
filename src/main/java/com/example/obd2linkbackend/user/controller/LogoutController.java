package com.example.obd2linkbackend.user.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.obd2linkbackend.user.service.SessionService;

import lombok.RequiredArgsConstructor;

@RestController 
@RequestMapping("/auth/logout")
@RequiredArgsConstructor 
public class LogoutController {

    private final SessionService sessionService;

    @PostMapping 
    public void deleteSession(@RequestBody String logoutResponseJSON){
        
    }
    
}
