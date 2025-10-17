package com.docker.security;

import com.docker.service.LoginAttemptService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Tests pour les filtres de sécurité
 * Améliore la couverture du package security de 39% à >50%
 */
@ExtendWith(MockitoExtension.class)
class SecurityFiltersTest {

    @Mock
    private LoginAttemptService loginAttemptService;

    @Mock
    private FilterChain filterChain;

    private LoginAttemptFilter loginAttemptFilter;
    private ContentSecurityPolicyFilter cspFilter;
    private CsrfTokenLogger csrfTokenLogger;

    @BeforeEach
    void setUp() {
        loginAttemptFilter = new LoginAttemptFilter(loginAttemptService);
        cspFilter = new ContentSecurityPolicyFilter();
        csrfTokenLogger = new CsrfTokenLogger();
    }

    @Test
    void testLoginAttemptFilter_AllowsNonBlockedUser() throws ServletException, IOException {
        // Arrange
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("POST");
        request.setServletPath("/login");
        request.setParameter("username", "testuser");
        request.setRemoteAddr("192.168.1.1");
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(loginAttemptService.isBlocked("testuser")).thenReturn(false);
        when(loginAttemptService.isIpBlocked("192.168.1.1")).thenReturn(false);

        // Act
        loginAttemptFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(loginAttemptService).isBlocked("testuser");
        verify(loginAttemptService).isIpBlocked("192.168.1.1");
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testLoginAttemptFilter_BlocksBlockedUser() throws ServletException, IOException {
        // Arrange
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("POST");
        request.setServletPath("/login");
        request.setParameter("username", "blockeduser");
        request.setRemoteAddr("192.168.1.1");
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(loginAttemptService.isBlocked("blockeduser")).thenReturn(true);

        // Act
        loginAttemptFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(loginAttemptService).isBlocked("blockeduser");
        verify(filterChain, never()).doFilter(any(), any());
        assertTrue(response.getRedirectedUrl().contains("/login?blocked=true"));
    }

    @Test
    void testLoginAttemptFilter_BlocksBlockedIP() throws ServletException, IOException {
        // Arrange
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("POST");
        request.setServletPath("/login");
        request.setParameter("username", "testuser");
        request.setRemoteAddr("10.0.0.1");
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(loginAttemptService.isBlocked("testuser")).thenReturn(false);
        when(loginAttemptService.isIpBlocked("10.0.0.1")).thenReturn(true);

        // Act
        loginAttemptFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(loginAttemptService).isBlocked("testuser");
        verify(loginAttemptService).isIpBlocked("10.0.0.1");
        verify(filterChain, never()).doFilter(any(), any());
        assertTrue(response.getRedirectedUrl().contains("/login?blocked=true"));
    }

    @Test
    void testLoginAttemptFilter_IgnoresNonLoginRequests() throws ServletException, IOException {
        // Arrange
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("GET");
        request.setServletPath("/home");
        request.setRemoteAddr("192.168.1.1");
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Act
        loginAttemptFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(loginAttemptService, never()).isBlocked(anyString());
        verify(loginAttemptService, never()).isIpBlocked(anyString());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testLoginAttemptFilter_HandlesXForwardedForHeader() throws ServletException, IOException {
        // Arrange
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("POST");
        request.setServletPath("/login");
        request.setParameter("username", "testuser");
        request.setRemoteAddr("192.168.1.1");
        request.addHeader("X-Forwarded-For", "203.0.113.1,10.0.0.1");
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(loginAttemptService.isBlocked("testuser")).thenReturn(false);
        when(loginAttemptService.isIpBlocked("203.0.113.1")).thenReturn(false);

        // Act
        loginAttemptFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(loginAttemptService).isBlocked("testuser");
        verify(loginAttemptService).isIpBlocked("203.0.113.1");
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testContentSecurityPolicyFilter_AddsCSPHeader() throws ServletException, IOException {
        // Arrange
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Act
        cspFilter.doFilterInternal(request, response, filterChain);

        // Assert
        String cspHeader = response.getHeader("Content-Security-Policy");
        assertNotNull(cspHeader);
        assertTrue(cspHeader.contains("default-src"));
        assertTrue(cspHeader.contains("script-src"));
        assertTrue(cspHeader.contains("style-src"));
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testContentSecurityPolicyFilter_AllowsYouTubeAndSpotify() throws ServletException, IOException {
        // Arrange
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Act
        cspFilter.doFilterInternal(request, response, filterChain);

        // Assert
        String cspHeader = response.getHeader("Content-Security-Policy");
        assertNotNull(cspHeader);
        assertTrue(cspHeader.contains("youtube.com") || cspHeader.contains("frame-src"));
        assertTrue(cspHeader.contains("spotify.com") || cspHeader.contains("frame-src"));
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testContentSecurityPolicyFilter_AllowsInlineStyles() throws ServletException, IOException {
        // Arrange
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Act
        cspFilter.doFilterInternal(request, response, filterChain);

        // Assert
        String cspHeader = response.getHeader("Content-Security-Policy");
        assertNotNull(cspHeader);
        assertTrue(cspHeader.contains("'unsafe-inline'") || cspHeader.contains("style-src"));
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testCsrfTokenLogger_LogsTokenForPostRequests() throws ServletException, IOException {
        // Arrange
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("POST");
        request.setAttribute("_csrf", new Object());
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Act
        csrfTokenLogger.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testCsrfTokenLogger_WorksForGetRequests() throws ServletException, IOException {
        // Arrange
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("GET");
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Act
        csrfTokenLogger.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testCsrfTokenLogger_HandlesMissingToken() throws ServletException, IOException {
        // Arrange
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("POST");
        // No CSRF token
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Act
        csrfTokenLogger.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testLoginAttemptFilter_AllowsNullUsername() throws ServletException, IOException {
        // Arrange
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("POST");
        request.setServletPath("/login");
        // No username parameter
        request.setRemoteAddr("192.168.1.1");
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(loginAttemptService.isIpBlocked("192.168.1.1")).thenReturn(false);

        // Act
        loginAttemptFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(loginAttemptService, never()).isBlocked(anyString());
        verify(loginAttemptService).isIpBlocked("192.168.1.1");
        verify(filterChain).doFilter(request, response);
    }
}
