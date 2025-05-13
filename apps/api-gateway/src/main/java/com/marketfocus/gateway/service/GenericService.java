package com.marketfocus.gateway.service;

import com.marketfocus.gateway.dto.AuthRequest;
import com.marketfocus.gateway.dto.AuthResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class GenericService {

    @Value("${gateway-service.url}")
    private String gatewayUrl; // Базовый URL для запросов

    private final WebClient webClient;

    public GenericService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(gatewayUrl).build();
    }

    public  Mono<AuthResponse> sendRequest(AuthRequest request, String uri) {
        return webClient.post()
                .uri(gatewayUrl + uri)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .onStatus(status -> status.is4xxClientError(), clientResponse -> {
                    return Mono.error(new RuntimeException("Client error: " + clientResponse.statusCode()));
                })
                .onStatus(status -> status.is5xxServerError(), clientResponse -> {
                    return Mono.error(new RuntimeException("Server error: " + clientResponse.statusCode()));
                })
                .bodyToMono(AuthResponse.class)
                .doOnTerminate(() -> System.out.println("Request to " + uri + " completed."))
                .doOnError(error -> System.out.println("Error during request to " + uri + ": " + error.getMessage()));
    }
}
