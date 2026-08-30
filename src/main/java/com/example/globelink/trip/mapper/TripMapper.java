package com.example.globelink.trip.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.example.globelink.trip.dto.TripSummaryResponse;

@Mapper
public interface TripMapper {

    TripSummaryResponse getTrips();

}
