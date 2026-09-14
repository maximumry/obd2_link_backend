# 設計書

## システム構成

```text
iPhoneアプリ ── HTTPS / JSON ──┐
                               ├─ Spring Boot ─ MyBatis ─ PostgreSQL
OBD2記録端末 ─ HTTPS / JSON ──┘
```

バックエンドは単一アプリとし、Controller（HTTP・入力検証）→ Service（業務処理・所有者確認）→ Mapper（SQL）に分ける。認証はSpring SecurityとWebAuthn検証ライブラリ、入力検証はBean Validation、DBスキーマ管理はFlywayを使用する。

## DB

| テーブル | 主なカラム |
| --- | --- |
| `users` | `id`、`email`（一意）、`display_name`、`created_at` |
| `sessions` | `id`、`user_id`、`token_hash`、`expires_at` |
| `passkeys` | `credential_id`（主キー）、`user_id`、`public_key`、`sign_count`、`name`、`created_at` |
| `auth_challenges` | `id`、`challenge`、`purpose`、`user_id`（登録時は仮ID）、`session_id`（再認証時）、`registration_data`（登録時のみ）、`expires_at` |
| `reauth_tokens` | `id`、`session_id`、`token_hash`、`purpose`、`target_id`、`expires_at` |
| `devices` | `id`、`user_id`（一意）、`token_hash` |
| `trips` | `trip_id`、`device_id`、`started_at`、`ended_at`、`last_received_at`、`deleted_at` |
| `telemetry` | `device_id`、`trip_id`、`sequence`、`timestamp`、`received_at`、計測値 |

ユーザーに端末、端末に走行、走行に計測値を紐付ける。日時は`timestamptz`で保存する。

計測値は車速（km/h）、回転数（rpm）、スロットル開度（%）、冷却水温・吸気温（℃）、緯度・経度、高度（m）、GPS速度（km/h）。欠損は`null`とする。

`telemetry`の主キーは`(device_id, trip_id, sequence)`とし、再送時の重複を防ぐ。走行一覧用に`trips(device_id, started_at DESC, trip_id DESC)`のインデックスを設ける。

走行削除時は計測値を物理削除し、走行に`deleted_at`を設定する。削除済みIDへの再送は拒否する。退会時は関連データを物理削除する。

## API

ベースパスは`/api/v1`、形式はJSON。「本人」はユーザー用Bearerトークン、「端末」は送信用Bearerトークンで認証する。

| メソッド・パス | 認証 | 内容 |
| --- | --- | --- |
| `POST /auth/register/options` | 不要 | メール・表示名を受け取り、パスキー登録チャレンジを発行 |
| `POST /auth/register/verify` | 不要 | 登録応答を検証し、アカウント・パスキーを作成 |
| `POST /auth/login/options` | 不要 | パスキー認証チャレンジを発行 |
| `POST /auth/login/verify` | 不要 | 署名を検証し、ユーザートークンと有効期限を発行 |
| `POST /auth/logout` | 本人 | 現在のセッションを削除 |
| `POST /auth/reauth/options` | 本人 | 操作用途を指定して再認証チャレンジを発行 |
| `POST /auth/reauth/verify` | 本人 | パスキーを検証し、操作用の再認証トークンを発行 |
| `GET /me/passkeys` | 本人 | 登録パスキーのID・名前・登録日時を取得 |
| `POST /me/passkeys/options` | 本人 | 再認証後、追加登録チャレンジを発行 |
| `POST /me/passkeys/verify` | 本人 | 登録応答を検証し、パスキーを追加 |
| `DELETE /me/passkeys/{credentialId}` | 本人 | 再認証後、パスキーを削除。最後の1件は409 |
| `GET /me` | 本人 | アカウント情報を取得 |
| `PATCH /me` | 本人 | 表示名を更新 |
| `DELETE /me` | 本人 | パスキーで再認証し、退会 |
| `POST /me/device` | 本人 | 端末IDと送信用トークンを発行 |
| `GET /me/device` | 本人 | 登録端末を取得 |
| `POST /me/device/token` | 本人 | パスキーで再認証し、送信用トークンを再発行 |
| `POST /telemetry/batches` | 端末 | 走行情報と計測値を一括保存 |
| `GET /trips` | 本人 | 走行一覧を取得 |
| `GET /trips/{tripId}` | 本人 | 走行概要を取得 |
| `GET /trips/{tripId}/telemetry` | 本人 | 走行の計測値を取得 |
| `DELETE /trips/{tripId}` | 本人 | 走行を削除 |

