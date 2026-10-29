
package com.rootcauseai.apigateway.security;

import java.util.List;
import java.util.function.Predicate;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

@Component
public class RouteValidator {

    private static final List<String> PUBLIC_PATHS = List.of(
            "/actuator/health",
            "/api/v1/auth/login",
            "/api/v1/auth/register"
    );

    public final Predicate<ServerHttpRequest> isSecured =
            request -> {
                String path = request.getURI().getPath();

                return PUBLIC_PATHS.stream()
                        .noneMatch(publicPath ->
                                path.equals(publicPath)
                                || (publicPath.endsWith("/**")
                                    && path.startsWith(
                                        publicPath.substring(
                                            0, publicPath.length() - 3
                                        )
                                    ))
                        );
            };
}
