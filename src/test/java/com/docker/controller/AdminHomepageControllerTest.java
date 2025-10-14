
package com.docker.controller;

import com.docker.entity.HomepageSettings;
import com.docker.service.HomepageSettingsService;
import com.docker.service.PhotoService;
import com.docker.service.TrackService;
import com.docker.service.VideoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdminHomepageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean private HomepageSettingsService homepageSettingsService;
    @MockBean private VideoService videoService;
    @MockBean private TrackService trackService;
    @MockBean private PhotoService photoService;

    private HomepageSettings settings;

    @BeforeEach
    void setUp() {
        settings = new HomepageSettings();
        settings.setId(1L);
        settings.setHeroTitle("Old Title");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testShowHomepageSettings_AsAdmin_ReturnsView() throws Exception {
        when(homepageSettingsService.getSettings()).thenReturn(settings);
        when(videoService.getAllVideos()).thenReturn(Collections.emptyList());
        when(trackService.getAllTracks()).thenReturn(Collections.emptyList());
        when(photoService.getAllPhotos()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/admin/homepage/settings"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/homepage-settings"))
                .andExpect(model().attributeExists("settings", "allVideos", "allTracks", "allPhotos"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testSaveHomepageSettings_AsAdmin_Success() throws Exception {
        when(homepageSettingsService.updateAllSettings(any(), any(), any(), any(), any())).thenReturn(settings);

        mockMvc.perform(post("/admin/homepage/settings/save").with(csrf())
                .param("heroTitle", "New Title")
                .param("heroSubtitle", "New Subtitle"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/homepage/settings"))
                .andExpect(flash().attributeExists("successMessage"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testToggleRegistration_AsAdmin_Success() throws Exception {
        mockMvc.perform(post("/admin/homepage/settings/toggle-registration").with(csrf())
                .param("enabled", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/homepage/settings"))
                .andExpect(flash().attributeExists("successMessage"));
    }
}
