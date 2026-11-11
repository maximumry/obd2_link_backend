package com.example.obd2linkbackend.passkey.model.entity;

import java.time.Instant;

import com.example.obd2linkbackend.passkey.model.enums.ReauthPurpose;

import lombok.Data;

@Data
public class ReauthChallengeEntity {

    /**
     * 再認証チャレンジの主キー
     */
    private Integer id;

    /**
     * 再認証するユーザーのID（usersテーブルの主キー）
     */
    private Integer userId;

    /**
     * チャレンジを発行したログインセッションのID
     */
    private Long sessionId;

    /**
     * 生成したチャレンジ
     */
    private byte[] challenge;

    /**
     * 再認証後に許可する操作
     */
    private ReauthPurpose purpose;

    /**
     * 操作対象のID。パスキー削除ではBase64URL形式のcredentialId。
     * 対象指定が不要な操作ではnull。
     */
    private String targetId;

    /**
     * チャレンジの有効期限
     */
    private Instant expiresAt;

    public static ReauthChallengeEntity create(Integer userId, Long sessionId, byte[] challenge,
            ReauthPurpose purpose, String targetId, Instant expiresAt){
        ReauthChallengeEntity entity = new ReauthChallengeEntity();
        entity.userId = userId;
        entity.sessionId = sessionId;
        entity.challenge = challenge;
        entity.purpose = purpose;
        entity.targetId = targetId;
        entity.expiresAt = expiresAt;
        return entity;
    }
}
