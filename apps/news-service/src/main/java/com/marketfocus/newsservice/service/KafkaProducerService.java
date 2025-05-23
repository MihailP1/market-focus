package com.marketfocus.newsservice.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaProducerService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    // Метод для отправки сообщения в Kafka
    public void sendMessage(String topic, String message) {
        kafkaTemplate.send(topic, message);
    }
}
