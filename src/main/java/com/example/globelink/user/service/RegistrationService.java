package com.example.globelink.user.service;

import org.springframework.stereotype.Service;

import com.example.globelink.user.model.dto.request.UserReqDto;

import jakarta.transaction.Transactional;

@Service
public interface RegistrationService {

    @Transactional 
    public void registerUser(UserReqDto userReqDto1);
    
}
