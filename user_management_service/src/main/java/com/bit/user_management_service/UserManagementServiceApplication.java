package com.bit.user_management_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(
		scanBasePackages = {"com.bit.sharedClasses", "com.bit.user_management_service", "com.bit.sharedFilter"}
)
@EnableJpaRepositories("com.bit.sharedClasses.repository")
@EntityScan("com.bit.sharedClasses.entity")
@EnableFeignClients(basePackages = {
		"com.bit.sharedFilter"
})
public class UserManagementServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserManagementServiceApplication.class, args);
	}
}
