package com.rootcauseai.ai_analysis_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import com.rootcauseai.rootcause_common.security.JwtUtil;

@SpringBootApplication
@Import(JwtUtil.class)
public class AiAnalysisServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AiAnalysisServiceApplication.class, args);
	}

}
