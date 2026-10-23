package com.rooutcauseai.incident_correlation_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;

import com.rootcauseai.rootcause_common.security.JwtUtil;

@SpringBootApplication
@Import(JwtUtil.class)
@EnableFeignClients 
public class IncidentCorrelationServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(IncidentCorrelationServiceApplication.class, args);
	}

}
