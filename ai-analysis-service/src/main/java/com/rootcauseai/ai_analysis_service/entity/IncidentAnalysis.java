package com.rootcauseai.ai_analysis_service.entity;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity 
@Table (name = "incident_analysis")
@Data 
@Builder 
@NoArgsConstructor 
@AllArgsConstructor 
public class IncidentAnalysis {

    @Id 
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (nullable = false)
    private String serviceName;

    @Column (nullable = false, columnDefinition = "TEXT")
    private String errorMessage;

    @Column (columnDefinition = "TEXT")
    private String stackTrace;

    private String severity;

    private String traceId;

    @Column (columnDefinition = "TEXT")
    private String probableRootCause;

    @Column (columnDefinition = "TEXT")
    private String evidence;

    @Column (columnDefinition = "TEXT")
    private String suggestedFix;

    @Column (nullable = false)
    private String status;

    @Column (nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist 
    protected void onCreate() {
        createdAt = Instant.now();

        if (status == null) {
            status = "RECEIVED";
        }
    }
}
