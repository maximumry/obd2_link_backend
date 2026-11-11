package com.example.obd2linkbackend.user.service.impl;

import org.springframework.stereotype.Service;

import com.example.obd2linkbackend.user.service.UserService;

import jakarta.transaction.Transactional;

@Service 
public class UserServiceImpl  implements UserService{

    @Override 
    @Transactional 
    public void deleteUser(Integer userId){
        
    }
    
}
