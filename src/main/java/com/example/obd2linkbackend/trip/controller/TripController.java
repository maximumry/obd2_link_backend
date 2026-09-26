package com.example.obd2linkbackend.trip.controller;

import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.example.obd2linkbackend.trip.dto.TripSummaryResponse;
import com.example.obd2linkbackend.trip.service.TripService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;


// @RestController("/api/trips/")
@Controller
@RequiredArgsConstructor
public class TripController {

    private final TripService tripService;

    @GetMapping("/")
    public String getMethodName() {
        return "index";
    }
    

    /**
     * 一回の走行データ取得
     * @param tripId
     * @return
     */
    // @GetMapping("/{tripId}")
    // public TripSummaryResponse getTrip(@PathVariable UUID tripId) {
    //     return tripService.getTrip(tripId);
    // }
    
}
