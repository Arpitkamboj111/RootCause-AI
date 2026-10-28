
package com.rootcauseai.ai_analysis_service.service;

import java.util.List;
import java.util.Map;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class IncidentVectorService {

        private final VectorStore vectorStore;

        public void storeIncident(
                        Long incidentId,
                        String serviceName,
                        String errorMessage,
                        String stackTrace,
                        String rootCause,
                        String evidence,
                        String suggestedFix) {

                String content = """
                                Incident ID: %s
                                Service: %s
                                Error: %s
                                Stack trace: %s
                                Root cause: %s
                                Evidence: %s
                                Suggested fix: %s
                                """.formatted(
                                incidentId,
                                safe(serviceName),
                                safe(errorMessage),
                                safe(stackTrace),
                                safe(rootCause),
                                safe(evidence),
                                safe(suggestedFix));

                Document document = new Document(
                                content,
                                Map.of(
                                                "incidentId", incidentId,
                                                "serviceName", safe(serviceName)));

                vectorStore.add(List.of(document));
        }

        public List<Document> findSimilarIncidents(String query, int topK) {

                if (query == null || query.isBlank()) {
                        throw new IllegalArgumentException(
                                        "Search query cannot be empty");
                }

                if (topK < 1 || topK > 20) {
                        throw new IllegalArgumentException(
                                        "topK must be between 1 and 20");
                }

                return vectorStore.similaritySearch(
                                        SearchRequest.builder()
                                                .query(query)
                                                .topK(topK)
                                                .similarityThreshold(0.70)
                                                .build());
                                        }

        private String safe(String value) {
                return value == null || value.isBlank()
                                ? "Not provided"
                                : value;
        }
}
