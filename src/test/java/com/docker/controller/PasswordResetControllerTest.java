package com.docker.controller;

import com.docker.entity.PasswordResetToken;
import com.docker.service.CommentService;
import com.docker.service.LoginAttemptService;
import com.docker.service.PasswordResetService;
import com.docker.service.PasswordValidationService;
import com.docker.service.SocialLinkService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests unitaires pour PasswordResetController
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PasswordResetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PasswordResetService passwordResetService;

    @MockBean
    private PasswordValidationService passwordValidationService;

    // Mock des services utilisés par @ControllerAdvice
    @MockBean
    private CommentService commentService;

    @MockBean
    private SocialLinkService socialLinkService;

    @MockBean
    private LoginAttemptService loginAttemptService;

    @BeforeEach
    void setUp() {
        reset(passwordResetService, passwordValidationService);
    }

    // Tests pour /forgot-password GET
    @Test
    @WithAnonymousUser
    void testShowForgotPasswordForm_ReturnsView() throws Exception {
        mockMvc.perform(get("/forgot-password"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/forgot-password"));
    }

    // Tests pour /forgot-password POST
    @Test
    @WithAnonymousUser
    void testProcessForgotPassword_WithSuccess_RedirectsWithSuccess() throws Exception {
        when(passwordResetService.createPasswordResetTokenForUser("user@example.com")).thenReturn(true);

        mockMvc.perform(post("/forgot-password")
                .param("email", "user@example.com")
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/forgot-password"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(passwordResetService, times(1)).createPasswordResetTokenForUser("user@example.com");
    }

    @Test
    @WithAnonymousUser
    void testProcessForgotPassword_WithFailure_RedirectsWithError() throws Exception {
        when(passwordResetService.createPasswordResetTokenForUser("nonexistent@example.com")).thenReturn(false);

        mockMvc.perform(post("/forgot-password")
                .param("email", "nonexistent@example.com")
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/forgot-password"))
                .andExpect(flash().attributeExists("errorMessage"));

        verify(passwordResetService, times(1)).createPasswordResetTokenForUser("nonexistent@example.com");
    }

    // Tests pour /reset-password GET
    @Test
    @WithAnonymousUser
    void testShowResetPasswordForm_WithValidToken_ReturnsView() throws Exception {
        PasswordResetToken token = new PasswordResetToken();
        when(passwordResetService.validatePasswordResetToken("validToken")).thenReturn(Optional.of(token));

        mockMvc.perform(get("/reset-password")
                .param("token", "validToken"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/reset-password"))
                .andExpect(model().attribute("token", "validToken"));

        verify(passwordResetService, times(1)).validatePasswordResetToken("validToken");
    }

    @Test
    @WithAnonymousUser
    void testShowResetPasswordForm_WithInvalidToken_RedirectsToForgotPassword() throws Exception {
        when(passwordResetService.validatePasswordResetToken("invalidToken")).thenReturn(Optional.empty());

        mockMvc.perform(get("/reset-password")
                .param("token", "invalidToken"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/forgot-password"))
                .andExpect(flash().attributeExists("errorMessage"));

        verify(passwordResetService, times(1)).validatePasswordResetToken("invalidToken");
    }

    // Tests pour /reset-password POST
    @Test
    @WithAnonymousUser
    void testProcessResetPassword_WithValidData_RedirectsToLogin() throws Exception {
        when(passwordValidationService.getValidationErrorMessage("newPassword123")).thenReturn(null);
        when(passwordResetService.resetPassword("validToken", "newPassword123")).thenReturn(true);

        mockMvc.perform(post("/reset-password")
                .param("token", "validToken")
                .param("password", "newPassword123")
                .param("confirmPassword", "newPassword123")
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(passwordResetService, times(1)).resetPassword("validToken", "newPassword123");
    }

    @Test
    @WithAnonymousUser
    void testProcessResetPassword_WithInvalidPassword_RedirectsWithError() throws Exception {
        when(passwordValidationService.getValidationErrorMessage("weak")).thenReturn("Le mot de passe est trop faible");

        mockMvc.perform(post("/reset-password")
                .param("token", "validToken")
                .param("password", "weak")
                .param("confirmPassword", "weak")
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/reset-password?token=validToken"))
                .andExpect(flash().attributeExists("errorMessage"));

        verify(passwordResetService, never()).resetPassword(anyString(), anyString());
    }

    @Test
    @WithAnonymousUser
    void testProcessResetPassword_WithMismatchedPasswords_RedirectsWithError() throws Exception {
        when(passwordValidationService.getValidationErrorMessage("newPassword123")).thenReturn(null);

        mockMvc.perform(post("/reset-password")
                .param("token", "validToken")
                .param("password", "newPassword123")
                .param("confirmPassword", "differentPassword")
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/reset-password?token=validToken"))
                .andExpect(flash().attributeExists("errorMessage"));

        verify(passwordResetService, never()).resetPassword(anyString(), anyString());
    }

    @Test
    @WithAnonymousUser
    void testProcessResetPassword_WithExpiredToken_RedirectsToForgotPassword() throws Exception {
        when(passwordValidationService.getValidationErrorMessage("newPassword123")).thenReturn(null);
        when(passwordResetService.resetPassword("expiredToken", "newPassword123")).thenReturn(false);

        mockMvc.perform(post("/reset-password")
                .param("token", "expiredToken")
                .param("password", "newPassword123")
                .param("confirmPassword", "newPassword123")
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/forgot-password"))
                .andExpect(flash().attributeExists("errorMessage"));

        verify(passwordResetService, times(1)).resetPassword("expiredToken", "newPassword123");
    }

    @Test
    @WithAnonymousUser
    void testForgotPasswordPost_WithoutCsrfToken_ReturnsForbidden() throws Exception {
        mockMvc.perform(post("/forgot-password")
                .param("email", "user@example.com"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    void testResetPasswordPost_WithoutCsrfToken_ReturnsForbidden() throws Exception {
        mockMvc.perform(post("/reset-password")
                .param("token", "validToken")
                .param("password", "newPassword123")
                .param("confirmPassword", "newPassword123"))
                .andExpect(status().isForbidden());
    }
}
