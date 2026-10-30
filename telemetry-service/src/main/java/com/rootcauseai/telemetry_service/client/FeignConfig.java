
package com.rootcauseai.telemetry_service.client;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import feign.RequestInterceptor;
import jakarta.servlet.http.HttpServletRequest;

@Configuration
public class FeignConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {

            ServletRequestAttributes attributes =
                    (ServletRequestAttributes) RequestContextHolder
                            .getRequestAttributes();

            if (attributes == null) {
                return;
            }

            HttpServletRequest request = attributes.getRequest();

            // Forward Authorization header, if present
            String authorization = request.getHeader("Authorization");

            if (authorization != null && !authorization.isBlank()) {
                requestTemplate.header("Authorization", authorization);
            }

            // Forward cookies, including accessToken
            String cookie = request.getHeader("Cookie");

            if (cookie != null && !cookie.isBlank()) {
                requestTemplate.header("Cookie", cookie);
            }
        };
    }
}
