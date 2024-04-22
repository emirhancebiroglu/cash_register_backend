package com.bit.productservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The entry point for the Product Service application.
 * This class initializes and starts the Spring Boot application.
 */
@SpringBootApplication(
        scanBasePackages = {"com.bit.productservice"}
)
public class ProductServiceApplication {
    /**
     * The main method to start the Product Service application.
     * @param args The command-line arguments passed to the application.
     */
    public static void main(String[] args) {
        SpringApplication.run(ProductServiceApplication.class, args);
    }

}
