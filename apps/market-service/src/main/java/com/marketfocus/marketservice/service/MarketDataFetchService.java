package com.marketfocus.marketservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marketfocus.marketservice.config.TwelveDataApiProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpMethod;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class MarketDataFetchService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final RestTemplate restTemplate;
    private final TwelveDataApiProperties twelveDataApiProperties;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String topic = "quotes-topic";

    @Autowired
    public MarketDataFetchService(KafkaTemplate<String, String> kafkaTemplate,
                                  TwelveDataApiProperties twelveDataApiProperties) {
        this.kafkaTemplate = kafkaTemplate;
        this.restTemplate = new RestTemplate();
        this.twelveDataApiProperties = twelveDataApiProperties;
    }

    @Scheduled(fixedRate = 60000)
    public void fetchMarketData() {
        String interval = "1h";
        int outputSize = 30;

        for (String symbol : twelveDataApiProperties.getSymbols()) {
            String url = String.format("%s/time_series?symbol=%s&interval=%s&outputsize=%d&apikey=%s",
                    twelveDataApiProperties.getUrl(),
                    symbol,
                    interval,
                    outputSize,
                    twelveDataApiProperties.getApiKey());

            try {
                ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);
                int statusCode = response.getStatusCodeValue();
                String body = response.getBody();

                System.out.printf("Twelve Data API response for %s: HTTP status=%d%n", symbol, statusCode);
                System.out.printf("Raw response: %s%n", body);

                if (statusCode != 200 || body == null) {
                    System.err.printf("HTTP error or empty response for %s%n", symbol);
                    continue;
                }

                JsonNode rootNode = objectMapper.readTree(body);

                if (rootNode.has("status") && rootNode.path("status").asText().equalsIgnoreCase("error")) {
                    System.err.printf("Twelve Data API error for %s: %s%n", symbol, rootNode.toString());
                    continue;
                }

                JsonNode valuesNode = rootNode.path("values");
                if (!valuesNode.isArray()) {
                    System.err.printf("Missing 'values' array for %s%n", symbol);
                    continue;
                }

                List<ObjectNode> dataPoints = new ArrayList<>();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

                for (JsonNode valueNode : valuesNode) {
                    try {
                        ObjectNode point = objectMapper.createObjectNode();

                        String datetime = valueNode.path("datetime").asText();
                        LocalDateTime localDateTime = LocalDateTime.parse(datetime, formatter);
                        Instant timestamp = localDateTime.atZone(ZoneId.of("America/New_York")).toInstant();

                        point.put("timestamp", timestamp.toString());
                        point.put("open", valueNode.path("open").asDouble());
                        point.put("high", valueNode.path("high").asDouble());
                        point.put("low", valueNode.path("low").asDouble());
                        point.put("close", valueNode.path("close").asDouble());
                        point.put("volume", valueNode.path("volume").asLong());

                        dataPoints.add(point);
                    } catch (Exception e) {
                        System.err.printf("Failed to parse data point: %s%n", e.getMessage());
                    }
                }

                // Сортировка по возрастанию времени
                dataPoints.sort(Comparator.comparing(a -> a.get("timestamp").asText()));

                ObjectNode message = objectMapper.createObjectNode();
                message.put("symbol", symbol);
                message.set("data", objectMapper.valueToTree(dataPoints));

                String finalJson = objectMapper.writeValueAsString(message);
                System.out.printf("[Twelve Data Hourly Data for %s]: %s%n", symbol, finalJson);

                kafkaTemplate.send(topic, finalJson);

            } catch (Exception e) {
                System.err.printf("Error fetching data for %s: %s%n", symbol, e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
