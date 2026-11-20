package com.poolsafety.frontend.config;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "poolsafety.backend")
public record BackendProperties(String baseUrl, Duration connectTimeout, Duration readTimeout) {
}
