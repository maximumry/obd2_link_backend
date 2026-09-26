package com.example.obd2linkbackend.trip.mapper;

import java.util.UUID;

import org.apache.ibatis.annotations.Mapper;

import com.example.obd2linkbackend.trip.dto.TripSummaryResponse;

@Mapper
public interface TripMapper {

    TripSummaryResponse getTrip(UUID tripId);

}
