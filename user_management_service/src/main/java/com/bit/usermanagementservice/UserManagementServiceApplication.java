package com.bit.usermanagementservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The UserManagementServiceApplication class serves as the entry point for the user management service application.
 * It bootstraps the Spring Boot application and starts the Spring context.
 * This class specifies the base packages to scan for components and configurations using the @SpringBootApplication annotation.
 */
@SpringBootApplication(
		scanBasePackages = {"com.bit.usermanagementservice"}
)
public class UserManagementServiceApplication {

	/**
	 * Main method to start the user management service application.
	 *
	 * @param args the command line arguments.
	 */
	public static void main(String[] args) {
		SpringApplication.run(UserManagementServiceApplication.class, args);
	}
}
