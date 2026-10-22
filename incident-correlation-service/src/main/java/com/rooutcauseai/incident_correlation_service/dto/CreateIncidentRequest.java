package com.rooutcauseai.incident_correlation_service.dto;


import java.time.LocalDateTime;

import com.rooutcauseai.incident_correlation_service.enums.IncidentSeverity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data 
@AllArgsConstructor 
@NoArgsConstructor 
public class CreateIncidentRequest {

    @NotBlank
    @Size(max = 100)
    private String incidentKey;

    @NotBlank
    @Size(max = 200)
    private String title;

    private String description;

    @NotNull
    private IncidentSeverity severity;

    private String rootService;

    private String affectedServices;

    @NotNull
    private LocalDateTime detectedAt;
}
