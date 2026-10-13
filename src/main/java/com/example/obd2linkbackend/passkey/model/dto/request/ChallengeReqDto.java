package com.example.obd2linkbackend.passkey.model.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * パスキー登録応答を受け取るDTO
 * ChallengeReqDto
 */
@Data
public class ChallengeReqDto {

    /**
     * クレデンシャルID(Base64URL)
     */
    @NotBlank(message = "クレデンシャルIDは必須です")
    private String id;

    /**
     * クレデンシャルID(Base64URL・生データ)
     */
    @NotBlank(message = "クレデンシャルIDは必須です")
    private String rawId;

    /**
     * クレデンシャルの種類(public-key)
     */
    @NotBlank(message = "クレデンシャルの種類は必須です")
    private String type;

    /**
     * 認証器からの応答
     */
    @NotNull(message = "応答データは必須です")
    @Valid
    private AuthenticatorResponse response;

    /**
     * 認証器からの応答データ
     */
    @Data
    public static class AuthenticatorResponse {

        /**
         * クライアントデータ(Base64URL・JSON)
         * 検証用のチャレンジが含まれる
         */
        @NotBlank(message = "clientDataJSONは必須です")
        private String clientDataJSON;

        /**
         * 公開鍵などを含む証明オブジェクト(Base64URL)
         */
        @NotBlank(message = "attestationObjectは必須です")
        private String attestationObject;

    }

}
