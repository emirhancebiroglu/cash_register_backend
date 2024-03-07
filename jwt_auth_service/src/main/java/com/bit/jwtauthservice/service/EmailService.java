package com.bit.jwtauthservice.service;

public interface EmailService {
    void sendUserCode(String to, String subject, String templateName, String userCode);
    void sendPasswordResetEmail(String to, String subject, String templateName, String resetLink);

}
