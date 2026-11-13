
package com.rootcauseai.telemetry_service.controller;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


import com.rootcauseai.rootcause_common.enums.EventType;
import com.rootcauseai.rootcause_common.enums.Severity;
import com.rootcauseai.telemetry_service.dto.TelemetryRequest;
import com.rootcauseai.telemetry_service.service.TelemetryService;

import lombok.RequiredArgsConstructor;
import tools.jackson.databind.JsonNode;

@RestController
@RequiredArgsConstructor
public class OtlpTraceController {

    private static final ZoneId ZONE = ZoneId.of("Asia/Kolkata");

    private final TelemetryService telemetryService;

    @PostMapping(
            path = "/v1/traces",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> receiveTraces(@RequestBody JsonNode payload) {

        List<TelemetryRequest> requests = new ArrayList<>();

        JsonNode resourceSpans = payload.path("resourceSpans");

        for (JsonNode resourceSpan : resourceSpans) {

            JsonNode resource = resourceSpan.path("resource");
            JsonNode scopeSpans = resourceSpan.path("scopeSpans");

            String serviceName = getAttribute(resource, "service.name");
            String environment = getAttribute(
                    resource, "deployment.environment.name");

            if (environment == null) {
                environment = getAttribute(resource, "deployment.environment");
            }

            for (JsonNode scopeSpan : scopeSpans) {

                JsonNode spans = scopeSpan.path("spans");

                for (JsonNode span : spans) {

                    String traceId = span.path("traceId").asText();
                    String spanName = span.path("name").asText("unknown-span");

                    String statusCode = span.path("status")
                            .path("code").asText();

                    Severity severity = isError(statusCode)
                            ? Severity.ERROR
                            : Severity.INFO;

                    LocalDateTime eventTimestamp = convertTimestamp(
                            span.path("startTimeUnixNano").asText());

                    String message = "Span: " + spanName
                            + " | Span ID: " + span.path("spanId").asText()
                            + " | Status: " + statusCode;

                    TelemetryRequest request = new TelemetryRequest();

                    request.setServiceName(
                            serviceName != null ? serviceName : "unknown-service");
                    request.setEventType(EventType.TRACE);
                    request.setSeverity(severity);
                    request.setMessage(message);
                    request.setCreatedAt(LocalDateTime.now(ZONE));
                    request.setEventTimestamp(eventTimestamp);
                    request.setTraceId(traceId);
                    request.setEnvironment(
                            environment != null ? environment : "unknown");

                    requests.add(request);
                }
            }
        }

        // Existing batch API supports a maximum of 500 events.
        for (int i = 0; i < requests.size(); i += 500) {

            List<TelemetryRequest> batch = requests.subList(
                    i, Math.min(i + 500, requests.size()));

            telemetryService.ingestBatch(batch);
        }

        return ResponseEntity.accepted().body(
                java.util.Map.of(
                        "message", "OTLP traces received successfully",
                        "receivedSpans", requests.size()));
    }

    private String getAttribute(JsonNode resource, String key) {

        for (JsonNode attribute : resource.path("attributes")) {

            if (key.equals(attribute.path("key").asText())) {

                JsonNode value = attribute.path("value");

                if (value.has("stringValue")) {
                    return value.path("stringValue").asText();
                }

                if (value.has("intValue")) {
                    return value.path("intValue").asText();
                }

                if (value.has("boolValue")) {
                    return value.path("boolValue").asText();
                }
            }
        }

        return null;
    }

    private boolean isError(String statusCode) {
        return "2".equals(statusCode)
                || "STATUS_CODE_ERROR".equals(statusCode);
    }

    private LocalDateTime convertTimestamp(String timestamp) {

        try {
            long nanos = Long.parseLong(timestamp);

            long seconds = nanos / 1_000_000_000L;
            long remainingNanos = nanos % 1_000_000_000L;

            return Instant.ofEpochSecond(seconds, remainingNanos)
                    .atZone(ZONE)
                    .toLocalDateTime();

        } catch (Exception exception) {
            return LocalDateTime.now(ZONE);
        }
    }
}
