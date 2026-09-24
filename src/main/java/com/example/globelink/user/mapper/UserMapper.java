package com.example.globelink.user.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.example.globelink.user.model.dto.request.UserReqDto;

@Mapper 
public interface UserMapper {

    void registerUser(UserReqDto userReqDto);
        
}
