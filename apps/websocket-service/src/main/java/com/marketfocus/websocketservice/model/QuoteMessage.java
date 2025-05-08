package com.marketfocus.websocketservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuoteMessage {

    private String symbol;
    private double price;
    private long timestamp;

}
