package com.dailycodework.universalpetcare.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import java.io.UnsupportedEncodingException;

import static org.mockito.Mockito.*;

class EmailServiceTest {

    private JavaMailSender javaMailSender;
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        javaMailSender = mock(JavaMailSender.class);
        emailService = new EmailService() {
            @Override
            public void sendEmail(String to, String subject, String senderName, String mailContent)
                    throws MessagingException, UnsupportedEncodingException {
                MimeMessage message = javaMailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message);
                helper.setFrom(EmailProperties.DEFAULT_USERNAME, senderName);
                helper.setTo(to);
                helper.setSubject(subject);
                helper.setText(mailContent, true);
                javaMailSender.send(message);
            }


        };
    }

    @Test
    void testSendEmail() throws MessagingException, UnsupportedEncodingException {
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailService.sendEmail("test@example.com", "Subject", "Sender", "<h1>Hello</h1>");

        verify(javaMailSender, times(1)).createMimeMessage();
        verify(javaMailSender, times(1)).send(mimeMessage);
    }
}