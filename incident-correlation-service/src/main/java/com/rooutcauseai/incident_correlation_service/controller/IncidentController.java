package com.rooutcauseai.incident_correlation_service.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.rooutcauseai.incident_correlation_service.dto.CreateIncidentRequest;
import com.rooutcauseai.incident_correlation_service.dto.UpdateIncidentStatusRequest;
import com.rooutcauseai.incident_correlation_service.enitity.Incident;
import com.rooutcauseai.incident_correlation_service.enitity.IncidentEvent;
import com.rooutcauseai.incident_correlation_service.service.IncidentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/incidents")
@RequiredArgsConstructor
@Validated
public class IncidentController {

        private final IncidentService incidentService;

        @PostMapping("create")
        public ResponseEntity<Incident> createIncident(
                        @Valid @RequestBody CreateIncidentRequest request) {

                Incident incident = incidentService.createIncident(request);

                return ResponseEntity.status(HttpStatus.CREATED).body(incident);
        }

        @GetMapping("id")
        public ResponseEntity<Incident> getIncidentById(
                        @RequestParam Long id) {

                return ResponseEntity.ok(
                                incidentService.getIncidentById(id));
        }

        @GetMapping
        public ResponseEntity<Page<Incident>> getAllIncidents(
                        @PageableDefault(size = 20, sort = "detectedAt") Pageable pageable) {

                return ResponseEntity.ok(
                                incidentService.getAllIncidents(pageable));
        }

        @PatchMapping("id/status")
        public ResponseEntity<Incident> updateIncidentStatus(
                        @RequestParam Long id,
                        @Valid @RequestBody UpdateIncidentStatusRequest request) {

                return ResponseEntity.ok(
                                incidentService.updateIncidentStatus(id, request));
        }

        @GetMapping("id/events")
        public ResponseEntity<List<IncidentEvent>> getIncidentEvents(
                        @RequestParam Long id) {

                return ResponseEntity.ok(
                                incidentService.getIncidentEvents(id));
        }

        @GetMapping("process-event/eventId")
        public ResponseEntity<Incident> processTelemetryEvent(
                        @RequestParam Long eventId) {

                Incident incident = incidentService.processTelemetryEvent(eventId);

                return ResponseEntity.ok(incident);
        }
}
