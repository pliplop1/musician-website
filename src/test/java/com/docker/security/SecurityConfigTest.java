package com.docker.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests de sécurité pour vérifier les règles d'accès
 * Vérifie que Spring Security est correctement configuré
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    // ===========================================
    // Tests des endpoints publics
    // ===========================================

    @Test
    @WithAnonymousUser
    void testPublicEndpoint_Homepage_AccessibleWithoutAuth() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser
    void testPublicApiEndpoint_AccessibleWithoutAuth() throws Exception {
        mockMvc.perform(get("/api/public/tracks"))
                .andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser
    void testStaticResources_AccessibleWithoutAuth() throws Exception {
        mockMvc.perform(get("/css/style.css"))
                .andExpect(status().isOk());
    }

    // ===========================================
    // Tests des endpoints protégés ADMIN
    // ===========================================

    @Test
    @WithAnonymousUser
    void testAdminEndpoint_WithoutAuth_RedirectsToLogin() throws Exception {
        mockMvc.perform(get("/admin/dashboard"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testAdminEndpoint_WithUserRole_ReturnsForbidden() throws Exception {
        mockMvc.perform(get("/admin/dashboard"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testAdminEndpoint_WithAdminRole_ReturnsOk() throws Exception {
        mockMvc.perform(get("/admin/dashboard"))
                .andExpect(status().isOk());
    }

    // ===========================================
    // Tests des endpoints protégés USER
    // ===========================================

    @Test
    @WithAnonymousUser
    void testUserEndpoint_WithoutAuth_RedirectsToLogin() throws Exception {
        mockMvc.perform(get("/user/profile"))
                .andExpect(status().is3xxRedirection());
    }

    // Note: Les tests avec @WithMockUser pour /user/profile nécessiteraient
    // un utilisateur réel en base de données. Ces tests sont omis car ils
    // testent la logique du contrôleur, pas la configuration de sécurité.

    // ===========================================
    // Tests CORS
    // ===========================================

    @Test
    void testCors_AllowedOrigin_ReturnsOk() throws Exception {
        mockMvc.perform(get("/api/public/tracks")
                        .header("Origin", "http://localhost:5173"))
                .andExpect(status().isOk())
                .andExpect(header().exists("Access-Control-Allow-Origin"));
    }

    // ===========================================
    // Tests de sécurité généraux
    // ===========================================

    @Test
    void testSecurityHeaders_ArePresent() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(header().exists("X-Content-Type-Options"))
                .andExpect(header().exists("X-Frame-Options"));
    }
}
