package com.poolsafety.frontend.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "poolsafety.security")
public record SecurityProperties(boolean relaxedCspForDevelopment) {
}
