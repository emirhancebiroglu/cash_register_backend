package com.bit.jwt_auth_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(
		scanBasePackages = {"com.bit.sharedClasses", "com.bit.jwt_auth_service"}
)
@EnableJpaRepositories("com.bit.sharedClasses.repository")
@EntityScan("com.bit.sharedClasses.entity")
public class JwtAuthServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(JwtAuthServiceApplication.class, args);
	}

}
