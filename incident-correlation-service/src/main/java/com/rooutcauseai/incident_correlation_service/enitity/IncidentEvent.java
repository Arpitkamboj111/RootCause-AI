package com.rooutcauseai.incident_correlation_service.enitity;


import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "incident_events",
    indexes = {
        @Index(
            name = "idx_incident_event_incident_id",
            columnList = "incident_id"
        ),
        @Index(
            name = "idx_incident_event_telemetry_id",
            columnList = "telemetry_event_id"
        )
    },
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_incident_event_telemetry_id",
            columnNames = "telemetry_event_id"
        )
    }
)
@Data 
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IncidentEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "incident_id", nullable = false)
    private Incident incident;

    @Column(name = "telemetry_event_id", nullable = false)
    private Long telemetryEventId;

    @Column(name = "service_name", nullable = false, length = 150)
    private String serviceName;

    @Column(name = "event_timestamp", nullable = false)
    private LocalDateTime eventTimestamp;

    @Column(name = "correlation_reason", columnDefinition = "TEXT")
    private String correlationReason;
}
