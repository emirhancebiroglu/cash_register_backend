package com.bit.jwtauthservice.service.service_impl;

import com.bit.jwtauthservice.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

    @Override
    public void sendUserCode(String to, String subject, String templateName, String userCode){
        Context context = new Context();

        context.setVariable("userCode", userCode);

        setHelper(to, subject, templateName, context);
    }

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