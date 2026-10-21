package com.rootcauseai.telemetry_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data 
@AllArgsConstructor
@NoArgsConstructor 
public class RegisterResponse {

    private Long id;
    private String username;
    private String email;
    private String role;
    private boolean enabled;
}