package com.rooutcauseai.incident_correlation_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rooutcauseai.incident_correlation_service.enitity.Incident;

@Repository 
public interface IncidentRepository extends JpaRepository<Incident , Long> {

     boolean existsByIncidentKey(String incidentKey);

}
