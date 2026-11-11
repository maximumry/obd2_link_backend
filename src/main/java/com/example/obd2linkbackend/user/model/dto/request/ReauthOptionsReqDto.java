package com.example.obd2linkbackend.user.model.dto.request;

import com.example.obd2linkbackend.passkey.model.enums.ReauthPurpose;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReauthOptionsReqDto {

    /**
     * 再認証後に行う操作
     */
    @NotNull(message = "操作用途は必須です")
    private ReauthPurpose purpose;

    /**
     * 操作対象のID
     * パスキー削除の場合は、削除対象のcredentialIdを
     * Base64URL形式で指定する。
     * 対象指定が不要な操作ではnull。
     */
    private String targetId;
    
}
