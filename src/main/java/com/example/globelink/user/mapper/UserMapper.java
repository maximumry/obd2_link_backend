package com.example.globelink.user.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper 
public interface UserMapper {

    void registerUser(String email, String displayName);
        
}
