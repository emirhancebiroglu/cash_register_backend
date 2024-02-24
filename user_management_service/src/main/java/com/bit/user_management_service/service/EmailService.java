package com.bit.user_management_service.service;

public interface EmailService {
    void sendEmail(String to, String subject, String templateName, String userCode, String userPassword,
                                   String firstName, String lastName);

    void sendEmail(String to, String subject, String templateName, String userCode,
                                   String firstName, String lastName);

    void sendEmail(String to, String subject, String templateName,
                   String firstName, String lastName);
}
