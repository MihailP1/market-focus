package com.marketfocus.websocketservice.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaListeners {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // Обрабатываем сообщения для различных топиков
    @KafkaListener(topics = "news-updates", groupId = "websocket-group")
    public void listenNews(String message) {
        sendToWebSocket("/topic/news", message);
    }

    @KafkaListener(topics = "quotes-updates", groupId = "websocket-group")
    public void listenQuotes(String message) {
        sendToWebSocket("/topic/quotes", message);
    }

    // Метод для отправки сообщений в WebSocket
    private void sendToWebSocket(String destination, String message) {
        try {
            // Отправляем сообщение в WebSocket
            messagingTemplate.convertAndSend(destination, message);
        } catch (Exception e) {
            // Логируем ошибки (если необходимо)
            System.err.println("Ошибка при отправке сообщения в WebSocket: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
