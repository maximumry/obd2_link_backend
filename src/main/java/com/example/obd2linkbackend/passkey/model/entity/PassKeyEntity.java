package com.example.obd2linkbackend.passkey.model.entity;

import java.time.Instant;

import lombok.Data;

@Data 
public class PassKeyEntity {

    /**
     * passkeysテーブルの主キー
     */
    private Long passkeyId;

    /**
     * 公開鍵に対応した、一意の識別番号
     */
    private byte[] credentialId;

    /**
     * usersテーブルの主キーを参照する外部キー
     */
    private Integer userId;

    /**
     * ユーザーの端末で作成された公開鍵
     */
    private byte[] publicKey;

    /**
     * 認証回数のカウント
     */
    private Long signCount;

    /**
     * 「私のiPhone」のような端末の名称
     */
    private String name;

    /**
     * パスキー作成日時
     */
    private Instant createdAt;

    public static PassKeyEntity create(byte[] credentialId, Integer userId, byte[] publicKey, Long signCount, String name){
        PassKeyEntity entity = new PassKeyEntity();
        entity.credentialId = credentialId;
        entity.userId = userId;
        entity.publicKey = publicKey;
        entity.signCount = signCount;
        entity.name = name;
        return entity;
    }
    
}
