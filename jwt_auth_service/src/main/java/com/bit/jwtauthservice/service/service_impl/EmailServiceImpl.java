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

    /**
     * Sends a user code email containing the user code.
     *
     * @param to           The recipient email address.
     * @param subject      The subject of the email.
     * @param templateName The name of the Thymeleaf template for the email content.
     * @param userCode     The user code to be included in the email.
     */
    @Override
    public void sendUserCode(String to, String subject, String templateName, String userCode){
        Context context = new Context();

        context.setVariable("userCode", userCode);

        setHelper(to, subject, templateName, context);
    }

    /**
     * Sends a password reset email containing the reset link.
     *
     * @param to           The recipient email address.
     * @param subject      The subject of the email.
     * @param templateName The name of the Thymeleaf template for the email content.
     * @param resetLink    The password reset link to be included in the email.
     */
    @Override
    public void sendPasswordResetEmail(String to, String subject, String templateName, String resetLink) {
        Context context = new Context();

        context.setVariable("resetLink", resetLink);
        context.setVariable("subject", subject);

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
            logger.info("The mail has been successfully sent.");

        } catch (MessagingException e) {
            logger.error(e.getMessage());
        }
    }
}