package com.rooutcauseai.incident_correlation_service.enitity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.rooutcauseai.incident_correlation_service.enums.IncidentSeverity;
import com.rooutcauseai.incident_correlation_service.enums.IncidentStatus;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "incidents", indexes = {
        @Index(name = "idx_incident_status", columnList = "status"),
        @Index(name = "idx_incident_severity", columnList = "severity"),
        @Index(name = "idx_incident_detected_at", columnList = "detected_at")
})
@Data 
@Builder 
@NoArgsConstructor
@AllArgsConstructor 
public class Incident {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "incident_key", nullable = false, unique = true, length = 100)
    private String incidentKey;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private IncidentSeverity severity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private IncidentStatus status;

    @Column(name = "root_service", length = 150)
    private String rootService;

    @Column(name = "affected_services", columnDefinition = "TEXT")
    private String affectedServices;

    @Column(name = "detected_at", nullable = false)
    private LocalDateTime detectedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
