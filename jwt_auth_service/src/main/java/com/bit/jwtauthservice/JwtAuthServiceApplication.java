package com.bit.jwtauthservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.bit.jwtauthservice", "com.bit.sharedfilter"})
public class JwtAuthServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(JwtAuthServiceApplication.class, args);
	}

}
