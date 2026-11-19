package com.example.obd2linkbackend.user.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.obd2linkbackend.passkey.model.enums.ReauthPurpose;
import com.example.obd2linkbackend.user.model.entity.ReauthTokenEntity;

@Mapper
public interface ReauthTokenMapper {

    int insertReauthToken(ReauthTokenEntity entity);

    void consumeReauthToken(
        @Param("tokenHash") String tokenHash,
        @Param("userId") Integer userId,
        @Param("sessionId") Long sessionId,
        @Param("purpose") ReauthPurpose purpose,
        @Param("targetId") String targetId);
}
