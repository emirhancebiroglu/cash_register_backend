package com.bit.usermanagementservice.config;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuration class for setting up password encoding.
 */
@Configuration
@RequiredArgsConstructor
@Data
public class PasswordEncoderConfig {
    private static final Logger logger = LogManager.getLogger(PasswordEncoderConfig.class);

    /**
     * Creates a PasswordEncoder bean using BCryptPasswordEncoder.
     *
     * @return PasswordEncoder instance configured with BCryptPasswordEncoder.
     */
    @Bean
    public PasswordEncoder passwordEncoder(){
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        logger.info("BCryptPasswordEncoder bean created successfully");
        return encoder;
    }
}
