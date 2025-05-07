package com.marketfocus.newsservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "newsapi")
@Getter
@Setter
public class NewsApiProperties {
    private String url;
    private String category;
    private String country;
    private String apiKey;
}
