package com.marketfocus.newsservice.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class NewsFetchService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final String topic = "news-topic"; // Пример топика, выберите тот, который вам нужен

    private final RestTemplate restTemplate;
    private final String baseUrl;
    private final String apiKey;
    private final String category;
    private final String country;

    @Autowired
    public NewsFetchService(KafkaTemplate<String, String> kafkaTemplate,
                            @Value("${newsapi.url}") String baseUrl,
                            @Value("${newsapi.apiKey}") String apiKey,
                            @Value("${newsapi.category}") String category,
                            @Value("${newsapi.country}") String country) {
        this.kafkaTemplate = kafkaTemplate;
        this.restTemplate = new RestTemplate();
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
        this.category = category;
        this.country = country;
    }

    @Scheduled(fixedRate = 60000)
    public void fetchNews() {
        String url = String.format("%s?category=%s&country=%s&apiKey=%s",
                baseUrl, category, country, apiKey);
        try {
            // Запрос новостей через API
            String response = restTemplate.getForObject(url, String.class);
            System.out.println("[News API Response]: " + response);

            // Отправляем полученные новости в Kafka
            kafkaTemplate.send(topic, response);

        } catch (Exception e) {
            System.err.println("Error fetching news: " + e.getMessage());
        }
    }
}
