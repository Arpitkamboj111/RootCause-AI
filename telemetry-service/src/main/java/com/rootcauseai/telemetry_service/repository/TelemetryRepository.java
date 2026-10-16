package com.rootcauseai.telemetry_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rootcauseai.telemetry_service.entity.Telemetry;

@Repository 
public interface TelemetryRepository  extends  JpaRepository<Telemetry , Long>{

}
