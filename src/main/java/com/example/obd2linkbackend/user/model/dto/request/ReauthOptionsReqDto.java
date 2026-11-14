package com.example.obd2linkbackend.user.model.dto.request;

import com.example.obd2linkbackend.passkey.model.enums.ReauthPurpose;

import jakarta.validation.constraints.AssertTrue;
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

    @AssertTrue(message = "用途に対する対象IDの指定が正しくありません")
    public boolean isTargetIdValid(){
        if(purpose == null){
            return true; // purposeはアノテーションに判断を任せるため、nullならtrue
        }

        return switch(purpose){
            case PASSKEY_ADD, ACCOUNT_DELETE -> targetId == null;
            case PASSKEY_DELETE, DEVICE_TOKEN_REISSUE -> targetId != null && !targetId.isBlank();
        };
    }
    
}
