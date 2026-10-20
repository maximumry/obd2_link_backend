package com.example.obd2linkbackend.user.model.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data 
public class LoginReqDto {

    /**
     * メールアドレス
     */
    @NotBlank(message = "メールアドレスは必須です")
    @Size(max = 254, message = "メールアドレスは254文字以内で入力して下さい")
    @Email(message = "メールアドレスの形式が正しくありません")
    private String email;
    
}
