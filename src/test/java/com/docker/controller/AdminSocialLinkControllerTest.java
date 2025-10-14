package com.docker.controller;

import com.docker.entity.SocialLink;
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

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests unitaires pour AdminSocialLinkController
 * Teste la gestion admin des réseaux sociaux (affichage, mise à jour)
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdminSocialLinkControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SocialLinkService socialLinkService;

    @MockBean
    private CommentService commentService;

    @MockBean
    private LoginAttemptService loginAttemptService;

    private List<SocialLink> testSocialLinks;

    @BeforeEach
    void setUp() {
        SocialLink link1 = new SocialLink();
        link1.setId(1L);
        link1.setName("Facebook");
        link1.setUrl("https://facebook.com/musician");
        link1.setEnabled(true);

        SocialLink link2 = new SocialLink();
        link2.setId(2L);
        link2.setName("Twitter");
        link2.setUrl("https://twitter.com/musician");
        link2.setEnabled(false);

        testSocialLinks = Arrays.asList(link1, link2);
    }

    // ========================================================================
    // TESTS AFFICHAGE PAGE GESTION RESEAUX SOCIAUX
    // ========================================================================

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testShowSocialLinksPage_AsAdmin_ReturnsView() throws Exception {
        when(socialLinkService.getAllSocialLinks()).thenReturn(testSocialLinks);

        mockMvc.perform(get("/admin/social-links"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/social-links"))
                .andExpect(model().attributeExists("socialLinks"))
                .andExpect(model().attribute("socialLinks", testSocialLinks));

        verify(socialLinkService, times(1)).getAllSocialLinks();
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testShowSocialLinksPage_AsUser_Returns403() throws Exception {
        mockMvc.perform(get("/admin/social-links"))
                .andExpect(status().isForbidden());

        verify(socialLinkService, never()).getAllSocialLinks();
    }

    // ========================================================================
    // TESTS MISE A JOUR LIEN SOCIAL
    // ========================================================================

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testUpdateSocialLink_AsAdmin_Success() throws Exception {
        doNothing().when(socialLinkService).updateSocialLink(anyLong(), anyString(), anyBoolean());

        mockMvc.perform(post("/admin/social-links/update/1")
                .param("url", "https://facebook.com/newurl")
                .param("enabled", "true")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/social-links"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(socialLinkService, times(1)).updateSocialLink(1L, "https://facebook.com/newurl", true);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testUpdateSocialLink_AsAdmin_WithEnabledFalse() throws Exception {
        doNothing().when(socialLinkService).updateSocialLink(anyLong(), anyString(), any());

        mockMvc.perform(post("/admin/social-links/update/2")
                .param("url", "https://twitter.com/newurl")
                .param("enabled", "false")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/social-links"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(socialLinkService, times(1)).updateSocialLink(2L, "https://twitter.com/newurl", false);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testUpdateSocialLink_AsAdmin_WithoutEnabledParam() throws Exception {
        doNothing().when(socialLinkService).updateSocialLink(anyLong(), anyString(), any());

        mockMvc.perform(post("/admin/social-links/update/1")
                .param("url", "https://facebook.com/newurl")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/social-links"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(socialLinkService, times(1)).updateSocialLink(eq(1L), eq("https://facebook.com/newurl"), isNull());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testUpdateSocialLink_AsUser_Returns403() throws Exception {
        mockMvc.perform(post("/admin/social-links/update/1")
                .param("url", "https://facebook.com/newurl")
                .param("enabled", "true")
                .with(csrf()))
                .andExpect(status().isForbidden());

        verify(socialLinkService, never()).updateSocialLink(anyLong(), anyString(), any());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testUpdateSocialLink_ServiceThrowsException_ReturnsError() throws Exception {
        doThrow(new RuntimeException("Service error")).when(socialLinkService)
                .updateSocialLink(anyLong(), anyString(), anyBoolean());

        mockMvc.perform(post("/admin/social-links/update/1")
                .param("url", "https://facebook.com/newurl")
                .param("enabled", "true")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/social-links"))
                .andExpect(flash().attributeExists("errorMessage"));

        verify(socialLinkService, times(1)).updateSocialLink(1L, "https://facebook.com/newurl", true);
    }
}
