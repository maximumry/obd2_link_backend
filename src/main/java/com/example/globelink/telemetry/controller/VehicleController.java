package com.example.globelink.telemetry.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
public class VehicleController {

    @GetMapping("telemetry")
    public String getTelemetry(@RequestParam String param) {
        return new String();
    }
    
    
}

