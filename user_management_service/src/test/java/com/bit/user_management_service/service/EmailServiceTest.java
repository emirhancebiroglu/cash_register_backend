package com.bit.user_management_service.service;

import com.bit.user_management_service.service.serviceImpl.EmailServiceImpl;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmailServiceTest {
    private static final String TO = "test@test.com";
    private static final String SUBJECT = "testSubject";
    private static final String TEMPLATE_NAME = "testTemplate";
    private static final String USER_CODE = "testUserCode";
    private static final String USER_PASSWORD = "testUserPassword";
    private static final String FIRST_NAME = "testFirstName";
    private static final String LAST_NAME = "testLastName";

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private TemplateEngine templateEngine;

    @InjectMocks
    private EmailServiceImpl emailService;

    @Test
    public void sendEmail(){
        ReflectionTestUtils.setField(emailService, "mailSender", mailSender);
        ReflectionTestUtils.setField(emailService, "templateEngine", templateEngine);

        when(templateEngine.process(eq(TEMPLATE_NAME), any(Context.class))).thenReturn("test html content");
        when(mailSender.createMimeMessage()).thenReturn(mock(MimeMessage.class));

        emailService.sendEmail(TO, SUBJECT, TEMPLATE_NAME, USER_CODE, USER_PASSWORD, FIRST_NAME, LAST_NAME);

        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }
}