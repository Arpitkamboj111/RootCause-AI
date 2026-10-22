package com.rooutcauseai.incident_correlation_service.dto;


import com.rooutcauseai.incident_correlation_service.enums.IncidentStatus;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data 
@AllArgsConstructor 
@NoArgsConstructor 
public class UpdateIncidentStatusRequest {

    @NotNull
    private IncidentStatus status;
}
