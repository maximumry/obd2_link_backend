package com.example.obd2linkbackend.passkey.service.impl;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ser.std.StdKeySerializers.Default;
import com.webauthn4j.data.client.challenge.Challenge;
import com.webauthn4j.data.client.challenge.DefaultChallenge;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class WeuAuthnServiceImpl {

    @Transactional 
    public String generateRegistrationChallenge(){
        Challenge challenge = new DefaultChallenge();
        byte[] challengeBytes = challenge.getValue();
    }
    
}
