package com.example.obd2linkbackend.passkey.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.obd2linkbackend.passkey.model.entity.AuthChallengeEntity;
import com.example.obd2linkbackend.passkey.model.enums.ChallengePurpose;

@Mapper
public interface AuthChallengeMapper {

    void insertChallenge(AuthChallengeEntity entity);

    AuthChallengeEntity selectChallenge(@Param("challenge") byte[] challenge, @Param("purpose") ChallengePurpose purpose);

    int deleteChallenge(@Param("id") Integer id);

}
