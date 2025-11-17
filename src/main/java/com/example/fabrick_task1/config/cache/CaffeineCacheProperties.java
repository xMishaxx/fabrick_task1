package com.example.fabrick_task1.config.cache;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "spring.cache.caffeine")
public class CaffeineCacheProperties {

    private long maximumSize = 1000; // default num of entry
    private long expireAfterWrite = 86400000; // default: 24 hours in milliseconds
}