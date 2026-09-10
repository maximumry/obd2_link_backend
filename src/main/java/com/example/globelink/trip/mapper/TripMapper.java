package com.example.globelink.trip.mapper;

import java.util.UUID;

import org.apache.ibatis.annotations.Mapper;

import com.example.globelink.trip.dto.TripSummaryResponse;

@Mapper
public interface TripMapper {

    TripSummaryResponse getTrip(UUID tripId);

}
