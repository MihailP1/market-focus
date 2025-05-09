package com.marketfocus.websocketservice.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.HttpStatus;

import java.util.Map;

public class AuthHandshakeInterceptor implements HandshakeInterceptor {

    // Создаем логгер
    private static final Logger logger = LoggerFactory.getLogger(AuthHandshakeInterceptor.class);

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {

        // ✅ Извлекаем токен из query-параметров URL
        String query = request.getURI().getQuery();
        String token = null;

        if (query != null && query.contains("token=")) {
            for (String param : query.split("&")) {
                if (param.startsWith("token=")) {
                    token = param.substring(6);
                    break;
                }
            }
        }

        if (token != null && isValidToken(token)) {
            attributes.put("token", token);
            return true;
        } else {
            System.out.println("[HandshakeInterceptor] Missing or invalid token in query params: " + token);
            response.setStatusCode(HttpStatus.FORBIDDEN);
            return false;
        }
    }


    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception ex) {
        // Логируем завершение обработки
        if (ex != null) {
            logger.error("Handshake failed with exception: {}", ex.getMessage());
        } else {
            logger.info("Handshake completed successfully.");
        }
    }

    // Пример метода для проверки токена
    private boolean isValidToken(String token) {
        // Логируем проверку токена
        logger.debug("Validating token: {}", token);

        // Реализуйте логику проверки токена (например, проверка подписи JWT)
        return token != null && token.length() > 10;  // Это просто заглушка для проверки
    }
}
