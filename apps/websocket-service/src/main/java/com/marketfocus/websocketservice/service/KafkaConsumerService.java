package com.marketfocus.websocketservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public KafkaConsumerService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    // Слушаем топик с новостями
    @KafkaListener(topics = "news-topic", groupId = "websocket-service-group")
    public void listenNews(String message) {
        System.out.println("Received news from Kafka: " + message);
        messagingTemplate.convertAndSend("/topic/news", message);
    }

    // Слушаем топик с котировками рынка
    @KafkaListener(topics = "quotes-topic", groupId = "websocket-service-group")
    public void listenMarketQuotes(String message) {
        System.out.println("Received market quote from Kafka: " + message);
        messagingTemplate.convertAndSend("/topic/quotes", message);
    }
}
