package com.example.obd2linkbackend.user.service;

import org.springframework.stereotype.Service;

import com.example.obd2linkbackend.user.model.entity.UserEntity;

import jakarta.transaction.Transactional;

@Service
public interface RegistrationService {

    @Transactional 
    public void registerUser(UserEntity userEntity);
    
}
