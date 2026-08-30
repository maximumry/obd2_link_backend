package com.example.globelink.trip.service;

import org.springframework.stereotype.Service;

import com.example.globelink.trip.dto.TripSummaryResponse;
import com.example.globelink.trip.mapper.TripMapper;

import lombok.RequiredArgsConstructor;

/**
 * 走行データサービスの実装
 */
@Service
@RequiredArgsConstructor
public class TripServiceImpl implements TripService {

    private final TripMapper tripMapper;

    @Override
    public TripSummaryResponse getTrips() {
        return tripMapper.getTrips();
    }
    
}
