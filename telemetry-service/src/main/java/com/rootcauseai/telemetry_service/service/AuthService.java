
package com.rootcauseai.telemetry_service.service;

import java.util.Map;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.rootcauseai.rootcause_common.exception.BusinessException;
import com.rootcauseai.rootcause_common.security.JwtUtil;
import com.rootcauseai.telemetry_service.dto.LoginRequest;
import com.rootcauseai.telemetry_service.dto.LoginResponse;
import com.rootcauseai.telemetry_service.dto.RegisterRequest;
import com.rootcauseai.telemetry_service.dto.RegisterResponse;
import com.rootcauseai.telemetry_service.entity.User;
import com.rootcauseai.telemetry_service.enums.Role;
import com.rootcauseai.telemetry_service.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public LoginResponse login(LoginRequest request) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()));

        String token = jwtUtil.generateToken(
                authentication.getName(),
                Map.of("tokenType", "access"));

        return new LoginResponse(
                token,
                "Bearer",
                86400);
    }

    public RegisterResponse register(RegisterRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException("Username already exists");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email already exists");
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(Role.DEVELOPER)
                .enabled(true)
                .build();

        User savedUser = userRepository.save(user);

        return new RegisterResponse(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail(),
                savedUser.getRole().name(),
                savedUser.isEnabled());
    }
}
