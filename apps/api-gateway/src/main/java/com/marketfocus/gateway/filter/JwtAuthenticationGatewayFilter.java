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

import java.net.URI;
import java.security.Key;
import java.util.Date;
import java.util.Optional;

@Component
public class JwtAuthenticationGatewayFilter implements GlobalFilter, Ordered {

    public JwtAuthenticationGatewayFilter() {
        System.out.println("[JWT Filter] JwtAuthenticationGatewayFilter bean created");
    }
    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpirationMs;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        System.out.println("[JWT Filter] Request path: " + path);

        // Пропускаем проверку JWT для путей /auth/*
        if (path.startsWith("/auth/")) {
            System.out.println("[JWT Filter] Skipping auth check for path: " + path);
            return chain.filter(exchange);
        }

        // Для WebSocket путей берём токен из query параметров
        if (path.startsWith("/ws/")) {
            System.out.println("[JWT Filter] WebSocket request detected");

            Optional<String> token = extractTokenFromQueryParams(exchange.getRequest().getURI());

            if (token.isPresent()) {
                System.out.println("[JWT Filter] Token from WS query params: " + token.get());
                if (validateToken(token.get())) {
                    System.out.println("[JWT Filter] WS token valid");
                    return chain.filter(exchange);
                } else {
                    System.err.println("[JWT Filter] WS token invalid or expired");
                    return Mono.error(new RuntimeException("Invalid or expired JWT token for WebSocket"));
                }
            } else {
                System.err.println("[JWT Filter] No token found in WS query params");
                return Mono.error(new RuntimeException("Missing JWT token for WebSocket"));
            }
        }

        // Для остальных путей берём токен из заголовка Authorization
        String token = extractToken(exchange);
        if (token != null) {
            System.out.println("[JWT Filter] Token from Authorization header: " + token);
            if (validateToken(token)) {
                System.out.println("[JWT Filter] Token valid");
                return chain.filter(exchange);
            } else {
                System.err.println("[JWT Filter] Token invalid or expired");
                return Mono.error(new RuntimeException("Invalid or expired JWT token"));
            }
        } else {
            System.err.println("[JWT Filter] No Authorization header found");
            return Mono.error(new RuntimeException("Missing Authorization header or Bearer token"));
        }
    }

    private Optional<String> extractTokenFromQueryParams(URI uri) {
        String query = uri.getQuery();
        System.out.println("[JWT Filter] Query string: " + query);
        if (query != null && query.contains("token=")) {
            String[] params = query.split("&");
            for (String param : params) {
                if (param.startsWith("token=")) {
                    String token = param.substring(6);
                    System.out.println("[JWT Filter] Extracted token from query: " + token);
                    return Optional.of(token);
                }
            }
        }
        System.out.println("[JWT Filter] No token parameter found in query");
        return Optional.empty();
    }

    private String extractToken(ServerWebExchange exchange) {
        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
        System.out.println("[JWT Filter] Authorization header: " + authHeader);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            System.out.println("[JWT Filter] Extracted token from header: " + token);
            return token;
        }
        return null;
    }

    private boolean validateToken(String token) {
        try {
            Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
            Claims claims = Jwts.parser()
                    .setSigningKey(key)
                    .parseClaimsJws(token)
                    .getBody();

            System.out.println("[JWT Filter] Token claims: " + claims);

            if (claims.getExpiration().before(new Date())) {
                System.err.println("[JWT Filter] Token expired at: " + claims.getExpiration());
                return false;
            }

            System.out.println("[JWT Filter] Token is valid and not expired");
            return true;
        } catch (Exception e) {
            System.err.println("[JWT Filter] Token validation failed: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
