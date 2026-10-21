package com.rootcauseai.telemetry_service.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Repository;

import com.rootcauseai.telemetry_service.entity.Telemetry;
import com.rootcauseai.telemetry_service.enums.EventType;
import com.rootcauseai.telemetry_service.enums.Severity;

@Repository
public interface TelemetryRepository extends JpaRepository<Telemetry, Long> {

        List<Telemetry> findByServiceNameOrderByEventTimestampDesc(
                        String serviceName);

        List<Telemetry> findByTraceIdOrderByEventTimestampDesc(
                        String traceId);

        @Query("""
                        SELECT t FROM Telemetry t
                        WHERE
                            (CAST(:serviceName AS string) IS NULL
                                OR t.serviceName = :serviceName)
                        AND
                            (CAST(:eventType AS string) IS NULL
                                OR t.eventType = :eventType)
                        AND
                            (CAST(:severity AS string) IS NULL
                                OR t.severity = :severity)
                        AND
                            (CAST(:fromTime AS timestamp) IS NULL
                                OR t.eventTimestamp >= :fromTime)
                        AND
                            (CAST(:toTime AS timestamp) IS NULL
                                OR t.eventTimestamp <= :toTime)
                        """)
        Page<Telemetry> searchEvents(
                        @Param("serviceName") String serviceName,
                        @Param("eventType") EventType eventType,
                        @Param("severity") Severity severity,
                        @Param("fromTime") LocalDateTime fromTime,
                        @Param("toTime") LocalDateTime toTime,
                        Pageable pageable);

}
