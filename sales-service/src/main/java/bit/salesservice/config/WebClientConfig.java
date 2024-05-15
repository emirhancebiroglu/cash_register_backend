package bit.salesservice.config;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Configuration class for WebClient setup.
 */
@Configuration
@RequiredArgsConstructor
public class WebClientConfig {
    // Logger for logging WebClient setup
    private static final Logger logger = LogManager.getLogger(WebClientConfig.class);

    // Value fetched from application.properties for the base URL of the product service
    @Value("${product.service.base.url}")
    private String productServiceBaseUrl;

    /**
     * Creates a load-balanced WebClient builder bean.
     *
     * @return the WebClient builder
     */
    @Bean
    @LoadBalanced
    public WebClient.Builder webClientBuilder(){
        // Create and return a load-balanced WebClient builder
        return WebClient.builder();
    }

    /**
     * Creates a configured WebClient bean.
     *
     * @return the configured WebClient
     */
    @Bean
    public WebClient webClient(){
        // Log the creation of WebClient instance
        logger.trace("Creating WebClient instance.");

        // Build a WebClient instance with the configured base URL
        WebClient webClient = webClientBuilder().baseUrl(productServiceBaseUrl).build();

        // Log the successful creation of WebClient instance
        logger.trace("WebClient instance created successfully.");

        // Return the configured WebClient
        return webClient;
    }
}
