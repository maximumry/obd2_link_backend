package com.example.obd2linkbackend.user.service;

import org.springframework.stereotype.Service;

@Service 
public interface UserService {

    void deleteUser(Integer usreId);
    
}
