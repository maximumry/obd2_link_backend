package com.example.globelink.trip.dto;

import java.time.Instant;
import java.util.UUID;

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
    private UUID tripId;

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
     * 最後に受信した日時
     */
    private Instant lastReceivedAt;

}
