package com.example.obd2linkbackend.passkey.service;

import jakarta.transaction.Transactional;

public interface WebAuthnService {
    
    @Transactional 
    void generateRegistrationChallenge();
}
