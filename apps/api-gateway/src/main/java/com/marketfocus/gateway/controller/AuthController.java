package com.marketfocus.gateway.controller;

import com.marketfocus.gateway.dto.AuthRequest;
import com.marketfocus.gateway.dto.AuthResponse;
import com.marketfocus.gateway.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public Mono<AuthResponse> login(@Valid  @RequestBody AuthRequest request) {
        System.out.println("request: " + request);
        return authService.authenticate(request);
    }

    @PostMapping("/register")
    public Mono<AuthResponse> register(@Valid @RequestBody AuthRequest request) {
        return authService.register(request);
    }
}
