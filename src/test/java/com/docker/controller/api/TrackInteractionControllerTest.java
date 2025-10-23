package com.docker.controller.api;

import com.docker.entity.Track;
import com.docker.entity.User;
import com.docker.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.HashSet;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests unitaires pour TrackInteractionController
 * Teste les likes, unlikes, écoutes et statistiques des musiques
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TrackInteractionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TrackService trackService;

    @MockBean
    private UserService userService;

    @MockBean
    private CommentService commentService;

    @MockBean
    private SocialLinkService socialLinkService;

    @MockBean
    private LoginAttemptService loginAttemptService;

    private Track testTrack;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");

        testTrack = new Track();
        testTrack.setId(1L);
        testTrack.setTitle("Test Track");
        testTrack.setArtist("Test Artist");
        testTrack.setAlbum("Test Album");
        testTrack.setGenre("Rock");
        testTrack.setCategory("Original");
        testTrack.setDurationSeconds(180);
        testTrack.setPublishedAt(LocalDateTime.now());
        testTrack.setLikedByUsers(new HashSet<>());
    }

    // ========================================================================
    // TESTS LIKE TRACK
    // ========================================================================

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testLikeTrack_Authenticated_ReturnsSuccess() throws Exception {
        when(userService.findByUsername("testuser")).thenReturn(testUser);
        when(trackService.findById(1L)).thenReturn(testTrack);
        when(trackService.save(any(Track.class))).thenReturn(testTrack);

        mockMvc.perform(post("/api/tracks/1/like")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.liked").value(true))
                .andExpect(jsonPath("$.likeCount").exists())
                .andExpect(jsonPath("$.message").value("Chanson likée avec succès"));

        verify(trackService, times(1)).findById(1L);
        verify(trackService, times(1)).save(any(Track.class));
    }

    @Test
    @WithAnonymousUser
    void testLikeTrack_NotAuthenticated_Returns401() throws Exception {
        mockMvc.perform(post("/api/tracks/1/like")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Authentification requise"));

        verify(trackService, never()).findById(any());
        verify(trackService, never()).save(any());
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testLikeTrack_TrackNotFound_Returns404() throws Exception {
        when(userService.findByUsername("testuser")).thenReturn(testUser);
        when(trackService.findById(999L)).thenReturn(null);

        mockMvc.perform(post("/api/tracks/999/like")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(trackService, times(1)).findById(999L);
        verify(trackService, never()).save(any());
    }

    // ========================================================================
    // TESTS UNLIKE TRACK
    // ========================================================================

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testUnlikeTrack_Authenticated_ReturnsSuccess() throws Exception {
        testTrack.addLike(testUser);
        when(userService.findByUsername("testuser")).thenReturn(testUser);
        when(trackService.findById(1L)).thenReturn(testTrack);
        when(trackService.save(any(Track.class))).thenReturn(testTrack);

        mockMvc.perform(delete("/api/tracks/1/like")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.liked").value(false))
                .andExpect(jsonPath("$.message").value("Like retiré avec succès"));

        verify(trackService, times(1)).findById(1L);
        verify(trackService, times(1)).save(any(Track.class));
    }

    @Test
    @WithAnonymousUser
    void testUnlikeTrack_NotAuthenticated_Returns401() throws Exception {
        mockMvc.perform(delete("/api/tracks/1/like")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Authentification requise"));

        verify(trackService, never()).findById(any());
    }

    // ========================================================================
    // TESTS LIKE STATUS
    // ========================================================================

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testGetLikeStatus_UserLiked_ReturnsTrue() throws Exception {
        testTrack.addLike(testUser);
        when(userService.findByUsername("testuser")).thenReturn(testUser);
        when(trackService.findById(1L)).thenReturn(testTrack);

        mockMvc.perform(get("/api/tracks/1/like-status")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.liked").value(true))
                .andExpect(jsonPath("$.likeCount").value(1));

        verify(trackService, times(1)).findById(1L);
    }

    @Test
    @WithAnonymousUser
    void testGetLikeStatus_NotAuthenticated_ReturnsFalse() throws Exception {
        when(trackService.findById(1L)).thenReturn(testTrack);

        mockMvc.perform(get("/api/tracks/1/like-status")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.liked").value(false))
                .andExpect(jsonPath("$.likeCount").value(0));

        verify(trackService, times(1)).findById(1L);
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testGetLikeStatus_TrackNotFound_Returns404() throws Exception {
        when(trackService.findById(999L)).thenReturn(null);

        mockMvc.perform(get("/api/tracks/999/like-status")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(trackService, times(1)).findById(999L);
    }

    // ========================================================================
    // TESTS PLAY COUNT
    // ========================================================================

    @Test
    @WithAnonymousUser
    void testIncrementPlayCount_Public_ReturnsSuccess() throws Exception {
        when(trackService.findById(1L)).thenReturn(testTrack);
        when(trackService.save(any(Track.class))).thenReturn(testTrack);

        mockMvc.perform(post("/api/tracks/1/play")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.playCount").exists());

        verify(trackService, times(1)).findById(1L);
        verify(trackService, times(1)).save(any(Track.class));
    }

    @Test
    @WithAnonymousUser
    void testIncrementPlayCount_TrackNotFound_Returns404() throws Exception {
        when(trackService.findById(999L)).thenReturn(null);

        mockMvc.perform(post("/api/tracks/999/play")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(trackService, times(1)).findById(999L);
        verify(trackService, never()).save(any());
    }

    // ========================================================================
    // TESTS TRACK STATS
    // ========================================================================

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testGetTrackStats_Authenticated_ReturnsCompleteStats() throws Exception {
        testTrack.setPlayCount(100L);
        testTrack.addLike(testUser);
        when(userService.findByUsername("testuser")).thenReturn(testUser);
        when(trackService.findById(1L)).thenReturn(testTrack);

        mockMvc.perform(get("/api/tracks/1/stats")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Track"))
                .andExpect(jsonPath("$.artist").value("Test Artist"))
                .andExpect(jsonPath("$.album").value("Test Album"))
                .andExpect(jsonPath("$.playCount").value(100))
                .andExpect(jsonPath("$.likeCount").value(1))
                .andExpect(jsonPath("$.isLiked").value(true))
                .andExpect(jsonPath("$.genre").value("Rock"))
                .andExpect(jsonPath("$.category").value("Original"))
                .andExpect(jsonPath("$.publishedAt").exists())
                .andExpect(jsonPath("$.duration").exists());

        verify(trackService, times(1)).findById(1L);
    }

    @Test
    @WithAnonymousUser
    void testGetTrackStats_NotAuthenticated_ReturnsStatsWithoutLikeStatus() throws Exception {
        testTrack.setPlayCount(50L);
        when(trackService.findById(1L)).thenReturn(testTrack);

        mockMvc.perform(get("/api/tracks/1/stats")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.playCount").value(50))
                .andExpect(jsonPath("$.isLiked").value(false));

        verify(trackService, times(1)).findById(1L);
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testGetTrackStats_TrackNotFound_Returns404() throws Exception {
        when(trackService.findById(999L)).thenReturn(null);

        mockMvc.perform(get("/api/tracks/999/stats")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(trackService, times(1)).findById(999L);
    }
}
