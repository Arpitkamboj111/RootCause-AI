package com.rootcauseai.ai_analysis_service.controller;

import java.util.List;
import org.springframework.ai.document.Document;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;


import com.rootcauseai.ai_analysis_service.dto.AnalysisResponse;
import com.rootcauseai.ai_analysis_service.dto.SimilarIncidentResponse;
import com.rootcauseai.ai_analysis_service.entity.IncidentAnalysis;
import com.rootcauseai.ai_analysis_service.service.AiAnalysisService;
import com.rootcauseai.rootcause_common.dto.AnalysisRequest;
import com.rootcauseai.rootcause_common.response.ApiResponse;
import com.rootcauseai.ai_analysis_service.service.IncidentVectorService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/analysis")
@RequiredArgsConstructor
public class AiAnalysisController {

        private final AiAnalysisService aiAnalysisService;
        private final IncidentVectorService incidentVectorService;

        @PostMapping
        public ResponseEntity<AnalysisResponse> analyzeIncident(
                        @Valid @RequestBody AnalysisRequest request) {

                AnalysisResponse response = aiAnalysisService.saveIncident(request);

                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(response);
        }

        @GetMapping("/id")
        public ResponseEntity<ApiResponse<AnalysisResponse>> getIncidentById(@RequestParam Long id) {
                AnalysisResponse response = aiAnalysisService.getIncidentById(id);
                return ResponseEntity.ok(ApiResponse.success("Incident found Successfully", response));
        }

        @GetMapping
        public ResponseEntity<ApiResponse<Page<IncidentAnalysis>>> getIncidents(
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size,
                        @RequestParam(required = false) String serviceName,
                        @RequestParam(required = false) String severity,
                        @RequestParam(required = false) String status) {

                return ResponseEntity.ok(
                                ApiResponse.success("Incidents retrieved successfully",
                                                aiAnalysisService.getIncidents(
                                                                page, size, serviceName, severity, status)));
        }

        @GetMapping("/similar")
        public List<SimilarIncidentResponse> findSimilarIncidents(
                        @RequestParam String query,
                        @RequestParam(defaultValue = "5") int topK) {

                if (query == null || query.isBlank()) {
                        throw new ResponseStatusException(
                                        HttpStatus.BAD_REQUEST,
                                        "Search query cannot be empty");
                }

                if (topK < 1 || topK > 20) {
                        throw new ResponseStatusException(
                                        HttpStatus.BAD_REQUEST,
                                        "topK must be between 1 and 20");
                }

                List<Document> documents = incidentVectorService.findSimilarIncidents(query, topK);

                return documents.stream()
                                .map(document -> new SimilarIncidentResponse(
                                                document.getText(),
                                                document.getMetadata()))
                                .toList();
        }

}
