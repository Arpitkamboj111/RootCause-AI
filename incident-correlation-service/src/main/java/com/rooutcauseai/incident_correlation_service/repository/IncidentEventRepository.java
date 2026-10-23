package com.rooutcauseai.incident_correlation_service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rooutcauseai.incident_correlation_service.enitity.IncidentEvent;

public interface IncidentEventRepository  extends  JpaRepository<IncidentEvent ,Long>{

    List<IncidentEvent> findByIncident_IdOrderByEventTimestampAsc(
            Long incidentId);

    boolean existsByTelemetryEventId(Long telemetryEventId);

    Optional<IncidentEvent> findFirstByTelemetryEventId(Long telemetryEventId);
 
}
