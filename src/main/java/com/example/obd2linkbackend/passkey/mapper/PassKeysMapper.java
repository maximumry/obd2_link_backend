package com.example.obd2linkbackend.passkey.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.example.obd2linkbackend.passkey.model.entity.PassKeyEntity;

@Mapper 
public interface PassKeysMapper {

    void registerPassKey(PassKeyEntity entity);
    
}
