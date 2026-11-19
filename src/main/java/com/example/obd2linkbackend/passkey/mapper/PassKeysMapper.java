package com.example.obd2linkbackend.passkey.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.obd2linkbackend.passkey.model.entity.PassKeyEntity;

@Mapper 
public interface PassKeysMapper {

    int registerPassKey(PassKeyEntity entity);

    PassKeyEntity selectByCredentialId(@Param("credentialId") byte[] credentialId);

    int updateSignCount(@Param("credentialId") byte[] crendetialId, @Param("signCount") long signCount);
    
    boolean existsPassKey(@Param("userId") Integer userId, @Param("credentialId") byte[] credentialId);

    long countPassKeysByUserId(@Param("userId") Integer userId);

    int deletePassKey(@Param("userId") Integer userId, @Param("credentialId") byte[] credentialId);
}
