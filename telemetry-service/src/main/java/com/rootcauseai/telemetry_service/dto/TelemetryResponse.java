package com.rootcauseai.telemetry_service.dto;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.rootcauseai.telemetry_service.enums.EventType;
import com.rootcauseai.telemetry_service.enums.Severity;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@AllArgsConstructor 
@NoArgsConstructor 
@Builder 
public class TelemetryResponse {

    private Long id;

    private String serviceName;

    private EventType eventType;

    private Severity severity;

    private String message;

    private LocalDateTime createdAt;

    private LocalDateTime eventTimestamp;

    private String traceId;

    private String environment;


}
