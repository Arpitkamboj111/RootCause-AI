package com.rootcauseai.ai_analysis_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor 
@NoArgsConstructor 
public class AnalysisResponse {

    private Long id;
    private String serviceName;
    private String status;
    private String message;
}
