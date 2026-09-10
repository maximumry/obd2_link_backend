package com.example.globelink.trip.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.globelink.trip.dto.TripSummaryResponse;

/**
 * 走行データサービス
 * TripServiceImpl.javaで実装する
 */
@Service
public interface TripService {

    /**
     * 一回の走行データの取得
     */
    TripSummaryResponse getTrip(UUID tripId);

}
