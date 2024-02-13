package com.bit.sharedClasses.config;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.logging.Logger;
import java.util.logging.Level;

@Configuration
@RequiredArgsConstructor
@Data
public class PasswordEncoderConfig {
    private static final Logger logger = Logger.getLogger(PasswordEncoderConfig.class.getName());

    @Bean
    public PasswordEncoder passwordEncoder(){
        try{
            return new BCryptPasswordEncoder();
        }
        catch (Exception e) {
            logger.log(Level.SEVERE, "Error while creating PasswordEncoder", e);
            throw e;
        }
    }


}
