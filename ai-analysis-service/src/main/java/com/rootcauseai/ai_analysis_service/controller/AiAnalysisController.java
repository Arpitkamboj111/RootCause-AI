package com.rootcauseai.ai_analysis_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rootcauseai.ai_analysis_service.dto.AnalysisRequest;
import com.rootcauseai.ai_analysis_service.dto.AnalysisResponse;
import com.rootcauseai.ai_analysis_service.service.AiAnalysisService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController 
@RequestMapping("/api/v1/analysis")
@RequiredArgsConstructor 
public class AiAnalysisController {

    private final AiAnalysisService aiAnalysisService;

    @PostMapping
    public ResponseEntity<AnalysisResponse> analyzeIncident(
            @Valid @RequestBody AnalysisRequest request) {

        AnalysisResponse response =aiAnalysisService.saveIncident(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

}
