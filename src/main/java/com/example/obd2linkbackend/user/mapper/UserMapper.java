package com.example.obd2linkbackend.user.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.example.obd2linkbackend.user.model.entity.UserEntity;

@Mapper 
public interface UserMapper {

    UserEntity getUser(Integer id);
    
    void registerUser(UserEntity userEntity);
        
}
