package com.example.globelink.trip.dto;

import java.time.Instant;

import lombok.Data;

/**
 * 走行データのサマリーを返すDTO
 * TripSummaryResponse
 */
@Data
public class TripSummaryResponse {

    /**
     * 走行データのID
     */
    private String tripId;

    /**
     * デバイスID
     */
    private String deviceId;

    /**
     * 開始日時
     */
    private Instant startedAt;

    /**
     * 終了日時
     */
    private Instant endedAt;

    /**
     * テレメトリデータ数
     */
    private long telemetryCount;
    
}
