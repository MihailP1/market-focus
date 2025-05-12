package com.marketfocus.gateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.security.Key;
import java.util.Date;
import java.util.Optional;

@Component
public class JwtAuthenticationFilter implements WebFilter {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpirationMs;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        System.out.println("Request received: " + path);

        if (path.startsWith("/auth/")) {
            System.out.println("Skipping token validation for path: " + path);
            return chain.filter(exchange);
        }

        if (path.startsWith("/ws/")) {
            Optional<String> token = extractTokenFromQueryParams(exchange.getRequest().getURI());

            if (token.isPresent()) {
                System.out.println("Token extracted from WebSocket query parameters: " + token.get());
                if (validateToken(token.get())) {
                    System.out.println("Valid token received for WebSocket");
                    return chain.filter(exchange);
                } else {
                    System.err.println("Invalid or missing JWT token for WebSocket");
                    return Mono.error(new RuntimeException("Invalid or missing JWT token for WebSocket"));
                }
            } else {
                System.err.println("No token found in WebSocket query parameters");
                return Mono.error(new RuntimeException("Missing JWT token for WebSocket"));
            }
        }

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

    private String extractToken(ServerWebExchange exchange) {
        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            System.out.println("Extracted token from Authorization header: " + token);
            return token;
        }
        return null;
    }

    private Optional<String> extractTokenFromQueryParams(URI uri) {
        String query = uri.getQuery();
        if (query != null && query.contains("token=")) {
            String[] params = query.split("&");
            for (String param : params) {
                if (param.startsWith("token=")) {
                    String token = param.substring(6);
                    System.out.println("Extracted token from query parameters: " + token);
                    return Optional.of(token);
                }
            }
        }
        return Optional.empty();
    }

    private boolean validateToken(String token) {
        try {
            Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes()); // 🔐 правильно инициализируем ключ

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
}
