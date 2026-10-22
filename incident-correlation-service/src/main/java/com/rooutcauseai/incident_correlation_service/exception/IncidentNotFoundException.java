package com.rooutcauseai.incident_correlation_service.exception;

public class IncidentNotFoundException extends RuntimeException {

    public IncidentNotFoundException(String message) {
        super(message);
    }
}