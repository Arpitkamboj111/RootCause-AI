
package com.rootcauseai.apigateway.security;

import java.util.List;
import java.util.function.Predicate;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

@Component
public class RouteValidator {

    private static final List<String> OPEN_API_ENDPOINTS = List.of(
            "/actuator/health",
            "/api/v1/auth/login",
            "/api/v1/auth/register"
    );

    public final Predicate<ServerHttpRequest> isSecured =
            request -> OPEN_API_ENDPOINTS.stream()
                    .noneMatch(path ->
                            request.getURI().getPath().equals(path));
}
