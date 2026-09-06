-- 期待値: 末尾001=600件、002=120件、003=60件。003のみendedAtがNULL。
SELECT t.trip_id AS "tripId", t.device_id AS "deviceId",
       t.started_at AS "startedAt", t.ended_at AS "endedAt",
       t.last_received_at AS "lastReceivedAt", count(m.sequence) AS "telemetryCount"
FROM trips t LEFT JOIN telemetry m ON m.trip_id=t.trip_id AND m.device_id=t.device_id
WHERE t.trip_id IN ('550e8400-e29b-41d4-a716-446655440001','550e8400-e29b-41d4-a716-446655440002','550e8400-e29b-41d4-a716-446655440003')
GROUP BY t.trip_id ORDER BY t.started_at DESC,t.trip_id DESC;
