package com.rootcauseai.telemetry_service.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.rootcauseai.telemetry_service.enums.EventType;
import com.rootcauseai.telemetry_service.enums.Severity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "telemetry_events", indexes = {
        @Index(name = "idx_telemetry_service_name", columnList = "service_name"),
        @Index(name = "idx_telemetry_event_type", columnList = "event_type"),
        @Index(name = "idx_telemetry_severity", columnList = "severity"),
        @Index(name = "idx_telemetry_timestamp", columnList = "event_timestamp"),
        @Index(name = "idx_telemetry_trace_id", columnList = "trace_id")
})
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Telemetry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "service_name", nullable = false, length = 150)
    private String serviceName;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, length = 20)
    private EventType eventType;

    @Enumerated(EnumType.STRING)
    @Column(name = "severity", nullable = false, length = 20)
    private Severity severity;

    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(name = "created_at", nullable = false , updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "event_timestamp", nullable = false)
    private LocalDateTime eventTimestamp;

    @Column(name = "trace_id", length = 100)
    private String traceId;

    @Column(name = "environment", nullable = false, length = 30)
    private String environment;

}