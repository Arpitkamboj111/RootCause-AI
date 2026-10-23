package com.rooutcauseai.incident_correlation_service.service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
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

        // 1. Fetch the requested telemetry event
        ApiResponse<TelemetryResponse> apiResponse = telemetryClient.getTelemetryEventById(eventId);

        if (apiResponse == null || apiResponse.getData() == null) {
            throw new IllegalStateException(
                    "Telemetry event not found: " + eventId);
        }

        TelemetryResponse telemetry = apiResponse.getData();

        // 2. If already processed, return its existing incident
        var existingEvent = incidentEventRepository
                .findFirstByTelemetryEventId(eventId);

        if (existingEvent.isPresent()) {
            return existingEvent.get().getIncident();
        }

        String traceId = telemetry.getTraceId();

        LocalDateTime eventTime = telemetry.getEventTimestamp();

        if (eventTime == null) {
            eventTime = LocalDateTime.now();
        }

        String serviceName = telemetry.getServiceName();

        // 3. Fetch events belonging to the same trace
        List<TelemetryResponse> relatedEvents = List.of(telemetry);

        if (traceId != null && !traceId.isBlank()) {

            ApiResponse<List<TelemetryResponse>> traceResponse = telemetryClient.getEventsByTraceId(traceId);

            if (traceResponse != null && traceResponse.getData() != null) {
                relatedEvents = traceResponse.getData();
            }
        }

        // 4. Look for an incident already linked to any event in this trace
        Incident incident = null;

        for (TelemetryResponse related : relatedEvents) {

            if (related.getId() == null) {
                continue;
            }

            var linkedEvent = incidentEventRepository
                    .findFirstByTelemetryEventId(related.getId());

            if (linkedEvent.isPresent()) {
                incident = linkedEvent.get().getIncident();
                break;
            }
        }

        // 5. If no matching incident exists, create a new one
        if (incident == null) {

            incident = Incident.builder()
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
        }

        // Update incident severity if the new event is more severe
        IncidentSeverity incomingSeverity = mapSeverity(telemetry.getSeverity());

        if (severityRank(incomingSeverity) > severityRank(incident.getSeverity())) {

            incident.setSeverity(incomingSeverity);
            incident = incidentRepository.save(incident);
        }

        // Update affected services using the incoming event
        updateAffectedServices(incident, serviceName);

        // Save both changes together
        incident = incidentRepository.save(incident);

        // 6. Link all unprocessed events from this trace to the incident
        for (TelemetryResponse related : relatedEvents) {

            if (related.getId() == null
                    || incidentEventRepository
                            .existsByTelemetryEventId(related.getId())) {
                continue;
            }

            LocalDateTime relatedTime = related.getEventTimestamp();

            if (relatedTime == null) {
                relatedTime = LocalDateTime.now();
            }

            if (related.getServiceName() == null
                    || related.getServiceName().isBlank()) {
                continue;
            }

            IncidentEvent incidentEvent = IncidentEvent.builder()
                    .incident(incident)
                    .telemetryEventId(related.getId())
                    .serviceName(related.getServiceName())
                    .eventTimestamp(relatedTime)
                    .correlationReason(
                            traceId == null || traceId.isBlank()
                                    ? "Individual telemetry event processing"
                                    : "Matched by traceId: " + traceId)
                    .build();

            incidentEventRepository.save(incidentEvent);
        }

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

    private int severityRank(IncidentSeverity severity) {

        if (severity == null) {
            return 0;
        }
        return switch (severity) {
            case LOW -> 1;
            case MEDIUM -> 2;
            case HIGH -> 3;
            case CRITICAL -> 4;
        };
    }

    private void updateAffectedServices(
        Incident incident,
        String newServiceName) {

    if (newServiceName == null || newServiceName.isBlank()) {
        return;
    }

    Set<String> services = new LinkedHashSet<>();

    // Existing affected services preserve karo
    if (incident.getAffectedServices() != null
            && !incident.getAffectedServices().isBlank()) {

        Arrays.stream(incident.getAffectedServices().split(","))
                .map(String::trim)
                .filter(name -> !name.isBlank())
                .forEach(services::add);
    }

    // New service add karo; duplicate automatically ignore hoga
    services.add(newServiceName.trim());

    incident.setAffectedServices(String.join(", ", services));
}
}