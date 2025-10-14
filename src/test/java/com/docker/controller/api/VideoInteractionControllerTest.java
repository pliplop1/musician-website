package com.docker.controller.api;

import com.docker.entity.User;
import com.docker.entity.Video;
import com.docker.entity.VideoType;
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
 * Tests unitaires pour VideoInteractionController
 * Teste les likes, unlikes, vues et statistiques des vidéos
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class VideoInteractionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VideoService videoService;

    @MockBean
    private UserService userService;

    @MockBean
    private CommentService commentService;

    @MockBean
    private SocialLinkService socialLinkService;

    @MockBean
    private LoginAttemptService loginAttemptService;

    private Video testVideo;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");

        testVideo = new Video();
        testVideo.setId(1L);
        testVideo.setTitle("Test Video");
        testVideo.setVideoType(VideoType.EMBED);
        testVideo.setEmbedCode("https://www.youtube.com/embed/test123");
        testVideo.setPublishedAt(LocalDateTime.now());
        testVideo.setLikedByUsers(new HashSet<>());
    }

    // ========================================================================
    // TESTS LIKE VIDEO
    // ========================================================================

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testLikeVideo_Authenticated_ReturnsSuccess() throws Exception {
        when(userService.findByUsername("testuser")).thenReturn(testUser);
        when(videoService.findById(1L)).thenReturn(testVideo);
        when(videoService.save(any(Video.class))).thenReturn(testVideo);

        mockMvc.perform(post("/api/videos/1/like")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.liked").value(true))
                .andExpect(jsonPath("$.likeCount").exists())
                .andExpect(jsonPath("$.message").value("Vidéo likée avec succès"));

        verify(videoService, times(1)).findById(1L);
        verify(videoService, times(1)).save(any(Video.class));
    }

    @Test
    @WithAnonymousUser
    void testLikeVideo_NotAuthenticated_Returns401() throws Exception {
        mockMvc.perform(post("/api/videos/1/like")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Authentification requise"));

        verify(videoService, never()).findById(any());
        verify(videoService, never()).save(any());
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testLikeVideo_VideoNotFound_Returns404() throws Exception {
        when(userService.findByUsername("testuser")).thenReturn(testUser);
        when(videoService.findById(999L)).thenReturn(null);

        mockMvc.perform(post("/api/videos/999/like")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(videoService, times(1)).findById(999L);
        verify(videoService, never()).save(any());
    }

    // ========================================================================
    // TESTS UNLIKE VIDEO
    // ========================================================================

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testUnlikeVideo_Authenticated_ReturnsSuccess() throws Exception {
        testVideo.addLike(testUser);
        when(userService.findByUsername("testuser")).thenReturn(testUser);
        when(videoService.findById(1L)).thenReturn(testVideo);
        when(videoService.save(any(Video.class))).thenReturn(testVideo);

        mockMvc.perform(delete("/api/videos/1/like")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.liked").value(false))
                .andExpect(jsonPath("$.message").value("Like retiré avec succès"));

        verify(videoService, times(1)).findById(1L);
        verify(videoService, times(1)).save(any(Video.class));
    }

    @Test
    @WithAnonymousUser
    void testUnlikeVideo_NotAuthenticated_Returns401() throws Exception {
        mockMvc.perform(delete("/api/videos/1/like")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Authentification requise"));

        verify(videoService, never()).findById(any());
    }

    // ========================================================================
    // TESTS LIKE STATUS
    // ========================================================================

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testGetLikeStatus_UserLiked_ReturnsTrue() throws Exception {
        testVideo.addLike(testUser);
        when(userService.findByUsername("testuser")).thenReturn(testUser);
        when(videoService.findById(1L)).thenReturn(testVideo);

        mockMvc.perform(get("/api/videos/1/like-status")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.liked").value(true))
                .andExpect(jsonPath("$.likeCount").value(1));

        verify(videoService, times(1)).findById(1L);
    }

    @Test
    @WithAnonymousUser
    void testGetLikeStatus_NotAuthenticated_ReturnsFalse() throws Exception {
        when(videoService.findById(1L)).thenReturn(testVideo);

        mockMvc.perform(get("/api/videos/1/like-status")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.liked").value(false))
                .andExpect(jsonPath("$.likeCount").value(0));

        verify(videoService, times(1)).findById(1L);
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testGetLikeStatus_VideoNotFound_Returns404() throws Exception {
        when(videoService.findById(999L)).thenReturn(null);

        mockMvc.perform(get("/api/videos/999/like-status")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(videoService, times(1)).findById(999L);
    }

    // ========================================================================
    // TESTS VIEW COUNT
    // ========================================================================

    @Test
    @WithAnonymousUser
    void testIncrementViewCount_Public_ReturnsSuccess() throws Exception {
        when(videoService.findById(1L)).thenReturn(testVideo);
        when(videoService.save(any(Video.class))).thenReturn(testVideo);

        mockMvc.perform(post("/api/videos/1/view")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.viewCount").exists());

        verify(videoService, times(1)).findById(1L);
        verify(videoService, times(1)).save(any(Video.class));
    }

    @Test
    @WithAnonymousUser
    void testIncrementViewCount_VideoNotFound_Returns404() throws Exception {
        when(videoService.findById(999L)).thenReturn(null);

        mockMvc.perform(post("/api/videos/999/view")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(videoService, times(1)).findById(999L);
        verify(videoService, never()).save(any());
    }

    // ========================================================================
    // TESTS VIDEO STATS
    // ========================================================================

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testGetVideoStats_Authenticated_ReturnsCompleteStats() throws Exception {
        testVideo.setViewCount(100L);
        testVideo.addLike(testUser);
        when(userService.findByUsername("testuser")).thenReturn(testUser);
        when(videoService.findById(1L)).thenReturn(testVideo);

        mockMvc.perform(get("/api/videos/1/stats")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Video"))
                .andExpect(jsonPath("$.viewCount").value(100))
                .andExpect(jsonPath("$.likeCount").value(1))
                .andExpect(jsonPath("$.isLiked").value(true))
                .andExpect(jsonPath("$.publishedAt").exists());

        verify(videoService, times(1)).findById(1L);
    }

    @Test
    @WithAnonymousUser
    void testGetVideoStats_NotAuthenticated_ReturnsStatsWithoutLikeStatus() throws Exception {
        testVideo.setViewCount(50L);
        when(videoService.findById(1L)).thenReturn(testVideo);

        mockMvc.perform(get("/api/videos/1/stats")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.viewCount").value(50))
                .andExpect(jsonPath("$.isLiked").value(false));

        verify(videoService, times(1)).findById(1L);
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testGetVideoStats_VideoNotFound_Returns404() throws Exception {
        when(videoService.findById(999L)).thenReturn(null);

        mockMvc.perform(get("/api/videos/999/stats")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(videoService, times(1)).findById(999L);
    }
}
