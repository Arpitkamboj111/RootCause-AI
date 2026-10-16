package com.rootcauseai.telemetry_service.service;

import org.springframework.stereotype.Service;

import com.rootcauseai.telemetry_service.repository.TelemetryRepository;

import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class TelemetryService {

    private final TelemetryRepository telemetryRepository;

}
