package com.example.globelink.trip.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.globelink.trip.controller.service.TripService;

@RestController("/api/trips/")
public class TripController {

    private final TripService tripService;

    /**
     * 一回の走行データ取得
     * @param tripId
     * @return
     */
    @GetMapping("{trip_id}")
    public String getTrip(@PathVariable String tripId) {
        
    }
    
}
