package com.example.obd2linkbackend.trip.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.obd2linkbackend.trip.dto.TripSummaryResponse;
import com.example.obd2linkbackend.trip.mapper.TripMapper;

import lombok.RequiredArgsConstructor;

/**
 * 走行データサービスの実装
 */
@Service
@RequiredArgsConstructor
public class TripServiceImpl implements TripService {

    private final TripMapper tripMapper;

    @Override
    public TripSummaryResponse getTrip(UUID tripId) {
        return tripMapper.getTrip(tripId);
    }
    
}
