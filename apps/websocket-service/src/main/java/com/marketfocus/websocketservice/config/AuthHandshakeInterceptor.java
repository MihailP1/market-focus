package com.marketfocus.websocketservice.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;

import java.util.Map;

public class AuthHandshakeInterceptor implements HandshakeInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(AuthHandshakeInterceptor.class);

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {

        // Просто пропускаем проверку токена и разрешаем соединение
        // Так как API Gateway уже позаботился о токене и авторизации

        // Логируем запрос
        logger.info("[HandshakeInterceptor] WebSocket request received for path: {}", request.getURI().getPath());

        // Пропускаем проверку токена
        // Если токен был проверен в API Gateway, то это место просто пропустит эту проверку.
        // Для удостоверенности, можно добавить информацию о токене в атрибуты, если это нужно для дальнейших процессов.
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception ex) {
        if (ex != null) {
            logger.error("Handshake failed with exception: {}", ex.getMessage());
        } else {
            logger.info("Handshake completed successfully.");
        }
    }
}
