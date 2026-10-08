package com.example.obd2linkbackend.passkey.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.example.obd2linkbackend.passkey.model.entity.AuthChallengeEntity;

@Mapper 
public interface AuthChallengeMapper {

    void insertChallenge(AuthChallengeEntity entity);
    
}
