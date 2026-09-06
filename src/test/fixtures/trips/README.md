# Tripテスト用データ

プロジェクトのTripSummaryResponseとスプレッドシート「要件定義書」12〜18・31〜53行を基準にした合成データです。位置情報も架空です。

| ケース | tripId末尾 | 件数 | 内容 |
|---|---|---:|---|
| completed | 001 | 600 | 10分、1Hz、終了済み、GPSあり |
| gps-null | 002 | 120 | 2分、終了済み、GPS関連はすべてnull |
| unfinished | 003 | 60 | 未終了、endedAt=null、通信途絶表示用 |

## 今のTrip実装で利用する

開発用DBを選び、00_schema.sql → 01_seed.sql → 02_check.sql の順でSQLを実行してください。テーブルが未定義だったため00は定義案として同梱しています。既存テーブルがある場合はカラム定義を照合してから利用してください。アプリ起動時の自動投入は設定していません。

01は同じキーの行を上書きしません。再実行しても3走行・780件のままです。02のSELECTはDTO用にcamelCaseの列別名を付けており、Mapperを実装するときの参考にできます。telemetryCountは保存値でなくCOUNTで求めます。

現在のTripMapperはusersを参照しているため、このSQLを投入するだけではTrip APIには表示されません。複数走行の一覧を返す場合はMapper・Service・Controllerの戻り値も単一DTOから一覧形式にする必要があります。1件取得はtripIdで絞ってください。TripControllerの@RestController("/api/trips/")はURL指定ではないため、URLは@RequestMappingなどで定義してください。これら既存コードは変更していません。

## 一括登録APIを実装した後

SQL投入とは別の空の開発DBで、requests内の各ケースのJSONを番号順にPOST /api/v1/telemetry/batchesへ送信します。Content-Type: application/jsonと送信用Bearerトークンが必要です。1リクエスト5件で、1Hz・5秒ごとの送信を再現しています。999-end.jsonは最後に送ります。

completedは初回合計acceptedCount=600、同じファイルの再送では合計acceptedCount=0・duplicateCount=600が期待値です。終了のみの応答は両方0です。limit=100の走行データ取得は600件を6ページで取得します。全ケース実行後の最新計測データはunfinishedのsequence=59です。

日時は作成日の前日UTCを基準にしています。正確な値はexpected.jsonを参照してください。APIの30日制限を超えて再利用する場合は、全JSONのstartedAt・endedAt・timestampを同じ日数だけ移動し、空のテストDBを使用してください。

直接SQLの再実行はDB上の重複防止確認です。APIの認証・検証・トランザクションやロガーの再送処理を検証済みという意味ではありません。
