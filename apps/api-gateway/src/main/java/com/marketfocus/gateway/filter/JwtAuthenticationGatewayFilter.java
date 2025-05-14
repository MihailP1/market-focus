package com.marketfocus.gateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.security.Key;
import java.util.Date;

@Component
public class JwtAuthenticationGatewayFilter implements GlobalFilter, Ordered {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpirationMs;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // Получаем путь запроса
        String path = exchange.getRequest().getURI().getPath();
        System.out.println("Request received: " + path);

        // Пропускаем проверку JWT для путей, связанных с авторизацией (например /auth/*)
        if (path.startsWith("/auth/")) {
            System.out.println("Skipping token validation for path: " + path);
            return chain.filter(exchange);
        }

        // Для других путей проверяем наличие и валидность токена
        String token = extractToken(exchange);

        if (token != null) {
            System.out.println("Token extracted from Authorization header: " + token);
            if (validateToken(token)) {
                System.out.println("Valid token received");
                return chain.filter(exchange);
            } else {
                System.err.println("Invalid JWT token");
                return Mono.error(new RuntimeException("Invalid JWT token"));
            }
        } else {
            System.err.println("No Authorization header found for token");
            return Mono.error(new RuntimeException("Invalid or missing JWT token"));
        }
    }

    // Метод для извлечения токена из Authorization header
    private String extractToken(ServerWebExchange exchange) {
        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            System.out.println("Extracted token from Authorization header: " + token);
            return token;
        }
        return null;
    }

    // Метод для валидации токена
    private boolean validateToken(String token) {
        try {
            Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes()); // Инициализируем ключ для HMAC

            Claims claims = Jwts.parser()
                    .setSigningKey(key)
                    .parseClaimsJws(token)
                    .getBody();

            if (claims.getExpiration().before(new Date())) {
                System.err.println("Token has expired: " + token);
                return false;
            }

            System.out.println("Token is valid: " + token);
            return true;
        } catch (Exception e) {
            System.err.println("Token validation failed: " + e.getMessage());
            return false;
        }
    }

    // Метод для задания порядка выполнения фильтра
    @Override
    public int getOrder() {
        return -1; // Чем меньше число, тем раньше сработает фильтр
    }
}
