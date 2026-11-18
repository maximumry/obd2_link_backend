package com.example.obd2linkbackend.user.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.example.obd2linkbackend.user.model.entity.ReauthTokenEntity;

@Mapper
public interface ReauthTokenMapper {

    int insertReauthToken(ReauthTokenEntity entity);
}
