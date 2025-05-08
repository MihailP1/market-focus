package com.marketfocus.websocketservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class MessageDispatcherService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // Метод для отправки сообщений в WebSocket
    public void sendToTopic(String destination, Object payload) {
        messagingTemplate.convertAndSend(destination, payload);
    }
}
