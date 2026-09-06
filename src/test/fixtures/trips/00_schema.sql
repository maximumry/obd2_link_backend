-- 開発用DBで手動実行するテーブル定義案。既存テーブルを変更しません。
CREATE TABLE IF NOT EXISTS trips (
  trip_id uuid PRIMARY KEY,
  device_id varchar(64) NOT NULL,
  started_at timestamptz NOT NULL,
  ended_at timestamptz,
  last_received_at timestamptz NOT NULL,
  UNIQUE(device_id, trip_id),
  CHECK (ended_at IS NULL OR ended_at >= started_at)
);
CREATE TABLE IF NOT EXISTS telemetry (
  device_id varchar(64) NOT NULL,
  trip_id uuid NOT NULL,
  sequence bigint NOT NULL CHECK(sequence BETWEEN 0 AND 9007199254740991),
  timestamp timestamptz NOT NULL,
  received_at timestamptz NOT NULL,
  rpm double precision, speed_kmh double precision, throttle_pct double precision,
  coolant_c double precision, intake_air_c double precision,
  latitude double precision, longitude double precision,
  altitude double precision, gps_speed_kmh double precision,
  PRIMARY KEY(device_id,trip_id,sequence),
  FOREIGN KEY(device_id,trip_id) REFERENCES trips(device_id,trip_id) ON DELETE CASCADE
);
CREATE INDEX IF NOT EXISTS telemetry_trip_sequence_idx ON telemetry(trip_id,sequence);
CREATE INDEX IF NOT EXISTS telemetry_device_timestamp_idx ON telemetry(device_id,timestamp);
