package com.marketfocus.gateway.controller;

import com.marketfocus.gateway.dto.AuthRequest;
import com.marketfocus.gateway.dto.AuthResponse;
import com.marketfocus.gateway.service.GenericService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
public class GenericController {

    @Autowired
    private GenericService genericService;

    @PostMapping("/auth/login")
    public Mono<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        return genericService.sendRequest(request, "/auth/login");
    }

    @PostMapping("/auth/register")
    public Mono<AuthResponse> register(@Valid @RequestBody AuthRequest request) {
        return genericService.sendRequest(request, "/auth/register");
    }
}
