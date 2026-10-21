package com.rootcauseai.telemetry_service.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rootcauseai.rootcause_common.response.ApiResponse;
import com.rootcauseai.telemetry_service.dto.TelemetryRequest;
import com.rootcauseai.telemetry_service.dto.TelemetryResponse;
import com.rootcauseai.telemetry_service.enums.EventType;
import com.rootcauseai.telemetry_service.enums.Severity;
import com.rootcauseai.telemetry_service.service.TelemetryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/v1/telemetry")
@RequiredArgsConstructor
public class TelemetryController {

    private final TelemetryService telemetryService;

    @PostMapping("events")
    public ResponseEntity<ApiResponse<TelemetryResponse>> events(@RequestBody TelemetryRequest req) {
        TelemetryResponse response = telemetryService.events(req);
        return ResponseEntity.ok(ApiResponse.success("Telemetry events  successfully", response));
    }

    @PostMapping("events/batch")
    public ResponseEntity<ApiResponse<List<TelemetryResponse>>> ingestBatch(
            @Valid @RequestBody List<@Valid TelemetryRequest> requests) {

        List<TelemetryResponse> response = telemetryService.ingestBatch(requests);
        return ResponseEntity.ok(ApiResponse.success("Telemetry events saved successfully", response));
    }

    @GetMapping("events/id")
    public ResponseEntity<ApiResponse<TelemetryResponse>> getById(
            @RequestParam  Long id) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Telemetry event retrieved successfully",
                        telemetryService.getById(id)));
    }

    @GetMapping("searchEvents")
    public ResponseEntity<ApiResponse<Page<TelemetryResponse>>> searchEvents(
            @RequestParam(required = false) String serviceName,
            @RequestParam(required = false) EventType eventType,
            @RequestParam(required = false) Severity severity,

            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromTime,

            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toTime,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Page<TelemetryResponse> response = telemetryService.searchEvents(serviceName, eventType, severity, fromTime,
                toTime, page, size);

        return ResponseEntity.ok(ApiResponse.success("Telemetry events retrieved successfully", response));
    }

    @GetMapping("serviceName")
    public ResponseEntity<ApiResponse<List<TelemetryResponse>>> getByService(
            @RequestParam  String serviceName) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Service telemetry events retrieved successfully",
                        telemetryService.getByServiceName(serviceName)));
    }

    @GetMapping("traceId")
    public ResponseEntity<ApiResponse<List<TelemetryResponse>>> getByTrace(
            @RequestParam  String traceId) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Trace telemetry events retrieved successfully",
                        telemetryService.getByTraceId(traceId)));
    }

    @GetMapping("health")
    public ResponseEntity<ApiResponse<Map<String, String>>> health() {

        Map<String, String> data = Map.of(
                "service", "telemetry-service",
                "status", "UP");

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Telemetry service is running", data));
    }

}
