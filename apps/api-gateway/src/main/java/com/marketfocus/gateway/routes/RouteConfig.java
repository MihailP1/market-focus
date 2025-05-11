package com.marketfocus.gateway.routes;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouteConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("auth_service", r -> r.path("/auth-service/**")
                        .uri("lb://auth-service"))  // ← нижний регистр
                .route("user_service", r -> r.path("/user-service/**")
                        .uri("lb://user-service"))
                .build();
    }
}

