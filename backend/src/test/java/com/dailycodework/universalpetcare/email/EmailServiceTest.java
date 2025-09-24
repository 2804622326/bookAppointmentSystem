package com.dailycodework.universalpetcare.email;

import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailPreparationException;
import org.springframework.mail.javamail.JavaMailSender;

import java.io.UnsupportedEncodingException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    private EmailService emailService;

    @BeforeEach
    void setUp() {
        emailService = new EmailService(mailSender);
    }

    @Test
    void sendEmail_whenMailSenderCreatesMessage_sendsSuccessfully() throws MessagingException, UnsupportedEncodingException {
        MimeMessage mimeMessage = new MimeMessage((Session) null);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailService.sendEmail("to@example.com", "Subject", "Sender", "<p>Body</p>");

        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }

    @Test
    void sendEmail_whenMailSenderThrowsMailException_wrapsInEmailSendException()
            throws MessagingException, UnsupportedEncodingException {
        when(mailSender.createMimeMessage()).thenThrow(new MailPreparationException("boom"));

        assertThatThrownBy(() -> emailService.sendEmail("to@example.com", "Subject", "Sender", "Body"))
                .isInstanceOf(EmailSendException.class)
                .hasMessageContaining("Failed to send email");
    }

    @Test
    void sendEmail_whenMailSenderNotInjected_throwsIllegalStateException() {
        EmailService serviceWithoutSender = new EmailService(null);

        assertThatThrownBy(() -> serviceWithoutSender.sendEmail("a", "b", "c", "d"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("JavaMailSender");
    }
}
