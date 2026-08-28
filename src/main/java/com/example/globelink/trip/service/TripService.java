package com.example.globelink.trip.service;

import org.springframework.stereotype.Service;

import com.example.globelink.trip.dto.TripSummaryResponse;

@Service
public interface TripService {

    /**
     * 全走行データの取得
     */
    TripSummaryResponse getTrips();

}
