package com.example.obd2linkbackend.user.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.obd2linkbackend.user.model.entity.UserEntity;

@Mapper 
public interface UserMapper {

    UserEntity getUser(Integer id);

    Integer lockUserById(@Param("userId") Integer userId);
    
    void registerUser(UserEntity userEntity);
        
}
