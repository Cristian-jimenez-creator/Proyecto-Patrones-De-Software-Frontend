package com.poolsafety.frontend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class BackendRestClientConfig {

    // Base client with timeouts. Step 2 wraps it in BackendApiClient (correlation ID, error handling).
    // It is built directly so it does not depend on Spring Boot's RestClient auto-configuration.
    @Bean
    public RestClient backendRestClient(BackendProperties properties) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(properties.connectTimeout());
        factory.setReadTimeout(properties.readTimeout());
        return RestClient.builder().baseUrl(properties.baseUrl()).requestFactory(factory).build();
    }
}
