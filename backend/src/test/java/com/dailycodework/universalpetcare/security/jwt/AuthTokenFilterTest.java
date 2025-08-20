package com.dailycodework.universalpetcare.security.jwt;

import com.dailycodework.universalpetcare.security.user.UPCUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthTokenFilterTest {

    private AuthTokenFilter authTokenFilter;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private UPCUserDetailsService userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authTokenFilter = new AuthTokenFilter();
        authTokenFilter.jwtUtils = jwtUtils;
        authTokenFilter.userDetailsService = userDetailsService;
        SecurityContextHolder.clearContext();

    }

    @Test
    void testDoFilterInternal_validToken_setsAuthentication() throws ServletException, IOException {
        String jwt = "valid.jwt.token";
        String email = "test@example.com";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + jwt);
        when(jwtUtils.validateToken(jwt)).thenReturn(true);
        when(jwtUtils.getUserNameFromToken(jwt)).thenReturn(email);
        when(userDetailsService.loadUserByUsername(email)).thenReturn(userDetails);
        when(userDetails.getAuthorities()).thenReturn(null);

        authTokenFilter.doFilterInternal(request, response, filterChain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_noToken_doesNothing() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn(null);

        authTokenFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_invalidToken_throwsServletException() {
        String jwt = "bad.jwt.token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + jwt);
        when(jwtUtils.validateToken(jwt)).thenThrow(new RuntimeException("Token invalid"));

        ServletException exception = assertThrows(ServletException.class, () ->
                authTokenFilter.doFilterInternal(request, response, filterChain));

        assertTrue(exception.getMessage().contains("Token invalid"));
    }
}
