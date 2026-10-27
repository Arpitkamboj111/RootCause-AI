package com.rootcauseai.ai_analysis_service.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.rootcauseai.ai_analysis_service.entity.IncidentAnalysis;

@Repository 
public interface AnalysisRepository extends JpaRepository<IncidentAnalysis, Long>,JpaSpecificationExecutor<IncidentAnalysis> {

    

}
