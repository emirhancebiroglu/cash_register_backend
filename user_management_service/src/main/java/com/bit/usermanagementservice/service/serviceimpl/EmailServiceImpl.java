package com.bit.usermanagementservice.service.serviceimpl;

import com.bit.usermanagementservice.service.EmailService;
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

/**
 * The EmailServiceImpl class is an implementation of the EmailService interface.
 * It provides methods to send emails using JavaMailSender and Thymeleaf template engine.
 * This class is responsible for sending various types of emails including user registration and notification emails.
 */
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

    /**
     * Sends an email with user details including user code and password.
     *
     * @param to the recipient email address.
     * @param subject the subject of the email.
     * @param templateName the name of the email template to use.
     * @param userCode the user code to include in the email.
     * @param userPassword the user password to include in the email.
     * @param firstName the first name of the user.
     * @param lastName the last name of the user.
     */
    @Override
    public void sendEmail(String to, String subject, String templateName, String userCode, String userPassword,
                          String firstName, String lastName){
        Context context = new Context();

        setContext(firstName, lastName, context);
        context.setVariable("userCode", userCode);
        context.setVariable("userPassword", userPassword);

        setHelper(to, subject, templateName, context);
    }

    /**
     * Sends an email with user details including user code.
     *
     * @param to the recipient email address.
     * @param subject the subject of the email.
     * @param templateName the name of the email template to use.
     * @param userCode the user code to include in the email.
     * @param firstName the first name of the user.
     * @param lastName the last name of the user.
     */
    @Override
    public void sendEmail(String to, String subject, String templateName,
                          String userCode, String firstName, String lastName) {
        Context context = new Context();

        setContext(firstName, lastName, context);
        context.setVariable("userCode", userCode);

        setHelper(to, subject, templateName, context);
    }

    /**
     * Sends an email with user details.
     *
     * @param to the recipient email address.
     * @param subject the subject of the email.
     * @param templateName the name of the email template to use.
     * @param firstName the first name of the user.
     * @param lastName the last name of the user.
     */
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
            logger.info("The mail has been successfully sent.");

        } catch (MessagingException e) {
            logger.error(e.getMessage());
        }
    }

    private void setContext(String firstName, String lastName, Context context){
        context.setVariable("firstName", firstName);
        context.setVariable("lastName", lastName);
    }
}