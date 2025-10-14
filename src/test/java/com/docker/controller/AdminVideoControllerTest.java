package com.docker.controller;

import com.docker.entity.Video;
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
 * Tests unitaires pour AdminVideoController
 * Teste la gestion admin des vidéos (affichage, upload embed/fichier, suppression)
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdminVideoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VideoService videoService;

    @MockBean
    private CommentService commentService;

    @MockBean
    private SocialLinkService socialLinkService;

    @MockBean
    private LoginAttemptService loginAttemptService;

    private List<Video> testVideos;

    @BeforeEach
    void setUp() {
        Video video1 = new Video();
        video1.setId(1L);
        video1.setTitle("Video 1");
        video1.setFilename("video1.mp4");

        Video video2 = new Video();
        video2.setId(2L);
        video2.setTitle("Video 2");
        video2.setFilename("video2.mp4");

        testVideos = Arrays.asList(video1, video2);
    }

    // ========================================================================
    // TESTS AFFICHAGE PAGE GESTION VIDEOS
    // ========================================================================

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testShowVideoManagementPage_AsAdmin_ReturnsView() throws Exception {
        when(videoService.getAllVideos()).thenReturn(testVideos);

        mockMvc.perform(get("/admin/videos"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/video-management"))
                .andExpect(model().attributeExists("videos"))
                .andExpect(model().attribute("videos", testVideos));

        verify(videoService, times(1)).getAllVideos();
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testShowVideoManagementPage_AsUser_Returns403() throws Exception {
        mockMvc.perform(get("/admin/videos"))
                .andExpect(status().isForbidden());

        verify(videoService, never()).getAllVideos();
    }

    // ========================================================================
    // TESTS AJOUT VIDEO EMBED
    // ========================================================================

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testAddEmbedVideo_AsAdmin_Success() throws Exception {
        doNothing().when(videoService).saveEmbedVideo(anyString(), anyString());

        mockMvc.perform(post("/admin/videos/add-embed")
                .param("title", "Embed Video")
                .param("embedCode", "<iframe>...</iframe>")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/videos"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(videoService, times(1)).saveEmbedVideo("Embed Video", "<iframe>...</iframe>");
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testAddEmbedVideo_AsUser_Returns403() throws Exception {
        mockMvc.perform(post("/admin/videos/add-embed")
                .param("title", "Embed Video")
                .param("embedCode", "<iframe>...</iframe>")
                .with(csrf()))
                .andExpect(status().isForbidden());

        verify(videoService, never()).saveEmbedVideo(anyString(), anyString());
    }

    // ========================================================================
    // TESTS AJOUT VIDEO UPLOAD
    // ========================================================================

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testAddUploadVideo_AsAdmin_Success() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test-video.mp4",
                "video/mp4",
                "test video content".getBytes()
        );

        doNothing().when(videoService).saveUploadedVideo(anyString(), any());

        mockMvc.perform(multipart("/admin/videos/add-upload")
                .file(file)
                .param("title", "Uploaded Video")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/videos"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(videoService, times(1)).saveUploadedVideo(eq("Uploaded Video"), any());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testAddUploadVideo_AsUser_Returns403() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test-video.mp4",
                "video/mp4",
                "test video content".getBytes()
        );

        mockMvc.perform(multipart("/admin/videos/add-upload")
                .file(file)
                .param("title", "Uploaded Video")
                .with(csrf()))
                .andExpect(status().isForbidden());

        verify(videoService, never()).saveUploadedVideo(anyString(), any());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testAddUploadVideo_IOException_ReturnsError() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test-video.mp4",
                "video/mp4",
                "test video content".getBytes()
        );

        doThrow(new java.io.IOException("Disk full")).when(videoService).saveUploadedVideo(anyString(), any());

        mockMvc.perform(multipart("/admin/videos/add-upload")
                .file(file)
                .param("title", "Uploaded Video")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/videos"))
                .andExpect(flash().attributeExists("errorMessage"));

        verify(videoService, times(1)).saveUploadedVideo(eq("Uploaded Video"), any());
    }

    // ========================================================================
    // TESTS SUPPRESSION VIDEO
    // ========================================================================

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testDeleteVideo_AsAdmin_Success() throws Exception {
        doNothing().when(videoService).deleteVideo(1L);

        mockMvc.perform(post("/admin/videos/delete/1")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/videos"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(videoService, times(1)).deleteVideo(1L);
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testDeleteVideo_AsUser_Returns403() throws Exception {
        mockMvc.perform(post("/admin/videos/delete/1")
                .with(csrf()))
                .andExpect(status().isForbidden());

        verify(videoService, never()).deleteVideo(any());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testDeleteVideo_IOException_ReturnsError() throws Exception {
        doThrow(new java.io.IOException("Cannot delete file")).when(videoService).deleteVideo(1L);

        mockMvc.perform(post("/admin/videos/delete/1")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/videos"))
                .andExpect(flash().attributeExists("errorMessage"));

        verify(videoService, times(1)).deleteVideo(1L);
    }
}
