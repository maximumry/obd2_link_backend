package com.example.obd2linkbackend.user.service;

import org.springframework.stereotype.Service;

@Service 
public interface SessionService {
    
    String generateToken();
}
