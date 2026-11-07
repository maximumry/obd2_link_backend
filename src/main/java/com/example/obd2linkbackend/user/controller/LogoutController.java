package com.example.obd2linkbackend.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.obd2linkbackend.auth.BearerTokenResolver;
import com.example.obd2linkbackend.user.service.SessionService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController 
@RequestMapping("/auth/logout")
@RequiredArgsConstructor 
public class LogoutController {

    private final SessionService sessionService;

    @PostMapping 
    public ResponseEntity<Void> deleteSession(HttpServletRequest reqeust){
        BearerTokenResolver resolver = new BearerTokenResolver();
        String token = resolver.resolve(reqeust);

        if(!StringUtils.hasText(token)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        sessionService.deleteSession(token);
        return ResponseEntity.noContent().build();
    }
    
}
