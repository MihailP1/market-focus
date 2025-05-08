package com.marketfocus.websocketservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    private final SimpMessagingTemplate messagingTemplate;

    // Внедрение SimpMessagingTemplate для отправки сообщений WebSocket клиентам
    @Autowired
    public KafkaConsumerService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    // Обработчик сообщений из Kafka
    @KafkaListener(topics = "news-topic", groupId = "websocket-service-group")
    public void listen(String message) {
        // Логируем полученное сообщение
        System.out.println("Received news from Kafka: " + message);

        // Отправляем сообщение всем подключенным WebSocket клиентам
        messagingTemplate.convertAndSend("/topic/news", message);
    }
}
