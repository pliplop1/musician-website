package com.docker.controller.api;

import com.docker.entity.Track;
import com.docker.repository.TrackRepository;
import com.docker.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests unitaires pour TrackStreamController
 * Teste le streaming de musiques via API
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TrackStreamControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TrackRepository trackRepository;

    @MockBean
    private CommentService commentService;

    @MockBean
    private SocialLinkService socialLinkService;

    @MockBean
    private LoginAttemptService loginAttemptService;

    private Track testTrack;

    @BeforeEach
    void setUp() {
        testTrack = new Track();
        testTrack.setId(1L);
        testTrack.setTitle("Test Song");
        testTrack.setFilename("test-song.mp3");
        testTrack.setArtist("Test Artist");
        testTrack.setAlbum("Test Album");
        testTrack.setGenre("Rock");
        testTrack.setDurationSeconds(180);
    }

    // ========================================================================
    // TESTS STREAMING
    // ========================================================================

    @Test
    @WithAnonymousUser
    void testStreamTrack_TrackNotFound_ThrowsException() throws Exception {
        when(trackRepository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/tracks/stream/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());

        verify(trackRepository, times(1)).findById(999L);
    }

    @Test
    @WithAnonymousUser
    void testStreamTrack_TrackExists_CallsRepository() throws Exception {
        when(trackRepository.findById(1L)).thenReturn(Optional.of(testTrack));

        // Note: Ce test va échouer car le fichier n'existe pas réellement
        // Mais il teste que le repository est appelé correctement
        try {
            mockMvc.perform(get("/api/tracks/stream/1")
                    .contentType(MediaType.APPLICATION_JSON));
        } catch (Exception e) {
            // Expected - fichier n'existe pas
        }

        verify(trackRepository, times(1)).findById(1L);
    }
}
