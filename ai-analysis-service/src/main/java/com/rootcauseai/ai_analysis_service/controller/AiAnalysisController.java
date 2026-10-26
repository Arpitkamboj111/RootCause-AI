package com.rootcauseai.ai_analysis_service.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rootcauseai.ai_analysis_service.service.AiAnalysisService;

import lombok.RequiredArgsConstructor;

@RestController 
@RequestMapping("/api/ai-analysis")
@RequiredArgsConstructor 
public class AiAnalysisController {

    private final AiAnalysisService aiAnalysisService;

}
