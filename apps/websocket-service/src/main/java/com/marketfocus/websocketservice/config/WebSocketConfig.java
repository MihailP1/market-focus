package com.marketfocus.websocketservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Включаем простое брокерирование сообщений, через /topic
        config.enableSimpleBroker("/topic");
        // Префикс для сообщений, отправляемых от клиента к серверу
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Регистрация endpoint для WebSocket, доступный по /ws/market
        registry.addEndpoint("/ws/market").setAllowedOriginPatterns("*").withSockJS();
    }
}
