package com.bit.usermanagementservice.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

/**
 * Configuration class for setting up email functionality using JavaMailSender.
 */
@Configuration
public class EmailConfig {
    private static final Logger logger = LogManager.getLogger(EmailConfig.class);

    @Value("${spring.mail.host}")
    private String host;

    @Value("${spring.mail.port}")
    private int port;

    @Value("${spring.mail.username}")
    private String username;

    @Value("${spring.mail.password}")
    private String password;

    /**
     * Creates a JavaMailSender bean for sending emails.
     *
     * @return JavaMailSender instance configured with the provided properties.
     */
    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setPort(port);
        mailSender.setUsername(username);
        mailSender.setPassword(password);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");

        logger.info("Setting up email configuration...");
        logger.info("Host: {}", host);
        logger.info("Port: {}", port);
        logger.info("Username: {}", username);

        if (logger.isDebugEnabled()) {
            logger.debug("Setting up email password...");
        }

        return mailSender;
    }
}
