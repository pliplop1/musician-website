package com.docker.controller;

import com.docker.entity.Biography;
import com.docker.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests unitaires pour AdminBiographyController
 * Teste la gestion admin de la biographie (affichage, mise à jour)
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdminBiographyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BiographyService biographyService;

    @MockBean
    private CommentService commentService;

    @MockBean
    private SocialLinkService socialLinkService;

    @MockBean
    private LoginAttemptService loginAttemptService;

    private Biography testBiography;

    @BeforeEach
    void setUp() {
        testBiography = new Biography();
        testBiography.setId(1L);
        testBiography.setContent("Ceci est ma biographie de test");
    }

    // ========================================================================
    // TESTS AFFICHAGE PAGE GESTION BIOGRAPHIE
    // ========================================================================

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testShowBiographyManagementPage_AsAdmin_ReturnsView() throws Exception {
        when(biographyService.getBiography()).thenReturn(testBiography);

        mockMvc.perform(get("/admin/biographie"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/biography-management"))
                .andExpect(model().attributeExists("biography"));

        verify(biographyService, times(1)).getBiography();
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testShowBiographyManagementPage_AsUser_Returns403() throws Exception {
        mockMvc.perform(get("/admin/biographie"))
                .andExpect(status().isForbidden());

        verify(biographyService, never()).getBiography();
    }

    // ========================================================================
    // TESTS MISE A JOUR BIOGRAPHIE
    // ========================================================================

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testUpdateBiography_AsAdmin_Success() throws Exception {
        doNothing().when(biographyService).saveBiography(anyString());

        mockMvc.perform(post("/admin/biography/update")
                .param("content", "Nouvelle biographie")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/biographie"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(biographyService, times(1)).saveBiography("Nouvelle biographie");
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testUpdateBiography_AsUser_Returns403() throws Exception {
        mockMvc.perform(post("/admin/biography/update")
                .param("content", "Nouvelle biographie")
                .with(csrf()))
                .andExpect(status().isForbidden());

        verify(biographyService, never()).saveBiography(anyString());
    }
}
