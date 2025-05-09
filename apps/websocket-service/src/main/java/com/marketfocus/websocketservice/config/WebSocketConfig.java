package com.marketfocus.websocketservice.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    // Создаем логгер
    private static final Logger logger = LoggerFactory.getLogger(WebSocketConfig.class);

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Логируем начало настройки брокера сообщений
        logger.info("Configuring WebSocket message broker");

        // Enable simple message broker for /topic
        config.enableSimpleBroker("/topic");
        // Prefix for client-to-server messages
        config.setApplicationDestinationPrefixes("/app");

        // Логируем завершение настройки
        logger.info("WebSocket message broker configured successfully with /topic and /app prefixes");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Логируем начало регистрации STOMP endpoints
        logger.info("Registering STOMP endpoints");

        // Register STOMP endpoint with authentication via HandshakeInterceptor
        registry.addEndpoint("/ws/market")
                .setAllowedOrigins("http://localhost:5173")  // Разрешаем CORS для фронтенда
                .addInterceptors(new AuthHandshakeInterceptor())  // Добавляем HandshakeInterceptor для авторизации
                .withSockJS();

        // Логируем успешную регистрацию endpoint
        logger.info("STOMP endpoint '/ws/market' registered with allowed origins: http://localhost:5173");
    }
}
