package com.example.globelink.user.service.impl;

import org.springframework.stereotype.Service;

import com.example.globelink.user.mapper.UserMapper;
import com.example.globelink.user.model.dto.request.UserReqDto;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class RegistrationServiceImpl {

    private final UserMapper userMapper;

    @Transactional 
    public void registerUser(UserReqDto userReqDto) {
        userMapper.registerUser(userReqDto);
    }
    
}
