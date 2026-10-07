package com.example.obd2linkbackend.passkey.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper 
public class AuthChallengeMapper {

    void insertChallenge(AuthChallengeEntity entity);
    
}
