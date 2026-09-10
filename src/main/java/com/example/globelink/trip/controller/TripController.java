package com.example.globelink.trip.controller;

import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.example.globelink.trip.dto.TripSummaryResponse;
import com.example.globelink.trip.service.TripService;

import lombok.RequiredArgsConstructor;

@RestController("/api/trips/")
@RequiredArgsConstructor
public class TripController {

    private final TripService tripService;

    /**
     * 一回の走行データ取得
     * @param tripId
     * @return
     */
    @GetMapping("/{tripId}")
    public TripSummaryResponse getTrip(@PathVariable UUID tripId) {
        return tripService.getTrip(tripId);
    }
    
}
