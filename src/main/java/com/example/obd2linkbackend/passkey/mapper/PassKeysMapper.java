package com.example.obd2linkbackend.passkey.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.obd2linkbackend.passkey.model.entity.PassKeyEntity;

@Mapper 
public interface PassKeysMapper {

    int registerPassKey(PassKeyEntity entity);

    PassKeyEntity selectByCredentialId(@Param("credentialId") byte[] credentialId);

    int updateSignCount(@Param("credentialId") byte[] crendetialId, @Param("signCount") long signCount);
    
}
