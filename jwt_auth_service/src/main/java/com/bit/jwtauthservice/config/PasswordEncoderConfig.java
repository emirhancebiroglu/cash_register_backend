package com.bit.jwtauthservice.config;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Configuration class for password encoder.
 */
@Configuration
@RequiredArgsConstructor
@Data
public class PasswordEncoderConfig {
    private static final Logger logger = LogManager.getLogger(PasswordEncoderConfig.class.getName());

    /**
     * Bean method for creating BCrypt password encoder.
     * @return PasswordEncoder object
     */
    @Bean
    public PasswordEncoder passwordEncoder(){
        logger.trace("Creating BCryptPasswordEncoder bean...");

        PasswordEncoder encoder = new BCryptPasswordEncoder();

        logger.trace("BCryptPasswordEncoder bean created successfully");
        return encoder;
    }
}
