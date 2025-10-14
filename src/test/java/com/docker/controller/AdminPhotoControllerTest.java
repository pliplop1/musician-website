package com.docker.controller;

import com.docker.entity.Photo;
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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests unitaires pour AdminPhotoController
 * Teste la gestion admin des photos (affichage, upload, suppression)
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdminPhotoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PhotoService photoService;

    @MockBean
    private CommentService commentService;

    @MockBean
    private SocialLinkService socialLinkService;

    @MockBean
    private LoginAttemptService loginAttemptService;

    private List<Photo> testPhotos;

    @BeforeEach
    void setUp() {
        Photo photo1 = new Photo();
        photo1.setId(1L);
        photo1.setTitle("Photo 1");
        photo1.setFilename("photo1.jpg");
        photo1.setPhotographer("John Doe");

        Photo photo2 = new Photo();
        photo2.setId(2L);
        photo2.setTitle("Photo 2");
        photo2.setFilename("photo2.jpg");
        photo2.setPhotographer("Jane Smith");

        testPhotos = Arrays.asList(photo1, photo2);
    }

    // ========================================================================
    // TESTS AFFICHAGE PAGE GESTION PHOTOS
    // ========================================================================

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testShowPhotoManagementPage_AsAdmin_ReturnsView() throws Exception {
        when(photoService.getAllPhotos()).thenReturn(testPhotos);

        mockMvc.perform(get("/admin/photos"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/photo-management"))
                .andExpect(model().attributeExists("photos"))
                .andExpect(model().attribute("photos", testPhotos));

        verify(photoService, times(1)).getAllPhotos();
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testShowPhotoManagementPage_AsUser_Returns403() throws Exception {
        mockMvc.perform(get("/admin/photos"))
                .andExpect(status().isForbidden());

        verify(photoService, never()).getAllPhotos();
    }

    // ========================================================================
    // TESTS UPLOAD PHOTO
    // ========================================================================

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testUploadPhoto_AsAdmin_Success() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test-photo.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        doNothing().when(photoService).savePhoto(any());

        mockMvc.perform(multipart("/admin/photos/upload")
                .file(file)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/photos"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(photoService, times(1)).savePhoto(any());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testUploadPhoto_AsUser_Returns403() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test-photo.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        mockMvc.perform(multipart("/admin/photos/upload")
                .file(file))
                .andExpect(status().isForbidden());

        verify(photoService, never()).savePhoto(any());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testUploadPhoto_IOException_ReturnsError() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test-photo.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        doThrow(new java.io.IOException("Disk full")).when(photoService).savePhoto(any());

        mockMvc.perform(multipart("/admin/photos/upload")
                .file(file)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/photos"))
                .andExpect(flash().attributeExists("errorMessage"));

        verify(photoService, times(1)).savePhoto(any());
    }

    // ========================================================================
    // TESTS SUPPRESSION PHOTO
    // ========================================================================

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testDeletePhoto_AsAdmin_Success() throws Exception {
        doNothing().when(photoService).deletePhoto(1L);

        mockMvc.perform(post("/admin/photos/delete/1")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/photos"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(photoService, times(1)).deletePhoto(1L);
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testDeletePhoto_AsUser_Returns403() throws Exception {
        mockMvc.perform(post("/admin/photos/delete/1"))
                .andExpect(status().isForbidden());

        verify(photoService, never()).deletePhoto(any());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testDeletePhoto_IOException_ReturnsError() throws Exception {
        doThrow(new java.io.IOException("Cannot delete file")).when(photoService).deletePhoto(1L);

        mockMvc.perform(post("/admin/photos/delete/1")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/photos"))
                .andExpect(flash().attributeExists("errorMessage"));

        verify(photoService, times(1)).deletePhoto(1L);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testDeletePhoto_IllegalArgumentException_ReturnsError() throws Exception {
        doThrow(new IllegalArgumentException("Photo not found")).when(photoService).deletePhoto(999L);

        mockMvc.perform(post("/admin/photos/delete/999")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/photos"))
                .andExpect(flash().attributeExists("errorMessage"));

        verify(photoService, times(1)).deletePhoto(999L);
    }
}
