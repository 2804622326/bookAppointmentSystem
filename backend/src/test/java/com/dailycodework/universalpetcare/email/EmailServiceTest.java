package com.dailycodework.universalpetcare.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.UnsupportedEncodingException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender javaMailSender;

    @InjectMocks
    private EmailService emailService;

    @Mock
    private MimeMessage mimeMessage;

    @BeforeEach
    void setUp() {
        // Inject the mock JavaMailSender into the EmailService
        ReflectionTestUtils.setField(emailService, "mailSender", javaMailSender);
    }

    @Test
    void testSendEmail_Success() throws MessagingException, UnsupportedEncodingException {
        // Given
        String to = "test@example.com";
        String subject = "Test Subject";
        String senderName = "Test Sender";
        String mailContent = "<h1>Hello World</h1>";

        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

        // When
        emailService.sendEmail(to, subject, senderName, mailContent);

        // Then
        verify(javaMailSender, times(1)).createMimeMessage();
        verify(javaMailSender, times(1)).send(mimeMessage);
    }

    @Test
    void testSendEmail_WithPlainText() throws MessagingException, UnsupportedEncodingException {
        // Given
        String to = "plain@example.com";
        String subject = "Plain Subject";
        String senderName = "Plain Sender";
        String mailContent = "Plain text content";

        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

        // When
        emailService.sendEmail(to, subject, senderName, mailContent);

        // Then
        verify(javaMailSender, times(1)).createMimeMessage();
        verify(javaMailSender, times(1)).send(mimeMessage);
    }

    @Test
    void testSendEmail_WithEmptyContent() throws MessagingException, UnsupportedEncodingException {
        // Given
        String to = "empty@example.com";
        String subject = "Empty Subject";
        String senderName = "Empty Sender";
        String mailContent = "";

        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

        // When
        emailService.sendEmail(to, subject, senderName, mailContent);

        // Then
        verify(javaMailSender, times(1)).createMimeMessage();
        verify(javaMailSender, times(1)).send(mimeMessage);
    }

    @Test
    void testSendEmail_ThrowsMessagingException() {
        // Given
        String to = "error@example.com";
        String subject = "Error Subject";
        String senderName = "Error Sender";
        String mailContent = "Error content";

        doThrow(new RuntimeException("Email error")).when(javaMailSender).send(any(MimeMessage.class));
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

        // When & Then
        assertThrows(RuntimeException.class, () -> 
            emailService.sendEmail(to, subject, senderName, mailContent));
    }

    @Test
    void testEmailService_Instantiation() {
        // Test that the service can be instantiated
        assertNotNull(emailService);
    }
}