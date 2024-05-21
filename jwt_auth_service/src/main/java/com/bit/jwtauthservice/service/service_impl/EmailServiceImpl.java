package com.bit.jwtauthservice.service.service_impl;

import com.bit.jwtauthservice.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

/**
 * Implementation of the EmailService interface providing email sending functionality.
 * This service class is responsible for sending user code emails and password reset emails.
 */
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private static final Logger logger = LogManager.getLogger(EmailServiceImpl.class);

    @Override
    public void sendUserCode(String to, String subject, String templateName, String userCode){
        Context context = new Context();

        // Add user code to the Thymeleaf context
        context.setVariable("userCode", userCode);

        // Send the email using helper method
        setHelper(to, subject, templateName, context);
    }

    @Override
    public void sendPasswordResetEmail(String to, String subject, String templateName, String resetLink) {
        Context context = new Context();

        // Add reset link and subject to the Thymeleaf context
        context.setVariable("resetLink", resetLink);
        context.setVariable("subject", subject);

        // Send the email using helper method
        setHelper(to, subject, templateName, context);
    }

    private void setHelper (String to, String subject, String templateName, Context context) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");

        try {
            helper.setTo(to);
            helper.setSubject(subject);
            String htmlContent = templateEngine.process(templateName, context);
            helper.setText(htmlContent, true);
            mailSender.send(mimeMessage);
            logger.trace("The mail has been successfully sent.");

        } catch (MessagingException e) {
            logger.error("Error occurred while sending email to {}: {}", to, e.getMessage(), e);
        }
    }
}