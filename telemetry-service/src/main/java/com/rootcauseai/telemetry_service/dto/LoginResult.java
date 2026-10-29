
package com.rootcauseai.telemetry_service.dto;

public record LoginResult(
        String accessToken,
        LoginResponse response) {
}