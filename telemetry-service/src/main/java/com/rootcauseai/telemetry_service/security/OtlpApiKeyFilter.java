
package com.rootcauseai.telemetry_service.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OtlpApiKeyFilter extends OncePerRequestFilter {

    @Value("${rootcause.otlp.api-key}")
    private String configuredApiKey;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !"/v1/traces".equals(request.getServletPath());
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String apiKey = request.getHeader("X-OTLP-API-KEY");

        if (configuredApiKey == null
                || configuredApiKey.isBlank()
                || apiKey == null
                || !java.security.MessageDigest.isEqual(
                        configuredApiKey.getBytes(java.nio.charset.StandardCharsets.UTF_8),
                        apiKey.getBytes(java.nio.charset.StandardCharsets.UTF_8))) {

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write(
                    "{\"status\":401,\"message\":\"Invalid OTLP API key\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
