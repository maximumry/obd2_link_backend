package com.example.obd2linkbackend.user.service.impl;

import org.springframework.stereotype.Service;

import com.example.obd2linkbackend.user.mapper.UserMapper;
import com.example.obd2linkbackend.user.model.dto.request.UserReqDto;
import com.example.obd2linkbackend.user.service.RegistrationService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class RegistrationServiceImpl implements RegistrationService {

    private final UserMapper userMapper;

    @Transactional 
    public void registerUser(UserReqDto userReqDto) {
        userMapper.registerUser(userReqDto);
    }
    
}
