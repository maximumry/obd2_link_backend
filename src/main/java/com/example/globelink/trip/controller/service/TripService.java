package com.example.globelink.trip.controller.service;

import org.springframework.stereotype.Service;

@Service
public interface TripService {

    /**
     * 全走行データの取得
     */
    TripSummaryResponse getTrips();

}
