package com.docker.controller;

import com.docker.entity.Track;
import com.docker.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests unitaires pour AdminTrackController
 * Teste la gestion admin des musiques (affichage, upload embed/fichier, suppression)
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdminTrackControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TrackService trackService;

    @MockBean
    private CommentService commentService;

    @MockBean
    private SocialLinkService socialLinkService;

    @MockBean
    private LoginAttemptService loginAttemptService;

    private List<Track> testTracks;

    @BeforeEach
    void setUp() {
        Track track1 = new Track();
        track1.setId(1L);
        track1.setTitle("Track 1");
        track1.setArtist("Artist 1");

        Track track2 = new Track();
        track2.setId(2L);
        track2.setTitle("Track 2");
        track2.setArtist("Artist 2");

        testTracks = Arrays.asList(track1, track2);
    }

    // ========================================================================
    // TESTS AFFICHAGE PAGE GESTION MUSIQUE
    // ========================================================================

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testShowMusicManagementPage_AsAdmin_ReturnsView() throws Exception {
        when(trackService.getAllTracks()).thenReturn(testTracks);

        mockMvc.perform(get("/admin/musique"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/music-management"))
                .andExpect(model().attributeExists("tracks"))
                .andExpect(model().attribute("tracks", testTracks));

        verify(trackService, times(1)).getAllTracks();
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testShowMusicManagementPage_AsUser_Returns403() throws Exception {
        mockMvc.perform(get("/admin/musique"))
                .andExpect(status().isForbidden());

        verify(trackService, never()).getAllTracks();
    }

    // ========================================================================
    // TESTS AJOUT TRACK EMBED
    // ========================================================================

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testAddEmbedTrack_AsAdmin_Success() throws Exception {
        doNothing().when(trackService).saveEmbedTrack(anyString(), anyString());

        mockMvc.perform(post("/admin/tracks/add-embed")
                .param("title", "Embed Track")
                .param("embedCode", "<iframe>...</iframe>")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/musique"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(trackService, times(1)).saveEmbedTrack("Embed Track", "<iframe>...</iframe>");
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testAddEmbedTrack_AsUser_Returns403() throws Exception {
        mockMvc.perform(post("/admin/tracks/add-embed")
                .param("title", "Embed Track")
                .param("embedCode", "<iframe>...</iframe>")
                .with(csrf()))
                .andExpect(status().isForbidden());

        verify(trackService, never()).saveEmbedTrack(anyString(), anyString());
    }

    // ========================================================================
    // TESTS AJOUT TRACK UPLOAD
    // ========================================================================

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testAddUploadTrack_AsAdmin_Success() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test-track.mp3",
                "audio/mpeg",
                "test track content".getBytes()
        );

        doNothing().when(trackService).saveUploadedTrack(anyString(), any());

        mockMvc.perform(multipart("/admin/tracks/add-upload")
                .file(file)
                .param("title", "Uploaded Track")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/musique"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(trackService, times(1)).saveUploadedTrack(eq("Uploaded Track"), any());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testAddUploadTrack_AsUser_Returns403() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test-track.mp3",
                "audio/mpeg",
                "test track content".getBytes()
        );

        mockMvc.perform(multipart("/admin/tracks/add-upload")
                .file(file)
                .param("title", "Uploaded Track")
                .with(csrf()))
                .andExpect(status().isForbidden());

        verify(trackService, never()).saveUploadedTrack(anyString(), any());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testAddUploadTrack_IOException_ReturnsError() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test-track.mp3",
                "audio/mpeg",
                "test track content".getBytes()
        );

        doThrow(new java.io.IOException("Disk full")).when(trackService).saveUploadedTrack(anyString(), any());

        mockMvc.perform(multipart("/admin/tracks/add-upload")
                .file(file)
                .param("title", "Uploaded Track")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/musique"))
                .andExpect(flash().attributeExists("errorMessage"));

        verify(trackService, times(1)).saveUploadedTrack(eq("Uploaded Track"), any());
    }

    // ========================================================================
    // TESTS SUPPRESSION TRACK
    // ========================================================================

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testDeleteTrack_AsAdmin_Success() throws Exception {
        doNothing().when(trackService).deleteTrack(1L);

        mockMvc.perform(post("/admin/tracks/delete/1")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/musique"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(trackService, times(1)).deleteTrack(1L);
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testDeleteTrack_AsUser_Returns403() throws Exception {
        mockMvc.perform(post("/admin/tracks/delete/1")
                .with(csrf()))
                .andExpect(status().isForbidden());

        verify(trackService, never()).deleteTrack(any());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testDeleteTrack_IOException_ReturnsError() throws Exception {
        doThrow(new java.io.IOException("Cannot delete file")).when(trackService).deleteTrack(1L);

        mockMvc.perform(post("/admin/tracks/delete/1")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/musique"))
                .andExpect(flash().attributeExists("errorMessage"));

        verify(trackService, times(1)).deleteTrack(1L);
    }
}
