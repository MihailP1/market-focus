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

    // Логгер для отслеживания
    private static final Logger logger = LoggerFactory.getLogger(WebSocketConfig.class);

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Логируем начало настройки брокера сообщений
        logger.info("Configuring WebSocket message broker");

        // Включаем простое сообщение брокера для /topic
        config.enableSimpleBroker("/topic");
        // Префикс для сообщений от клиента к серверу
        config.setApplicationDestinationPrefixes("/app");

        // Логируем завершение настройки
        logger.info("WebSocket message broker configured successfully with /topic and /app prefixes");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Логируем начало регистрации STOMP endpoints
        logger.info("Registering STOMP endpoints");

        // Регистрируем STOMP endpoint с поддержкой SockJS
        registry.addEndpoint("/ws/market")
                .setAllowedOrigins("http://localhost:5173")  // Разрешаем CORS для фронтенда
                // Убираем Interceptor для авторизации, так как это будет обрабатываться на уровне API Gateway
                .withSockJS();

        // Логируем успешную регистрацию endpoint
        logger.info("STOMP endpoint '/ws/market' registered with allowed origins: http://localhost:5173");
    }
}
