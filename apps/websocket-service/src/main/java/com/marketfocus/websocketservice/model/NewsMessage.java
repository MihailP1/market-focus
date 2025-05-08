package com.marketfocus.websocketservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewsMessage {

    private String title;
    private String description;
    private String url;
    private String source;

}
