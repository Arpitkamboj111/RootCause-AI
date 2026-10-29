package com.rootcauseai.rootcause_common.dto;

import java.time.LocalDateTime;



import com.rootcauseai.rootcause_common.enums.EventType;
import com.rootcauseai.rootcause_common.enums.Severity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@AllArgsConstructor 
@NoArgsConstructor 
@Builder 
public class TelemetryResponse {

    private Long id;

    private String serviceName;

    private EventType eventType;

    private Severity severity;

    private String message;

    private LocalDateTime createdAt;

    private LocalDateTime eventTimestamp;

    private String traceId;

    private String environment;


}
