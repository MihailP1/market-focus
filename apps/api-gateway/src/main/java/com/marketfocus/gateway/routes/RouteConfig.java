package com.marketfocus.gateway.routes;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouteConfig {

    @Value("${auth-service.url}")
    private String authServiceUrl;

    @Value("${websocket-service.url}")
    private String websocketServiceUrl;

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("auth_login", r -> r.path("/auth/login")
                        .uri(authServiceUrl + "/auth/login"))
                .route("auth_register", r -> r.path("/auth/register")
                        .uri(authServiceUrl + "/auth/register"))
                .route("websocket_service", r -> r.path("/ws/market/**")
                        .uri(websocketServiceUrl))
                .build();
    }
}
