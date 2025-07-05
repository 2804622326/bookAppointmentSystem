package com.dailycodework.universalpetcare.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.core.AuthenticationException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtAuthEntryPointTest {


    private JwtAuthEntryPoint entryPoint;

    @BeforeEach
    void setUp() {
        entryPoint = new JwtAuthEntryPoint();
    }

    @Test
    void commence_shouldReturn401JsonResponse() throws IOException, ServletException {
        // Mock request, response, and exception
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        AuthenticationException authException = mock(AuthenticationException.class);

        // Define expected values
        String servletPath = "/api/test";
        String exceptionMessage = "Invalid token";

        when(request.getServletPath()).thenReturn(servletPath);
        when(authException.getMessage()).thenReturn(exceptionMessage);

        // Capture output stream
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ServletOutputStream servletOutputStream = new ServletOutputStream() {
            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setWriteListener(WriteListener writeListener) {

            }

            @Override
            public void write(int b) {
                outputStream.write(b);
            }
        };

        when(response.getOutputStream()).thenReturn(servletOutputStream);

        // Act
        entryPoint.commence(request, response, authException);

        // Verify response status and content-type
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(response).setContentType("application/json");

        // Deserialize JSON result
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> json = mapper.readValue(outputStream.toByteArray(), Map.class);

        // Assert JSON content
        assertEquals(401, json.get("status"));
        assertEquals("Unauthorized", json.get("error"));
        assertEquals("Invalid token", json.get("message"));
        assertEquals("/api/test", json.get("path"));
    }
}