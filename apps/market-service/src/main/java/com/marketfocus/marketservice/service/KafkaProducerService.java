package com.marketfocus.marketservice.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaProducerService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Отправляет сообщение (например, котировки) в указанный Kafka-топик.
     *
     * @param topic   название Kafka-топика
     * @param message строка с данными (например, JSON с котировками)
     */
    public void sendQuote(String topic, String message) {
        kafkaTemplate.send(topic, message);
    }
}
