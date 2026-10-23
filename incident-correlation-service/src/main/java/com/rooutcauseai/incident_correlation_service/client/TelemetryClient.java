package com.rooutcauseai.incident_correlation_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.rootcauseai.rootcause_common.dto.TelemetryResponse;
import com.rootcauseai.rootcause_common.response.ApiResponse;

@FeignClient(
        name = "telemetry-service",url = "http://localhost:8091", configuration = FeignConfig.class
)
public interface TelemetryClient {

    @GetMapping ("/api/v1/telemetry/events/id")
    ApiResponse<TelemetryResponse> getTelemetryEventById(@RequestParam ("id") Long id);

}
