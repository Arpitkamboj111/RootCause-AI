package com.rooutcauseai.incident_correlation_service.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.rooutcauseai.incident_correlation_service.dto.CreateIncidentRequest;
import com.rooutcauseai.incident_correlation_service.dto.UpdateIncidentStatusRequest;
import com.rooutcauseai.incident_correlation_service.enitity.Incident;
import com.rooutcauseai.incident_correlation_service.enitity.IncidentEvent;
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
}