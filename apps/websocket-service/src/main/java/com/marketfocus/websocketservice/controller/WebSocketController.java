package com.marketfocus.websocketservice.controller;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;

@Controller
public class WebSocketController {

    private final SimpMessagingTemplate messagingTemplate;

    // Внедрение зависимости через конструктор
    @Autowired
    public WebSocketController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    // Обработчик сообщений от клиента
    @MessageMapping("/send/message")
    public void handleMessage(String message) {
        // Отправляем полученное сообщение всем клиентам, подписанным на "/topic/message"
        messagingTemplate.convertAndSend("/topic/message", message);
    }
}