### 走行データ

概要は`tripId`、`deviceId`、`startedAt`、`endedAt`、`lastReceivedAt`、`status`、`durationSeconds`を返す。`status`は`completed`または`unfinished`とする。

一覧は`limit`と`cursor`を受け取り、`items`と`nextCursor`を返す。開始日時降順・ID降順で取得し、最近の走行カードにも同じAPIを使う。

計測値は`limit`と`afterSequence`を受け取り、`samples`と`nextAfterSequence`を返す。`sequence`昇順とし、次ページがなければカーソルは`null`とする。

受信データは`deviceId`、`tripId`、`startedAt`、`endedAt`、`samples`で構成する。各計測値には`sequence`と`timestamp`を付ける。

- 端末が走行ごとにUUIDの`tripId`を生成する。
- トークンと端末IDの一致を確認し、走行情報と計測値を1トランザクションで保存する。
- 同じ主キーの計測値は上書きせず、`acceptedCount`と`duplicateCount`を返す。
- 終了通知は`endedAt`と空の`samples`で送信できる。確定した開始・終了日時は変更しない。
- 終了後も走行時間内の遅延データを受け入れる。計測日時と走行時間が矛盾する場合は拒否する。
- 端末は送信成功までデータを保持し、通信復旧後に再送する。

### エラー

応答は`{"code":"TRIP_NOT_FOUND","message":"走行が見つかりません"}`形式とする。

400：入力不正、401：認証失敗、403：トークンの用途違反、404：未存在または他人のデータ、409：データ矛盾・重複登録、410：削除済み走行への再送、413：サイズ超過、429：回数制限。

## 認証

- ユーザー認証はパスキーのみ。メールは識別情報として保存し、メールの所有確認や認証・復旧には使用しない。
- 登録時にランダムなユーザーIDを発行し、WebAuthnの`user.id`として使用する。登録成功時にユーザーとパスキーを同一トランザクションで保存する。既存メールへの登録でアカウントを上書き・統合しない。
- チャレンジは暗号学的乱数で生成し、5分有効・1回限りとする。用途・対象ユーザー・セッションに紐付け、検証成功時に原子的に消費する。登録中のメール・表示名はチャレンジに紐付け、有効期限後に破棄する。
- 登録・認証時はチャレンジ、処理種別、許可したOrigin、RP ID、ユーザー確認（UV）を検証する。認証時は保存済み公開鍵で署名を検証し、credential IDとuserHandleが同じユーザーを指すことを確認する。Discoverable Credentialと`userVerification: required`を使用する。
- サーバーは公開鍵を保存し、秘密鍵・生体情報は取得しない。署名カウンターは同期パスキーで増加しない場合も考慮し、カウンターだけで一律に拒否しない。
- ユーザートークンは有効期限30日とし、DBにはハッシュのみ保存する。iPhoneではKeychainに保存する。
- 再認証は現在のユーザーのパスキーで行う。再認証トークンは5分有効・1回限りとし、セッション・操作・対象IDに紐付ける。退会、端末トークン再発行、パスキー追加・削除で検証・消費する。
- パスキーは複数登録できる。最後の1件の削除は同時操作時も拒否し、削除時は全ユーザーセッションを失効する。すべてのパスキーを失った場合、パスキー提供元での復元を利用する。
- 端末トークンはデータ送信専用とし、再発行時に旧トークンを無効化する。
- 全てのデータ操作で所有関係を確認する。認証関連APIに回数制限を設け、DB認証情報は環境変数で管理する。

検証は[WebAuthn仕様](https://www.w3.org/TR/webauthn-3/)に従う。

## 外部サービス・iPhone連携

認証用のメール配信サービスは使用しない。iPhoneはAuthenticationServicesでパスキーを作成・使用する。

RP IDとなるドメインで`/.well-known/apple-app-site-association`を公開し、`webcredentials`にアプリIDを登録する。iPhone側には対応するAssociated Domainsを設定する。RP ID・許可Originはサーバー設定で固定し、実機での認証確認にはHTTPSドメインとアプリの関連付けを用意する。[Appleのパスキー連携資料](https://developer.apple.com/documentation/authenticationservices/connecting-to-a-service-with-passkeys)
