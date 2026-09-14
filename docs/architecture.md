# 設計書

## システム構成

```text
iPhoneアプリ ── HTTPS / JSON ──┐
                               ├─ Spring Boot ─ MyBatis ─ PostgreSQL
OBD2記録端末 ─ HTTPS / JSON ──┘       │
                                      └─ SMTP（パスワード再設定）
```

バックエンドは単一アプリとし、Controller（HTTP・入力検証）→ Service（業務処理・所有者確認）→ Mapper（SQL）に分ける。認証はSpring Security、入力検証はBean Validation、DBスキーマ管理はFlywayを使用する。

## DB

| テーブル | 主なカラム |
| --- | --- |
| `users` | `id`、`email`（一意）、`password_hash`、`display_name`、`created_at` |
| `sessions` | `id`、`user_id`、`token_hash`、`expires_at` |
| `password_reset_tokens` | `id`、`user_id`、`token_hash`、`expires_at` |
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
| `POST /auth/register` | 不要 | メール・パスワード・表示名で登録 |
| `POST /auth/login` | 不要 | トークンと有効期限を取得 |
| `POST /auth/logout` | 本人 | 現在のセッションを削除 |
| `POST /auth/password-reset/request` | 不要 | 再設定メールを送信 |
| `POST /auth/password-reset/confirm` | 不要 | パスワードを更新し、全ユーザーセッションを失効 |
| `GET /me` | 本人 | アカウント情報を取得 |
| `PATCH /me` | 本人 | 表示名を更新 |
| `DELETE /me` | 本人 | パスワードで再認証し、退会 |
| `POST /me/device` | 本人 | 端末IDと送信用トークンを発行 |
| `GET /me/device` | 本人 | 登録端末を取得 |
| `POST /me/device/token` | 本人 | パスワードで再認証し、送信用トークンを再発行 |
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

- メール・パスワードで認証し、パスワードはBCryptでハッシュ化する。
- ユーザートークンは有効期限30日とし、DBにはハッシュのみ保存する。iPhoneではKeychainに保存する。
- 端末トークンはデータ送信専用とし、再発行時に旧トークンを無効化する。
- 全てのデータ操作でユーザーと端末・走行の所有関係を確認する。
- パスワード再設定トークンは30分有効・1回限りとし、登録メールの有無を応答で明かさない。
- 認証関連APIに回数制限を設ける。DB・メールの認証情報は環境変数で管理する。

## 外部サービス

パスワード再設定メールの送信にSMTPを使用する。
