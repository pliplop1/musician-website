package com.docker.controller;

import com.docker.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests unitaires pour LegalController
 * Teste tous les endpoints des pages légales (RGPD, mentions légales, cookies)
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class LegalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // Mock des services utilisés par @ControllerAdvice
    @MockBean
    private CommentService commentService;

    @MockBean
    private SocialLinkService socialLinkService;

    @MockBean
    private LoginAttemptService loginAttemptService;

    // ========================================================================
    // TESTS DES PAGES LÉGALES
    // ========================================================================

    @Test
    @WithAnonymousUser
    void testPrivacyPolicyPage_ReturnsPrivacyPolicyView() throws Exception {
        mockMvc.perform(get("/privacy-policy"))
                .andExpect(status().isOk())
                .andExpect(view().name("legal/privacy-policy"));
    }

    @Test
    @WithAnonymousUser
    void testMentionsLegalesPage_ReturnsMentionsLegalesView() throws Exception {
        mockMvc.perform(get("/mentions-legales"))
                .andExpect(status().isOk())
                .andExpect(view().name("legal/mentions-legales"));
    }

    @Test
    @WithAnonymousUser
    void testCookiesPage_ReturnsCookiesView() throws Exception {
        mockMvc.perform(get("/cookies"))
                .andExpect(status().isOk())
                .andExpect(view().name("legal/cookies"));
    }

    // ========================================================================
    // TESTS D'ACCESSIBILITÉ PUBLIQUE
    // ========================================================================

    @Test
    @WithAnonymousUser
    void testAllLegalPages_ArePubliclyAccessible() throws Exception {
        // Vérifier que toutes les pages légales sont accessibles sans authentification
        mockMvc.perform(get("/privacy-policy"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/mentions-legales"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/cookies"))
                .andExpect(status().isOk());
    }

    // ========================================================================
    // TESTS DE SÉCURITÉ
    // ========================================================================

    @Test
    @WithAnonymousUser
    void testPrivacyPolicyPage_HasSecurityHeaders() throws Exception {
        mockMvc.perform(get("/privacy-policy"))
                .andExpect(status().isOk())
                .andExpect(header().exists("X-Content-Type-Options"))
                .andExpect(header().exists("X-Frame-Options"))
                .andExpect(header().exists("X-XSS-Protection"));
    }

    @Test
    @WithAnonymousUser
    void testMentionsLegalesPage_HasSecurityHeaders() throws Exception {
        mockMvc.perform(get("/mentions-legales"))
                .andExpect(status().isOk())
                .andExpect(header().exists("X-Content-Type-Options"))
                .andExpect(header().exists("X-Frame-Options"))
                .andExpect(header().exists("X-XSS-Protection"));
    }

    @Test
    @WithAnonymousUser
    void testCookiesPage_HasSecurityHeaders() throws Exception {
        mockMvc.perform(get("/cookies"))
                .andExpect(status().isOk())
                .andExpect(header().exists("X-Content-Type-Options"))
                .andExpect(header().exists("X-Frame-Options"))
                .andExpect(header().exists("X-XSS-Protection"));
    }
}
