package com.rootcauseai.ai_analysis_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor 
@NoArgsConstructor 
public class AnalysisRequest {

    @NotBlank
    private String serviceName;

    @NotBlank
    private String errorMessage;

    private String stackTrace;
    private String severity;
    private String traceId;
}
