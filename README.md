# OBD2 Link Backend

## プロジェクト概要

iPhoneアプリで、自分の走行履歴と1回分のOBD2データを確認するためのバックエンドです。走行データの保存・閲覧・削除とアカウント管理を扱います。ユーザー認証はパスキーのみを使用します。

## 技術スタック

| 用途 | 技術 |
| --- | --- |
| 言語 | Java 21 |
| フレームワーク | Spring Boot |
| DBアクセス | MyBatis |
| ユーザー認証 | パスキー（WebAuthn / FIDO2） |
| データベース | PostgreSQL |
| ビルド | Maven Wrapper |
| ローカルDB環境 | Docker Compose |

## ローカル起動

前提：JDK 21、Docker Compose、依存ライブラリを取得できるネットワーク。

1. ルートに`.env`を用意します。既存ファイルがある場合は内容を確認して利用してください（Git管理対象外）。

   ```dotenv
   POSTGRES_DB=globelink_dev_db
   POSTGRES_USER=globelink
   POSTGRES_PASSWORD=replace-with-local-password
   POSTGRES_PORT=5432
   ```

2. DBを起動します。

   ```sh
   docker compose up -d postgres
   ```

3. 同じDB接続情報を環境変数に指定して起動します。`.env`はCompose用で、Spring Bootには自動では読み込まれません。

   ```sh
   export SPRING_DATASOURCE_URL='jdbc:postgresql://localhost:5432/globelink_dev_db'
   export SPRING_DATASOURCE_USERNAME='globelink'
   export SPRING_DATASOURCE_PASSWORD='replace-with-local-password'
   ./mvnw spring-boot:run -Dspring-boot.run.profiles=local
   ```

4. `curl http://localhost:8080/actuator/health`で起動状態を確認します。


## ドキュメント

- [要件定義](docs/requirements.md)
- [設計書](docs/architecture.md)
