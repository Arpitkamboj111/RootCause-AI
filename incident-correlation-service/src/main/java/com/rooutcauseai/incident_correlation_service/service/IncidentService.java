package com.rooutcauseai.incident_correlation_service.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.rootcauseai.rootcause_common.dto.TelemetryResponse;
import com.rootcauseai.rootcause_common.response.ApiResponse;
import com.rooutcauseai.incident_correlation_service.client.TelemetryClient;
import com.rooutcauseai.incident_correlation_service.dto.CreateIncidentRequest;
import com.rooutcauseai.incident_correlation_service.dto.UpdateIncidentStatusRequest;
import com.rooutcauseai.incident_correlation_service.enitity.Incident;
import com.rooutcauseai.incident_correlation_service.enitity.IncidentEvent;
import com.rooutcauseai.incident_correlation_service.enums.IncidentSeverity;
import com.rooutcauseai.incident_correlation_service.enums.IncidentStatus;
import com.rooutcauseai.incident_correlation_service.exception.IncidentNotFoundException;
import com.rooutcauseai.incident_correlation_service.repository.IncidentEventRepository;
import com.rooutcauseai.incident_correlation_service.repository.IncidentRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor 
public class IncidentService {

    private final IncidentRepository incidentRepository;
    private final IncidentEventRepository incidentEventRepository;
    private final TelemetryClient telemetryClient;

    @Transactional 
    public Incident createIncident(CreateIncidentRequest request) {

        if (incidentRepository.existsByIncidentKey(
                request.getIncidentKey())) {
            throw new IllegalArgumentException(
                    "Incident key already exists: "
                            + request.getIncidentKey());
        }

        Incident incident = Incident.builder()
                .incidentKey(request.getIncidentKey())
                .title(request.getTitle())
                .description(request.getDescription())
                .severity(request.getSeverity())
                .status(IncidentStatus.DETECTED)
                .rootService(request.getRootService())
                .affectedServices(request.getAffectedServices())
                .detectedAt(request.getDetectedAt())
                .build();

        return incidentRepository.save(incident);
    }

    public Incident getIncidentById(Long id) {
        return incidentRepository.findById(id)
                .orElseThrow(() -> new IncidentNotFoundException(
                        "Incident not found with ID: " + id));
    }

    public Page<Incident> getAllIncidents(Pageable pageable) {
        return incidentRepository.findAll(pageable);
    }

    @Transactional
    public Incident updateIncidentStatus(
            Long id,
            UpdateIncidentStatusRequest request) {

        Incident incident = getIncidentById(id);
        incident.setStatus(request.getStatus());

        return incidentRepository.save(incident);
    }

    public List<IncidentEvent> getIncidentEvents(Long incidentId) {
        getIncidentById(incidentId);

        return incidentEventRepository
                .findByIncident_IdOrderByEventTimestampAsc(incidentId);
    }


     @Transactional
    public Incident processTelemetryEvent(Long eventId) {

        // Fetch event from Telemetry Service
        ApiResponse<TelemetryResponse> apiResponse =
                telemetryClient.getTelemetryEventById(eventId);

        if (apiResponse == null || apiResponse.getData() == null) {
            throw new IllegalStateException(
                    "Telemetry event not found: " + eventId);
        }

        TelemetryResponse telemetry = apiResponse.getData();

        // Prevent duplicate processing
        if (incidentEventRepository.existsByTelemetryEventId(eventId)) {
            throw new IllegalArgumentException(
                    "Telemetry event already processed: " + eventId);
        }

        LocalDateTime eventTime = telemetry.getEventTimestamp();

        if (eventTime == null) {
            eventTime = LocalDateTime.now();
        }

        String serviceName = telemetry.getServiceName();

        Incident incident = Incident.builder()
                .incidentKey("INC-" + UUID.randomUUID())
                .title("Telemetry incident: " + serviceName)
                .description(telemetry.getMessage())
                .severity(mapSeverity(telemetry.getSeverity()))
                .status(IncidentStatus.DETECTED)
                .rootService(serviceName)
                .affectedServices(serviceName)
                .detectedAt(eventTime)
                .build();

        incident = incidentRepository.save(incident);

        IncidentEvent incidentEvent = IncidentEvent.builder()
                .incident(incident)
                .telemetryEventId(eventId)
                .serviceName(serviceName)
                .eventTimestamp(eventTime)
                .correlationReason("Initial telemetry event processing")
                .build();

        incidentEventRepository.save(incidentEvent);

        return incident;
    }

    private IncidentSeverity mapSeverity(
            com.rootcauseai.rootcause_common.enums.Severity severity) {

        if (severity == null) {
            return IncidentSeverity.MEDIUM;
        }

        return switch (severity.name().toUpperCase()) {
            case "CRITICAL" -> IncidentSeverity.CRITICAL;
            case "HIGH", "ERROR" -> IncidentSeverity.HIGH;
            case "MEDIUM", "WARN", "WARNING" -> IncidentSeverity.MEDIUM;
            default -> IncidentSeverity.LOW;
        };
    }
}