package com.example.globelink.user.service;

import org.springframework.stereotype.Service;

import com.example.globelink.user.mapper.UserMapper;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor 
public class RegistrationService {

    private final UserMapper userMapper;

    @Transactional 
    public void registerUser(String email, String displayName){
        userMapper.registerUser(email, displayName);
    }
    
}
