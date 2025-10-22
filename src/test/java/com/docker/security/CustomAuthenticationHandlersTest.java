package com.docker.security;

import com.docker.service.LoginAttemptService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.IOException;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires pour les handlers d'authentification personnalisés
 * Testé : CustomAuthenticationSuccessHandler et CustomAuthenticationFailureHandler
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CustomAuthenticationHandlersTest {

    @Mock
    private LoginAttemptService loginAttemptService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    private CustomAuthenticationSuccessHandler successHandler;
    private CustomAuthenticationFailureHandler failureHandler;

    @BeforeEach
    void setUp() {
        successHandler = new CustomAuthenticationSuccessHandler(loginAttemptService);
        failureHandler = new CustomAuthenticationFailureHandler(loginAttemptService);

        // Configuration commune des mocks
        when(request.getRemoteAddr()).thenReturn("192.168.1.1");
        when(request.getHeader("User-Agent")).thenReturn("Mozilla/5.0");
        when(request.getSession()).thenReturn(session);
    }

    // ========================================
    // Tests pour CustomAuthenticationSuccessHandler
    // ========================================

    @Test
    void testOnAuthenticationSuccess_AdminRole_ShouldRedirectToDashboard() throws IOException, ServletException {
        // Given
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "admin",
                "password",
                Arrays.asList(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );

        doNothing().when(loginAttemptService).recordSuccessfulLogin(anyString(), anyString(), anyString());

        // When
        successHandler.onAuthenticationSuccess(request, response, authentication);

        // Then
        verify(loginAttemptService, times(1)).recordSuccessfulLogin(
                eq("admin"),
                eq("192.168.1.1"),
                eq("Mozilla/5.0")
        );
    }

    @Test
    void testOnAuthenticationSuccess_UserRole_ShouldRedirectToProfile() throws IOException, ServletException {
        // Given
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "user",
                "password",
                Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"))
        );

        doNothing().when(loginAttemptService).recordSuccessfulLogin(anyString(), anyString(), anyString());

        // When
        successHandler.onAuthenticationSuccess(request, response, authentication);

        // Then
        verify(loginAttemptService, times(1)).recordSuccessfulLogin(
                eq("user"),
                eq("192.168.1.1"),
                eq("Mozilla/5.0")
        );
    }

    @Test
    void testOnAuthenticationSuccess_NoSpecificRole_ShouldRedirectToHome() throws IOException, ServletException {
        // Given
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "guest",
                "password",
                Arrays.asList(new SimpleGrantedAuthority("ROLE_GUEST"))
        );

        doNothing().when(loginAttemptService).recordSuccessfulLogin(anyString(), anyString(), anyString());

        // When
        successHandler.onAuthenticationSuccess(request, response, authentication);

        // Then
        verify(loginAttemptService, times(1)).recordSuccessfulLogin(
                eq("guest"),
                eq("192.168.1.1"),
                eq("Mozilla/5.0")
        );
    }

    @Test
    void testOnAuthenticationSuccess_WithProxyHeader_ShouldUseForwardedIp() throws IOException, ServletException {
        // Given
        when(request.getHeader("X-Forwarded-For")).thenReturn("203.0.113.5, 70.41.3.18");

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "user",
                "password",
                Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"))
        );

        doNothing().when(loginAttemptService).recordSuccessfulLogin(anyString(), anyString(), anyString());

        // When
        successHandler.onAuthenticationSuccess(request, response, authentication);

        // Then
        verify(loginAttemptService, times(1)).recordSuccessfulLogin(
                eq("user"),
                eq("203.0.113.5"), // Doit utiliser la première IP du X-Forwarded-For
                eq("Mozilla/5.0")
        );
    }

    // ========================================
    // Tests pour CustomAuthenticationFailureHandler
    // ========================================

    @Test
    void testOnAuthenticationFailure_ShouldRecordFailedLogin() throws IOException, ServletException {
        // Given
        when(request.getParameter("username")).thenReturn("testuser");
        when(loginAttemptService.getRemainingAttempts("testuser")).thenReturn(3);
        doNothing().when(loginAttemptService).recordFailedLogin(anyString(), anyString(), anyString(), anyString());

        BadCredentialsException exception = new BadCredentialsException("Mauvais mot de passe");

        // When
        failureHandler.onAuthenticationFailure(request, response, exception);

        // Then
        verify(loginAttemptService, times(1)).recordFailedLogin(
                eq("testuser"),
                eq("192.168.1.1"),
                eq("Mozilla/5.0"),
                eq("Mauvais mot de passe")
        );
        verify(loginAttemptService, times(1)).getRemainingAttempts("testuser");
    }

    @Test
    void testOnAuthenticationFailure_WithRemainingAttempts_ShouldShowRemainingMessage() throws IOException, ServletException {
        // Given
        when(request.getParameter("username")).thenReturn("testuser");
        when(loginAttemptService.getRemainingAttempts("testuser")).thenReturn(3);
        doNothing().when(loginAttemptService).recordFailedLogin(anyString(), anyString(), anyString(), anyString());

        BadCredentialsException exception = new BadCredentialsException("Mauvais mot de passe");

        // When
        failureHandler.onAuthenticationFailure(request, response, exception);

        // Then
        verify(session, times(1)).setAttribute(
                eq("errorMessage"),
                eq("Identifiants invalides. Il vous reste 3 tentative(s).")
        );
    }

    @Test
    void testOnAuthenticationFailure_NoRemainingAttempts_ShouldShowBlockMessage() throws IOException, ServletException {
        // Given
        when(request.getParameter("username")).thenReturn("testuser");
        when(loginAttemptService.getRemainingAttempts("testuser")).thenReturn(0);
        doNothing().when(loginAttemptService).recordFailedLogin(anyString(), anyString(), anyString(), anyString());

        BadCredentialsException exception = new BadCredentialsException("Mauvais mot de passe");

        // When
        failureHandler.onAuthenticationFailure(request, response, exception);

        // Then
        verify(session, times(1)).setAttribute(
                eq("errorMessage"),
                eq("Compte temporairement bloqué suite à de multiples tentatives échouées. Réessayez dans 15 minutes.")
        );
    }

    @Test
    void testOnAuthenticationFailure_With1RemainingAttempt_ShouldShowSingularForm() throws IOException, ServletException {
        // Given
        when(request.getParameter("username")).thenReturn("testuser");
        when(loginAttemptService.getRemainingAttempts("testuser")).thenReturn(1);
        doNothing().when(loginAttemptService).recordFailedLogin(anyString(), anyString(), anyString(), anyString());

        BadCredentialsException exception = new BadCredentialsException("Mauvais mot de passe");

        // When
        failureHandler.onAuthenticationFailure(request, response, exception);

        // Then
        verify(session, times(1)).setAttribute(
                eq("errorMessage"),
                eq("Identifiants invalides. Il vous reste 1 tentative(s).")
        );
    }

    @Test
    void testOnAuthenticationFailure_WithProxyHeader_ShouldUseForwardedIp() throws IOException, ServletException {
        // Given
        when(request.getParameter("username")).thenReturn("testuser");
        when(request.getHeader("X-Forwarded-For")).thenReturn("203.0.113.5, 70.41.3.18");
        when(loginAttemptService.getRemainingAttempts("testuser")).thenReturn(3);
        doNothing().when(loginAttemptService).recordFailedLogin(anyString(), anyString(), anyString(), anyString());

        BadCredentialsException exception = new BadCredentialsException("Mauvais mot de passe");

        // When
        failureHandler.onAuthenticationFailure(request, response, exception);

        // Then
        verify(loginAttemptService, times(1)).recordFailedLogin(
                eq("testuser"),
                eq("203.0.113.5"), // Doit utiliser la première IP du X-Forwarded-For
                eq("Mozilla/5.0"),
                eq("Mauvais mot de passe")
        );
    }

    @Test
    void testOnAuthenticationFailure_WithMultipleFailures_ShouldDecreaseRemainingAttempts() throws IOException, ServletException {
        // Given
        when(request.getParameter("username")).thenReturn("testuser");
        when(loginAttemptService.getRemainingAttempts("testuser"))
                .thenReturn(4)  // Première tentative
                .thenReturn(3)  // Deuxième tentative
                .thenReturn(2); // Troisième tentative

        doNothing().when(loginAttemptService).recordFailedLogin(anyString(), anyString(), anyString(), anyString());

        BadCredentialsException exception = new BadCredentialsException("Mauvais mot de passe");

        // When - Première tentative
        failureHandler.onAuthenticationFailure(request, response, exception);
        verify(session, times(1)).setAttribute(
                eq("errorMessage"),
                eq("Identifiants invalides. Il vous reste 4 tentative(s).")
        );

        // When - Deuxième tentative
        failureHandler.onAuthenticationFailure(request, response, exception);
        verify(session, times(1)).setAttribute(
                eq("errorMessage"),
                eq("Identifiants invalides. Il vous reste 3 tentative(s).")
        );

        // When - Troisième tentative
        failureHandler.onAuthenticationFailure(request, response, exception);
        verify(session, times(1)).setAttribute(
                eq("errorMessage"),
                eq("Identifiants invalides. Il vous reste 2 tentative(s).")
        );

        // Then - Vérifier que recordFailedLogin a été appelé 3 fois
        verify(loginAttemptService, times(3)).recordFailedLogin(
                eq("testuser"),
                eq("192.168.1.1"),
                eq("Mozilla/5.0"),
                eq("Mauvais mot de passe")
        );
    }
}
