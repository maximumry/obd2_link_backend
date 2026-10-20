package com.example.obd2linkbackend.user.service.impl;

import org.springframework.stereotype.Service;

import com.example.obd2linkbackend.user.mapper.UserMapper;
import com.example.obd2linkbackend.user.model.entity.UserEntity;
import com.example.obd2linkbackend.user.service.RegistrationService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class RegistrationServiceImpl implements RegistrationService {

    private final UserMapper userMapper;

    @Transactional 
    public void registerUser(UserEntity userEntity) {
        userMapper.registerUser(userEntity);
    }
    
}
