package com.docker.config;

import com.docker.entity.User;
import com.docker.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Tests pour les classes de configuration
 * Améliore la couverture du package config de 41% à >50%
 */
@ExtendWith(MockitoExtension.class)
class ConfigClassesTest {

    @Mock
    private UserService userService;

    @Mock
    private FilterChain filterChain;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    private UserControllerAdvice userControllerAdvice;
    private GlobalControllerAdvice globalControllerAdvice;
    private CorsFilter corsFilter;

    @BeforeEach
    void setUp() {
        userControllerAdvice = new UserControllerAdvice(userService);
        globalControllerAdvice = new GlobalControllerAdvice();
        corsFilter = new CorsFilter();
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testUserControllerAdvice_AuthenticatedUser() {
        // Arrange
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/some/page");

        User mockUser = new User();
        mockUser.setUsername("testuser");

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn("testuser");
        when(authentication.getName()).thenReturn("testuser");
        when(userService.findByUsername("testuser")).thenReturn(mockUser);

        // Act
        User result = userControllerAdvice.getCurrentUser(request);

        // Assert
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(userService).findByUsername("testuser");
    }

    @Test
    void testUserControllerAdvice_UnauthenticatedUser() {
        // Arrange
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/some/page");

        when(securityContext.getAuthentication()).thenReturn(null);

        // Act
        User result = userControllerAdvice.getCurrentUser(request);

        // Assert
        assertNull(result);
        verify(userService, never()).findByUsername(anyString());
    }

    @Test
    void testUserControllerAdvice_AnonymousUser() {
        // Arrange
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/some/page");

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn("anonymousUser");

        // Act
        User result = userControllerAdvice.getCurrentUser(request);

        // Assert
        assertNull(result);
        verify(userService, never()).findByUsername(anyString());
    }

    @Test
    void testUserControllerAdvice_ErrorPage() {
        // Arrange
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/error/404");

        // Act
        User result = userControllerAdvice.getCurrentUser(request);

        // Assert
        assertNull(result);
        verify(userService, never()).findByUsername(anyString());
    }

    @Test
    void testUserControllerAdvice_UploadedResourcePage() {
        // Arrange
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/uploaded-avatars/test.jpg");

        // Act
        User result = userControllerAdvice.getCurrentUser(request);

        // Assert
        assertNull(result);
        verify(userService, never()).findByUsername(anyString());
    }

    @Test
    void testGlobalControllerAdvice_CurrentUri() {
        // Arrange
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/test/page");

        // Act
        String result = globalControllerAdvice.getCurrentUri(request);

        // Assert
        assertEquals("/test/page", result);
    }

    @Test
    void testGlobalControllerAdvice_RootUri() {
        // Arrange
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/");

        // Act
        String result = globalControllerAdvice.getCurrentUri(request);

        // Assert
        assertEquals("/", result);
    }

    @Test
    void testCorsFilter_AllowedOrigin_Localhost5173() throws ServletException, IOException {
        // Arrange
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Origin", "http://localhost:5173");
        request.setMethod("GET");
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Act
        corsFilter.doFilter(request, response, filterChain);

        // Assert
        assertEquals("http://localhost:5173", response.getHeader("Access-Control-Allow-Origin"));
        assertEquals("true", response.getHeader("Access-Control-Allow-Credentials"));
        assertEquals("GET, POST, PUT, DELETE, OPTIONS", response.getHeader("Access-Control-Allow-Methods"));
        assertEquals("3600", response.getHeader("Access-Control-Max-Age"));
        assertNotNull(response.getHeader("Access-Control-Allow-Headers"));
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testCorsFilter_AllowedOrigin_Localhost8106() throws ServletException, IOException {
        // Arrange
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Origin", "http://localhost:8106");
        request.setMethod("POST");
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Act
        corsFilter.doFilter(request, response, filterChain);

        // Assert
        assertEquals("http://localhost:8106", response.getHeader("Access-Control-Allow-Origin"));
        assertEquals("true", response.getHeader("Access-Control-Allow-Credentials"));
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testCorsFilter_DisallowedOrigin() throws ServletException, IOException {
        // Arrange
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Origin", "http://malicious-site.com");
        request.setMethod("GET");
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Act
        corsFilter.doFilter(request, response, filterChain);

        // Assert
        assertNull(response.getHeader("Access-Control-Allow-Origin"));
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testCorsFilter_NoOriginHeader() throws ServletException, IOException {
        // Arrange
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("GET");
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Act
        corsFilter.doFilter(request, response, filterChain);

        // Assert
        assertNull(response.getHeader("Access-Control-Allow-Origin"));
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testCorsFilter_OptionsRequest() throws ServletException, IOException {
        // Arrange
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Origin", "http://localhost:5173");
        request.setMethod("OPTIONS");
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Act
        corsFilter.doFilter(request, response, filterChain);

        // Assert
        assertEquals("http://localhost:5173", response.getHeader("Access-Control-Allow-Origin"));
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    void testCorsFilter_AllowsAllHttpMethods() throws ServletException, IOException {
        // Arrange
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Origin", "http://localhost:5173");
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Act & Assert for each method
        for (String method : new String[]{"GET", "POST", "PUT", "DELETE"}) {
            request.setMethod(method);
            corsFilter.doFilter(request, response, filterChain);

            String allowedMethods = response.getHeader("Access-Control-Allow-Methods");
            assertTrue(allowedMethods.contains(method), "Should allow " + method);
        }
    }

    @Test
    void testCorsFilter_ContentTypeInAllowedHeaders() throws ServletException, IOException {
        // Arrange
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Origin", "http://localhost:5173");
        request.setMethod("POST");
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Act
        corsFilter.doFilter(request, response, filterChain);

        // Assert
        String allowedHeaders = response.getHeader("Access-Control-Allow-Headers");
        assertNotNull(allowedHeaders);
        assertTrue(allowedHeaders.contains("Content-Type"));
        assertTrue(allowedHeaders.contains("Authorization"));
    }
}
