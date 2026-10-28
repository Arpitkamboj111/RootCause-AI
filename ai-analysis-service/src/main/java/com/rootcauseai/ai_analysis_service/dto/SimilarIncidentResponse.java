package com.rootcauseai.ai_analysis_service.dto;

import java.util.Map;

public record SimilarIncidentResponse(
        String content,
        Map<String, Object> metadata
) {
}
