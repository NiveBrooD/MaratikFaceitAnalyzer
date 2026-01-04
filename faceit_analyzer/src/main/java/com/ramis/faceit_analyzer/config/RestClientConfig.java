package com.ramis.faceit_analyzer.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClient;

@Configuration
@RequiredArgsConstructor
public class RestClientConfig {
    private final FaceitProperties faceitProperties;

    @Bean
    public RestClient restClient() {
        return RestClient.builder()
                .baseUrl(faceitProperties.getApiUrl())
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + faceitProperties.getApiKey())
                .defaultHeader(HttpHeaders.ACCEPT, "application/json")
                .build();
    }
}
