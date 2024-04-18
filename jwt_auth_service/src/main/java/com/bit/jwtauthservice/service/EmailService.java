package com.bit.jwtauthservice.service;

public interface EmailService {
    /**
     * Sends a user code to the specified email address.
     *
     * @param to           The recipient's email address.
     * @param subject      The subject of the email.
     * @param templateName The name of the email template.
     * @param userCode     The user code to be sent.
     */
    void sendUserCode(String to, String subject, String templateName, String userCode);

    /**
     * Sends a password reset email to the specified email address.
     *
     * @param to           The recipient's email address.
     * @param subject      The subject of the email.
     * @param templateName The name of the email template.
     * @param resetLink    The password reset link to be sent.
     */
    void sendPasswordResetEmail(String to, String subject, String templateName, String resetLink);

}
