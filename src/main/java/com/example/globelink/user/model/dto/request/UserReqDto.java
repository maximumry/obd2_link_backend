package com.example.globelink.user.model.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data 
public class UserReqDto {

    /**
     * メールアドレス
     */
    @NotBlank(message = "メールアドレスは必須です")
    @Size(max = 254, message = "メールアドレスは254文字以内で入力して下さい")
    @Email(message = "メールアドレスの形式が正しくありません")
    private String email;

    /**
     * 表示名
     */
    @NotBlank(message = "ユーザー名は必須です")
    @Size(max = 64, message = "ユーザー名は64文字以内で入力して下さい")
    private String displayName;

}
