package com.rootcauseai.ai_analysis_service.service;

import org.springframework.stereotype.Service;

import com.rootcauseai.ai_analysis_service.repository.AnalysisRepository;

import lombok.RequiredArgsConstructor;

@Service @RequiredArgsConstructor 
public class AiAnalysisService {

    private final AnalysisRepository analysisRepository;

}
