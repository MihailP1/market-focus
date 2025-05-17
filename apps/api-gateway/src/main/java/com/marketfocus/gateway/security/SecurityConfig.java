package com.marketfocus.gateway.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.reactive.CorsWebFilter;
import java.util.Arrays;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))  // Указываем источник конфигурации CORS
                .csrf(csrf -> csrf.disable())
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers(HttpMethod.OPTIONS).permitAll()    // Разрешаем CORS preflight
                        .pathMatchers("/auth/**").permitAll()
                        .pathMatchers("/ws/market/**").permitAll()
                        // Разрешаем доступ к авторизационным путям
                        .anyExchange().authenticated()                  // Все остальные запросы требуют авторизации
                );

        return http.build();
    }
    @Bean
    public CorsWebFilter corsWebFilter() {
        return new CorsWebFilter(corsConfigurationSource());
    }
    private UrlBasedCorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        // Указываем фронтенд, с которого разрешаем доступ
        System.out.println("Configuring CORS with allowed origins:");
        config.setAllowedOrigins(Arrays.asList("http://localhost:5173"));
        System.out.println("Added origin: http://localhost:5173");
        config.addAllowedMethod("*");                       // Разрешаем все HTTP методы (GET, POST, OPTIONS и т.д.)
        config.addAllowedHeader("*");                       // Разрешаем все заголовки
        config.setAllowCredentials(true);                   // Разрешаем использование сессий и токенов

        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
