package com.dailycodework.universalpetcare.event.listener;

import com.dailycodework.universalpetcare.email.EmailService;
import com.dailycodework.universalpetcare.event.*;
import com.dailycodework.universalpetcare.model.Appointment;
import com.dailycodework.universalpetcare.model.User;
import com.dailycodework.universalpetcare.service.token.IVerificationTokenService;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.UnsupportedEncodingException;

import static org.mockito.Mockito.*;

class NotificationEventListenerTest {

    @Mock
    private EmailService emailService;

    @Mock
    private IVerificationTokenService tokenService;

    @InjectMocks
    private NotificationEventListener listener;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(listener, "frontendBaseUrl", "http://localhost:3000");
    }

    @Test
    void testOnRegistrationCompleteEvent() throws MessagingException, UnsupportedEncodingException {
        User user = new User();
        user.setFirstName("John");
        user.setEmail("john@example.com");

        RegistrationCompleteEvent event = new RegistrationCompleteEvent(user);

        listener.onApplicationEvent(event);

        verify(tokenService).saveVerificationTokenForUser(anyString(), eq(user));
        verify(emailService).sendEmail(eq(user.getEmail()), anyString(), anyString(), contains("Verify your email"));
    }

    @Test
    void testOnAppointmentBookedEvent() throws MessagingException, UnsupportedEncodingException {
        User vet = new User();
        vet.setFirstName("Dr. Smith");
        vet.setEmail("vet@example.com");

        Appointment appointment = new Appointment();
        appointment.setVeterinarian(vet);

        AppointmentBookedEvent event = new AppointmentBookedEvent(appointment);

        listener.onApplicationEvent(event);

        verify(emailService).sendEmail(eq(vet.getEmail()), anyString(), anyString(), contains("new appointment"));
    }

    @Test
    void testOnAppointmentApprovedEvent() throws MessagingException, UnsupportedEncodingException {
        User patient = new User();
        patient.setFirstName("Alice");
        patient.setEmail("alice@example.com");

        Appointment appointment = new Appointment();
        appointment.setPatient(patient);

        AppointmentApprovedEvent event = new AppointmentApprovedEvent(appointment);

        listener.onApplicationEvent(event);

        verify(emailService).sendEmail(eq(patient.getEmail()), anyString(), anyString(), contains("approved"));
    }

    @Test
    void testOnAppointmentDeclinedEvent() throws MessagingException, UnsupportedEncodingException {
        User patient = new User();
        patient.setFirstName("Bob");
        patient.setEmail("bob@example.com");

        Appointment appointment = new Appointment();
        appointment.setPatient(patient);

        AppointmentDeclinedEvent event = new AppointmentDeclinedEvent(appointment);

        listener.onApplicationEvent(event);

        verify(emailService).sendEmail(eq(patient.getEmail()), anyString(), anyString(), contains("not approved"));
    }

    @Test
    void testOnPasswordResetEvent() throws MessagingException, UnsupportedEncodingException {
        User user = new User();
        user.setFirstName("Eve");
        user.setEmail("eve@example.com");

        PasswordResetEvent event = new PasswordResetEvent(this, user);

        listener.onApplicationEvent(event);

        verify(tokenService).saveVerificationTokenForUser(anyString(), eq(user));
        verify(emailService).sendEmail(eq(user.getEmail()), anyString(), anyString(), contains("reset your password"));
    }
}