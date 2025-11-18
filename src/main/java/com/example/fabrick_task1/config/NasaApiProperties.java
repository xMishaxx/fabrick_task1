package com.example.fabrick_task1.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "application.nasa.api")
public class NasaApiProperties {
    private String baseUrl;
    private String apiKey;
}