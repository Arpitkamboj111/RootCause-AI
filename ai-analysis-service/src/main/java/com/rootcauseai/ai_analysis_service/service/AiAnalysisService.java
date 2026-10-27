package com.rootcauseai.ai_analysis_service.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rootcauseai.ai_analysis_service.dto.AnalysisRequest;
import com.rootcauseai.ai_analysis_service.dto.AnalysisResponse;
import com.rootcauseai.ai_analysis_service.entity.IncidentAnalysis;
import com.rootcauseai.ai_analysis_service.repository.AnalysisRepository;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service @RequiredArgsConstructor 
public class AiAnalysisService {

    private final AnalysisRepository analysisRepository;

     @Transactional 
    public AnalysisResponse saveIncident(AnalysisRequest request) {

        IncidentAnalysis incident = IncidentAnalysis.builder()
                .serviceName(request.getServiceName())
                .errorMessage(request.getErrorMessage())
                .stackTrace(request.getStackTrace())
                .severity(request.getSeverity())
                .traceId(request.getTraceId())
                .status("RECEIVED")
                .build();

        IncidentAnalysis saved = analysisRepository.save(incident);

        return AnalysisResponse.builder()
                .id(saved.getId())
                .serviceName(saved.getServiceName())
                .status(saved.getStatus())
                .message("Incident received and saved successfully")
                .build();
    }

}
