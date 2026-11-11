package com.example.obd2linkbackend.passkey.model.enums;

public enum ReauthPurpose {
    ACCOUNT_DELETE,  //アカウント削除を許可する
    DEVICE_TOKEN_REISSUE,  //端末の送信用トークンの再発行を許可
    PASSKEY_ADD,  //パスキーの追加登録を許可する
    PASSKEY_DELETE  //指定したパスキーの削除を許可する
}
