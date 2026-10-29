
package com.rootcauseai.apigateway.security;

import com.rootcauseai.rootcause_common.security.JwtUtil;
import lombok.RequiredArgsConstructor;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private final JwtUtil jwtUtil;
    private final RouteValidator routeValidator;

    @Override
    public Mono<Void> filter(
            ServerWebExchange exchange,
            GatewayFilterChain chain) {

        // Allow explicitly public routes
        if (!routeValidator.isSecured.test(exchange.getRequest())) {
            return chain.filter(exchange);
        }

        String token = null;

        // 1. Read Authorization header
        String authHeader = exchange.getRequest()
                .getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7).trim();
        }

        // 2. Fall back to accessToken cookie
        if (token == null || token.isBlank()) {
            var cookie = exchange.getRequest()
                    .getCookies()
                    .getFirst("accessToken");

            if (cookie != null) {
                token = cookie.getValue();
            }
        }

        // 3. Reject missing token
        if (token == null || token.isBlank()) {
            return unauthorized(exchange);
        }

        // 4. Validate JWT using your existing JwtUtil method
        try {
            if (!jwtUtil.isTokenValid(token)) {
                return unauthorized(exchange);
            }
        } catch (RuntimeException ex) {
            return unauthorized(exchange);
        }

        // 5. Forward validated token to downstream service
        final String validatedToken = token;

        ServerHttpRequest request = exchange.getRequest()
                .mutate()
                .headers(headers ->
                        headers.setBearerAuth(validatedToken))
                .build();

        return chain.filter(
                exchange.mutate()
                        .request(request)
                        .build()
        );
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange) {
        exchange.getResponse()
                .setStatusCode(HttpStatus.UNAUTHORIZED);

        return exchange.getResponse().setComplete();
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
