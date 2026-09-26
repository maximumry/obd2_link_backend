package com.example.obd2linkbackend.user.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.example.obd2linkbackend.user.model.dto.request.UserReqDto;

@Mapper 
public interface UserMapper {

    void registerUser(UserReqDto userReqDto);
        
}
