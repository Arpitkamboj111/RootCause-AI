package com.rooutcauseai.incident_correlation_service.exception;

public class TelemetryEventAlreadyProcessedException
        extends RuntimeException {

    public TelemetryEventAlreadyProcessedException(Throwable cause) {
        super("Telemetry event was processed concurrently.", cause);
    }
}
