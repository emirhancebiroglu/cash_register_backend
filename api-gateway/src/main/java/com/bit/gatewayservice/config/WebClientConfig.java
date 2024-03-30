package com.bit.gatewayservice.config;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class WebClientConfig {
    private static final Logger logger = LogManager.getLogger(WebClientConfig.class);

    @Value("${auth.service.base.url}")
    private String authServiceBaseUr;

    @Bean
    @LoadBalanced
    public WebClient.Builder webClientBuilder(){
        return WebClient.builder();
    }

    @Bean
    public WebClient webClient(){
        logger.debug("Creating WebClient instance.");
        WebClient webClient = webClientBuilder().baseUrl(authServiceBaseUr).build();
        logger.debug("WebClient instance created successfully.");
        return webClient;
    }
}
