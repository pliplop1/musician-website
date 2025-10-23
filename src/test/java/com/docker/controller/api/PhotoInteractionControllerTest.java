package com.docker.controller.api;

import com.docker.entity.Photo;
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
 * Tests unitaires pour PhotoInteractionController
 * Teste les likes, unlikes, vues et statistiques des photos
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PhotoInteractionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PhotoService photoService;

    @MockBean
    private UserService userService;

    @MockBean
    private CommentService commentService;

    @MockBean
    private SocialLinkService socialLinkService;

    @MockBean
    private LoginAttemptService loginAttemptService;

    private Photo testPhoto;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");

        testPhoto = new Photo();
        testPhoto.setId(1L);
        testPhoto.setTitle("Test Photo");
        testPhoto.setFilename("test-photo.jpg");
        testPhoto.setPhotographer("John Doe");
        testPhoto.setLocation("Paris, France");
        testPhoto.setCategory("Portrait");
        testPhoto.setWidth(1920);
        testPhoto.setHeight(1080);
        testPhoto.setTakenAt(LocalDateTime.now());
        testPhoto.setLikedByUsers(new HashSet<>());
    }

    // ========================================================================
    // TESTS LIKE PHOTO
    // ========================================================================

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testLikePhoto_Authenticated_ReturnsSuccess() throws Exception {
        when(userService.findByUsername("testuser")).thenReturn(testUser);
        when(photoService.findById(1L)).thenReturn(testPhoto);
        when(photoService.save(any(Photo.class))).thenReturn(testPhoto);

        mockMvc.perform(post("/api/photos/1/like")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.liked").value(true))
                .andExpect(jsonPath("$.likeCount").exists())
                .andExpect(jsonPath("$.message").value("Photo likée avec succès"));

        verify(photoService, times(1)).findById(1L);
        verify(photoService, times(1)).save(any(Photo.class));
    }

    @Test
    @WithAnonymousUser
    void testLikePhoto_NotAuthenticated_Returns401() throws Exception {
        mockMvc.perform(post("/api/photos/1/like")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Authentification requise"));

        verify(photoService, never()).findById(any());
        verify(photoService, never()).save(any());
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testLikePhoto_PhotoNotFound_Returns404() throws Exception {
        when(userService.findByUsername("testuser")).thenReturn(testUser);
        when(photoService.findById(999L)).thenReturn(null);

        mockMvc.perform(post("/api/photos/999/like")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(photoService, times(1)).findById(999L);
        verify(photoService, never()).save(any());
    }

    // ========================================================================
    // TESTS UNLIKE PHOTO
    // ========================================================================

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testUnlikePhoto_Authenticated_ReturnsSuccess() throws Exception {
        testPhoto.addLike(testUser);
        when(userService.findByUsername("testuser")).thenReturn(testUser);
        when(photoService.findById(1L)).thenReturn(testPhoto);
        when(photoService.save(any(Photo.class))).thenReturn(testPhoto);

        mockMvc.perform(delete("/api/photos/1/like")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.liked").value(false))
                .andExpect(jsonPath("$.message").value("Like retiré avec succès"));

        verify(photoService, times(1)).findById(1L);
        verify(photoService, times(1)).save(any(Photo.class));
    }

    @Test
    @WithAnonymousUser
    void testUnlikePhoto_NotAuthenticated_Returns401() throws Exception {
        mockMvc.perform(delete("/api/photos/1/like")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Authentification requise"));

        verify(photoService, never()).findById(any());
    }

    // ========================================================================
    // TESTS LIKE STATUS
    // ========================================================================

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testGetLikeStatus_UserLiked_ReturnsTrue() throws Exception {
        testPhoto.addLike(testUser);
        when(userService.findByUsername("testuser")).thenReturn(testUser);
        when(photoService.findById(1L)).thenReturn(testPhoto);

        mockMvc.perform(get("/api/photos/1/like-status")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.liked").value(true))
                .andExpect(jsonPath("$.likeCount").value(1));

        verify(photoService, times(1)).findById(1L);
    }

    @Test
    @WithAnonymousUser
    void testGetLikeStatus_NotAuthenticated_ReturnsFalse() throws Exception {
        when(photoService.findById(1L)).thenReturn(testPhoto);

        mockMvc.perform(get("/api/photos/1/like-status")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.liked").value(false))
                .andExpect(jsonPath("$.likeCount").value(0));

        verify(photoService, times(1)).findById(1L);
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testGetLikeStatus_PhotoNotFound_Returns404() throws Exception {
        when(photoService.findById(999L)).thenReturn(null);

        mockMvc.perform(get("/api/photos/999/like-status")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(photoService, times(1)).findById(999L);
    }

    // ========================================================================
    // TESTS VIEW COUNT
    // ========================================================================

    @Test
    @WithAnonymousUser
    void testIncrementViewCount_Public_ReturnsSuccess() throws Exception {
        when(photoService.findById(1L)).thenReturn(testPhoto);
        when(photoService.save(any(Photo.class))).thenReturn(testPhoto);

        mockMvc.perform(post("/api/photos/1/view")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.viewCount").exists());

        verify(photoService, times(1)).findById(1L);
        verify(photoService, times(1)).save(any(Photo.class));
    }

    @Test
    @WithAnonymousUser
    void testIncrementViewCount_PhotoNotFound_Returns404() throws Exception {
        when(photoService.findById(999L)).thenReturn(null);

        mockMvc.perform(post("/api/photos/999/view")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(photoService, times(1)).findById(999L);
        verify(photoService, never()).save(any());
    }

    // ========================================================================
    // TESTS PHOTO STATS
    // ========================================================================

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testGetPhotoStats_Authenticated_ReturnsCompleteStats() throws Exception {
        testPhoto.setViewCount(100L);
        testPhoto.addLike(testUser);
        when(userService.findByUsername("testuser")).thenReturn(testUser);
        when(photoService.findById(1L)).thenReturn(testPhoto);

        mockMvc.perform(get("/api/photos/1/stats")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Photo"))
                .andExpect(jsonPath("$.filename").value("test-photo.jpg"))
                .andExpect(jsonPath("$.photographer").value("John Doe"))
                .andExpect(jsonPath("$.location").value("Paris, France"))
                .andExpect(jsonPath("$.viewCount").value(100))
                .andExpect(jsonPath("$.likeCount").value(1))
                .andExpect(jsonPath("$.isLiked").value(true))
                .andExpect(jsonPath("$.category").value("Portrait"))
                .andExpect(jsonPath("$.dimensions").value("1920x1080"));

        verify(photoService, times(1)).findById(1L);
    }

    @Test
    @WithAnonymousUser
    void testGetPhotoStats_NotAuthenticated_ReturnsStatsWithoutLikeStatus() throws Exception {
        testPhoto.setViewCount(50L);
        when(photoService.findById(1L)).thenReturn(testPhoto);

        mockMvc.perform(get("/api/photos/1/stats")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.viewCount").value(50))
                .andExpect(jsonPath("$.isLiked").value(false));

        verify(photoService, times(1)).findById(1L);
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testGetPhotoStats_PhotoNotFound_Returns404() throws Exception {
        when(photoService.findById(999L)).thenReturn(null);

        mockMvc.perform(get("/api/photos/999/stats")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(photoService, times(1)).findById(999L);
    }
}
