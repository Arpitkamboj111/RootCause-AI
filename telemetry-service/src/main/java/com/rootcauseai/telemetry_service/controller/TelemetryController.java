package com.rootcauseai.telemetry_service.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rootcauseai.telemetry_service.service.TelemetryService;

import lombok.RequiredArgsConstructor;

@RestController 
@RequestMapping("api/v1/telemetry")
@RequiredArgsConstructor 
public class TelemetryController {

    private final TelemetryService telemetryService;



}
