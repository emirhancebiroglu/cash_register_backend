package com.bit.usermanagementservice.service.serviceimpl;

import com.bit.usermanagementservice.service.EmailService;
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
 * The EmailServiceImpl class is an implementation of the EmailService interface.
 * It provides methods to send emails using JavaMailSender and Thymeleaf template engine.
 * This class is responsible for sending various types of emails including user registration and notification emails.
 */
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private static final Logger logger = LogManager.getLogger(EmailServiceImpl.class);

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

     /**
     * This method is used to set up and send an email using the provided parameters.
     *
     * @param to The recipient's email address.
     * @param subject The subject of the email.
     * @param templateName The name of the Thymeleaf template to be used for the email content.
     * @param context The context object containing variables to be used in the template.
     *
      */
    private void setHelper (String to, String subject, String templateName, Context context) {
        // Create a new MimeMessage object
        MimeMessage mimeMessage = mailSender.createMimeMessage();

        // Create a MimeMessageHelper object to handle email-specific operations
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");

        try {
            // Set the recipient's email address
            helper.setTo(to);

            // Set the subject of the email
            helper.setSubject(subject);

            // Process the Thymeleaf template and get the HTML content
            String htmlContent = templateEngine.process(templateName, context);

            // Set the email content as HTML
            helper.setText(htmlContent, true);

            // Send the email
            mailSender.send(mimeMessage);

            // Log a success message
            logger.info("The mail has been successfully sent.");

        } catch (MessagingException e) {
            // Log any MessagingException errors
            logger.error(e.getMessage());
        }
    }

     /**
     * This method is used to set the context variables for the email template.
     *
     * @param firstName The first name of the recipient.
     * @param lastName The last name of the recipient.
     * @param context The context object to store the variables.
     *
      */
    private void setContext(String firstName, String lastName, Context context){
        // Set the first name variable in the context
        context.setVariable("firstName", firstName);

        // Set the last name variable in the context
        context.setVariable("lastName", lastName);
    }
}