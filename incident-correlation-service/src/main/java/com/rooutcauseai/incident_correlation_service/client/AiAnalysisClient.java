
package com.rooutcauseai.incident_correlation_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.rootcauseai.rootcause_common.dto.AnalysisRequest;



@FeignClient(
    name = "ai-analysis-service",
    url = "http://localhost:8093",
    configuration = FeignConfig.class
)
public interface AiAnalysisClient {

    @PostMapping("/api/v1/analysis")
    Object analyzeIncident(@RequestBody AnalysisRequest request);
}
