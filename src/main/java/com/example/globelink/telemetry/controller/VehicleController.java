package com.example.globelink.telemetry.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;


@RestController("/api/v1/telemetry/")
public class VehicleController {

    @GetMapping("{deviceId}")
    public String getTelemetry(@PathVariable String deviceId, @RequestParam String param) {
        return new String();
    }

    
    @PostMapping("{deviceId}/start")
    public String postTelemetry(@PathVariable String deviceId, @RequestParam String param){
        
    }    
}

