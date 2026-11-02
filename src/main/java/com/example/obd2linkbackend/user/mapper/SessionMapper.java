package com.example.obd2linkbackend.user.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.obd2linkbackend.user.model.entity.SessionEntity;

@Mapper 
public interface SessionMapper {
    
    void insertSession(SessionEntity entity);

    SessionEntity findValidSession(@Param("tokenHash") String tokenHash);
}
