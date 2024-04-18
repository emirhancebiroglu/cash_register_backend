package com.bit.jwtauthservice.config;

import com.bit.jwtauthservice.exceptions.mailconfig.MailConfigException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

/**
 * Configuration class for email settings.
 */
@Configuration
public class EmailConfig {
    private static final Logger logger = LoggerFactory.getLogger(EmailConfig.class);

    @Value("${spring.mail.host}")
    private String host;

    @Value("${spring.mail.port}")
    private int port;

    @Value("${spring.mail.username}")
    private String username;

    @Value("${spring.mail.password}")
    private String password;

    /**
     * Bean method for configuring the JavaMailSender.
     * @return JavaMailSender object
     * @throws MailConfigException if there is an error configuring the mail sender
     */
    @Bean
    public JavaMailSender javaMailSender() throws MailConfigException {
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

        try {
            mailSender.testConnection();
        }
        catch (Exception e){
            logger.error("Failed to establish connection with the mail server: {}", e.getMessage());
            throw new MailConfigException("Failed to configure mail sender");
        }

        return mailSender;
    }
}
