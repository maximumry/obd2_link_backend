package com.example.obd2linkbackend.user.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.example.obd2linkbackend.user.model.entity.UserEntity;

@Mapper 
public interface UserMapper {

    void registerUser(UserEntity userEntity);
        
}
