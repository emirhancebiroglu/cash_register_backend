package com.bit.jwtauthservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Main class to start the JWT Auth Service application.
 */
@SpringBootApplication(scanBasePackages = {"com.bit.jwtauthservice"})
@EnableJpaRepositories(basePackages = {"com.bit.jwtauthservice.repository"})
public class JwtAuthServiceApplication {
	/**
	 * Main method to start the JWT Auth Service application.
	 *
	 * @param args Command-line arguments.
	 */
	public static void main(String[] args) {
		SpringApplication.run(JwtAuthServiceApplication.class, args);
	}

}
