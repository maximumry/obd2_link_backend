package com.example.obd2linkbackend.trip.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.obd2linkbackend.trip.dto.TripSummaryResponse;

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
