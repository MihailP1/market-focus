package com.marketfocus.gateway.service;

import com.marketfocus.gateway.dto.AuthRequest;
import com.marketfocus.gateway.dto.AuthResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class AuthService {

    @Value("${auth-service.url}")
    private String authServiceUrl;

    private final WebClient webClient = WebClient.create();

    public Mono<AuthResponse> authenticate(AuthRequest request) {
        // Логируем URL и тело запроса перед отправкой
        System.out.println("Authenticating with URL: " + authServiceUrl + "/auth/login");
        System.out.println("Request body: " + request);

        return webClient.post()
                .uri(authServiceUrl + "/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .onStatus(status -> status.is4xxClientError(), clientResponse -> {
                    System.out.println("Client error occurred: " + clientResponse.statusCode());
                    return Mono.error(new RuntimeException("Client error: " + clientResponse.statusCode()));
                })
                .onStatus(status -> status.is5xxServerError(), clientResponse -> {
                    System.out.println("Server error occurred: " + clientResponse.statusCode());
                    return Mono.error(new RuntimeException("Server error: " + clientResponse.statusCode()));
                })
                .bodyToMono(AuthResponse.class)
                .doOnTerminate(() -> System.out.println("Authentication attempt completed."))
                .doOnError(error -> System.out.println("Error during authentication: " + error.getMessage()));
    }


    public Mono<AuthResponse> register(AuthRequest request) {
        return webClient.post()
                .uri(authServiceUrl + "/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(AuthResponse.class);
    }
}
