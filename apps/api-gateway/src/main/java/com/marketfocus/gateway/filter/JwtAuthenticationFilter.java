package com.marketfocus.gateway.filter;

import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationFilter implements WebFilter {

    @Value("${jwt.secret}")
    private String jwtSecret;  // Секретный ключ JWT, извлекаем из application.yml

    @Value("${jwt.expiration}")
    private long jwtExpirationMs;  // Время истечения срока действия JWT, извлекаем из application.yml

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        System.out.println("Request received: " + path);

        // ⛔️ Игнорировать /auth/** маршруты (логин, регистрация и т.п.)
        if (path.startsWith("/auth/")) {
            return chain.filter(exchange); // пропускаем дальше без проверки токена
        }

        String token = extractToken(exchange);

        if (token != null && validateToken(token)) {
            System.out.println("Valid token received: " + token);
            return chain.filter(exchange);
        } else {
            System.err.println("Invalid or missing JWT token");
            return Mono.error(new RuntimeException("Invalid or missing JWT token"));
        }
    }

    private String extractToken(ServerWebExchange exchange) {
        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // Убираем "Bearer " из начала строки
            System.out.println("Extracted token: " + token); // Логируем извлечённый токен (можно добавить условие, чтобы не логировать сам токен для безопасности)
            return token;
        }
        return null;
    }

    private boolean validateToken(String token) {
        try {
            // Используем секрет из конфигурации
            Jwts.parser()
                    .setSigningKey(jwtSecret) // Берем секретный ключ из конфигурации
                    .parseClaimsJws(token); // Попытка парсинга токена
            return true;
        } catch (Exception e) {
            // Логируем ошибку валидации токена
            System.err.println("Token validation failed: " + e.getMessage());
            return false;
        }
    }
}
