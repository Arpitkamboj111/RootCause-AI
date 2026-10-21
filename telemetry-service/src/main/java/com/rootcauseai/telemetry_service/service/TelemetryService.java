package com.rootcauseai.telemetry_service.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.rootcauseai.rootcause_common.exception.ResourceNotFoundException;
import com.rootcauseai.telemetry_service.dto.TelemetryRequest;
import com.rootcauseai.telemetry_service.dto.TelemetryResponse;
import com.rootcauseai.telemetry_service.entity.Telemetry;
import com.rootcauseai.telemetry_service.enums.EventType;
import com.rootcauseai.telemetry_service.enums.Severity;
import com.rootcauseai.telemetry_service.repository.TelemetryRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TelemetryService {

        private final TelemetryRepository telemetryRepository;

        public TelemetryResponse events(TelemetryRequest req) {
                Telemetry telemetry = Telemetry.builder()
                                .serviceName(req.getServiceName())
                                .eventType(req.getEventType())
                                .severity(req.getSeverity())
                                .message(req.getMessage())
                                .eventTimestamp(req.getEventTimestamp())
                                .traceId(req.getTraceId())
                                .environment(req.getEnvironment())
                                .build();
                Telemetry savedTelemetry = telemetryRepository.save(telemetry);
                return mapToResponse(savedTelemetry);
        }

        private TelemetryResponse mapToResponse(Telemetry savedTelemetry) {

                return TelemetryResponse.builder()
                                .id(savedTelemetry.getId())
                                .serviceName(savedTelemetry.getServiceName())
                                .eventType(savedTelemetry.getEventType())
                                .severity(savedTelemetry.getSeverity())
                                .message(savedTelemetry.getMessage())
                                .eventTimestamp(savedTelemetry.getEventTimestamp())
                                .createdAt(savedTelemetry.getCreatedAt())
                                .traceId(savedTelemetry.getTraceId())
                                .environment(savedTelemetry.getEnvironment())
                                .build();
        }

        @Transactional
        public List<TelemetryResponse> ingestBatch(
                        List<TelemetryRequest> requests) {

                if (requests == null || requests.isEmpty()) {
                        throw new ResourceNotFoundException(
                                        "At least one telemetry event is required");
                }

                if (requests.size() > 500) {
                        throw new ResourceNotFoundException(
                                        "Maximum 500 events are allowed per batch");
                }

                List<Telemetry> entities = requests.stream()
                                .map(this::toEntity)
                                .toList();

                return telemetryRepository.saveAll(entities)
                                .stream()
                                .map(this::mapToResponse)
                                .collect(Collectors.toList());
        }

        @Transactional
        public TelemetryResponse getById(Long id) {
                Telemetry telemetry = telemetryRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Telemetry event not found: " + id));

                return mapToResponse(telemetry);
        }

        @Transactional
        public Page<TelemetryResponse> searchEvents(
                        String serviceName,
                        EventType eventType,
                        Severity severity,
                        LocalDateTime fromTime,
                        LocalDateTime toTime,
                        int page,
                        int size) {

                if (page < 0 || size < 1 || size > 200) {
                        throw new ResourceNotFoundException(
                                        "Page must be >= 0 and size must be between 1 and 200");
                }

                if (fromTime != null && toTime != null
                                && fromTime.isAfter(toTime)) {
                        throw new ResourceNotFoundException(
                                        "fromTime cannot be after toTime");
                }

                Pageable pageable = PageRequest.of(
                                page,
                                size,
                                Sort.by(Sort.Direction.DESC, "eventTimestamp"));

                return telemetryRepository.searchEvents(
                                serviceName, eventType, severity,
                                fromTime, toTime, pageable)
                                .map(this::mapToResponse);
        }

        @Transactional
        public List<TelemetryResponse> getByServiceName(String serviceName) {
                return telemetryRepository
                                .findByServiceNameOrderByEventTimestampDesc(serviceName)
                                .stream()
                                .map(this::mapToResponse)
                                .toList();
        }

        @Transactional
        public List<TelemetryResponse> getByTraceId(String traceId) {
                return telemetryRepository
                                .findByTraceIdOrderByEventTimestampDesc(traceId)
                                .stream()
                                .map(this::mapToResponse)
                                .toList();
        }

        private Telemetry toEntity(TelemetryRequest req) {
                return Telemetry.builder()
                                .serviceName(req.getServiceName())
                                .eventType(req.getEventType())
                                .severity(req.getSeverity())
                                .message(req.getMessage())
                                .eventTimestamp(req.getEventTimestamp())
                                .traceId(req.getTraceId())
                                .environment(req.getEnvironment())
                                .build();
        }

}
