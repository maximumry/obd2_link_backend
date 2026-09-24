package com.example.globelink.user.model.dto.request;

import lombok.Data;

@Data 
public class UserReqDto {

    /**
     * メールアドレス
     */
    private String email;

    /**
     * 表示名
     */
    private String displayName;

}
