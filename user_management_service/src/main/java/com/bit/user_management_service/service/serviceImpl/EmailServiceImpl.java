package com.bit.user_management_service.service.serviceImpl;

import com.bit.user_management_service.service.EmailService;
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
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    public void sendEmail(String to, String subject, String templateName, String userCode, String userPassword,
                          String firstName, String lastName){
        Context context = new Context();

        setContext(firstName, lastName, context);
        context.setVariable("userCode", userCode);
        context.setVariable("userPassword", userPassword);

        setHelper(to, subject, templateName, context);
    }

    @Override
    public void sendEmail(String to, String subject, String templateName,
                          String userCode, String firstName, String lastName) {
        Context context = new Context();

        setContext(firstName, lastName, context);
        context.setVariable("userCode", userCode);

        setHelper(to, subject, templateName, context);
    }

    @Override
    public void sendEmail(String to, String subject, String templateName, String firstName, String lastName) {
        Context context = new Context();

        setContext(firstName, lastName, context);

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
            logger.info("Mail is sent successfully");

        } catch (MessagingException e) {
            logger.error(e.getMessage());
        }
    }

    private void setContext(String firstName, String lastName, Context context){
        context.setVariable("firstName", firstName);
        context.setVariable("lastName", lastName);
    }
}