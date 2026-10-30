package com.rootcauseai.telemetry_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(
    name = "incident-correlation-service",
    url = "http://localhost:8092",configuration = FeignConfig.class
)
public interface IncidentCorrelationClient {

    @PostMapping("/api/v1/incidents/process-event/eventId")
    Object processTelemetryEvent(
        @RequestParam("eventId") Long eventId
    );
}
