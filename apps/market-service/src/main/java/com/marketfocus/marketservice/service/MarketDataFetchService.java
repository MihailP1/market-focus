package com.marketfocus.marketservice.service;

import com.marketfocus.marketservice.config.FinnhubApiProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class MarketDataFetchService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final RestTemplate restTemplate;
    private final FinnhubApiProperties finnhubApiProperties;

    private final String topic = "quotes-topic"; // Название Kafka-топика под котировки

    @Autowired
    public MarketDataFetchService(KafkaTemplate<String, String> kafkaTemplate,
                                  FinnhubApiProperties finnhubApiProperties) {
        this.kafkaTemplate = kafkaTemplate;
        this.restTemplate = new RestTemplate();
        this.finnhubApiProperties = finnhubApiProperties;
    }

    @Scheduled(fixedRate = 60000)
    public void fetchMarketData() {
        for (String symbol : finnhubApiProperties.getSymbols()) {
            String url = String.format("%s?symbol=%s&token=%s",
                    finnhubApiProperties.getUrl(),
                    symbol,
                    finnhubApiProperties.getApiKey());

            try {
                String response = restTemplate.getForObject(url, String.class);
                System.out.printf("[Finnhub Response for %s]: %s%n", symbol, response);

                kafkaTemplate.send(topic, response);

            } catch (Exception e) {
                System.err.printf("Error fetching data for %s: %s%n", symbol, e.getMessage());
            }
        }
    }

}
