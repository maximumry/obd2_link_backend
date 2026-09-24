package com.example.globelink.user.model.entity;

import java.time.Instant;

import lombok.Data;

@Data 
public class User {

    /**
     * ユーザーID
     * 主キー
     */
    private Integer id;

    /**
     * メールアドレス
     */
    private String email;

    /**
     * 表示名
     */
    private String displayName;

    /**
     * 作成日時
     */
    private Instant createdAt;
    
}
