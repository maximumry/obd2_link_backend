package com.example.obd2linkbackend.passkey.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.obd2linkbackend.auth.CustomUserDetails;

@RestController
@RequestMapping("/me/passkeys")
public class PasskeyController {

    @DeleteMapping("/{credentialId}")
    public ResponseEntity<Void> deletePasskey(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable("credentialId") String credentialId,
            @RequestHeader("X-Reauth-Token") String reauthToken) {
        // 再認証トークンの消費とパスキー削除をサービスに接続するまで未実装を返す
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }
}
