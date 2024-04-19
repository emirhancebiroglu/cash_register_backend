package com.bit.usermanagementservice.service;

/**
 * The EmailService interface defines methods for sending emails.
 * It provides overloaded methods to send emails with different parameters.
 * Implementations of this interface are responsible for sending emails to users.
 */
public interface EmailService {
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
    void sendEmail(String to, String subject, String templateName, String userCode, String userPassword,
                                   String firstName, String lastName);

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
    void sendEmail(String to, String subject, String templateName, String userCode,
                                   String firstName, String lastName);

    /**
     * Sends an email with user details.
     *
     * @param to the recipient email address.
     * @param subject the subject of the email.
     * @param templateName the name of the email template to use.
     * @param firstName the first name of the user.
     * @param lastName the last name of the user.
     */
    void sendEmail(String to, String subject, String templateName,
                   String firstName, String lastName);
}
