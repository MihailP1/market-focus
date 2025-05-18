package com.marketfocus.marketservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "finnhub")
@Getter
@Setter
public class FinnhubApiProperties {
    private String url;
    private List<String> symbols;
    private String apiKey;
}
