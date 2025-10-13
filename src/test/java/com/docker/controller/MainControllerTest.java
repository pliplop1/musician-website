package com.docker.controller;

import com.docker.entity.*;
import com.docker.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests unitaires pour MainController
 * Teste particulièrement les pages refactorisées avec CSS externalisé :
 * - /galerie (gallery-page.css)
 * - /musique (music-page.css)
 * - /videos (video-page.css)
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MainControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ConcertService concertService;

    @MockBean
    private MessageService messageService;

    @MockBean
    private UserService userService;

    @MockBean
    private BiographyService biographyService;

    @MockBean
    private PhotoService photoService;

    @MockBean
    private TrackService trackService;

    @MockBean
    private VideoService videoService;

    @MockBean
    private CommentService commentService;

    @MockBean
    private SocialLinkService socialLinkService;

    @MockBean
    private LoginAttemptService loginAttemptService;

    private List<Photo> testPhotos;
    private List<Track> testTracks;
    private List<Video> testVideos;

    @BeforeEach
    void setUp() {
        setupTestPhotos();
        setupTestTracks();
        setupTestVideos();
    }

    private void setupTestPhotos() {
        testPhotos = new ArrayList<>();

        Photo photo1 = new Photo();
        photo1.setId(1L);
        photo1.setFilename("photo1.jpg");
        photo1.setTitle("Photo de concert 1");
        photo1.setCategory("Concert");
        photo1.setDisplayOrder(1);
        testPhotos.add(photo1);

        Photo photo2 = new Photo();
        photo2.setId(2L);
        photo2.setFilename("photo2.jpg");
        photo2.setTitle("Photo de concert 2");
        photo2.setCategory("Studio");
        photo2.setDisplayOrder(2);
        testPhotos.add(photo2);
    }

    private void setupTestTracks() {
        testTracks = new ArrayList<>();

        Track track1 = new Track();
        track1.setId(1L);
        track1.setTitle("Morceau 1");
        track1.setArtist("Duo Black & White");
        track1.setTrackType(TrackType.EMBED);
        track1.setEmbedCode("https://open.spotify.com/embed/track/test123");
        testTracks.add(track1);

        Track track2 = new Track();
        track2.setId(2L);
        track2.setTitle("Morceau 2");
        track2.setArtist("Duo Black & White");
        track2.setTrackType(TrackType.UPLOADED_FILE);
        track2.setFilename("track2.mp3");
        testTracks.add(track2);
    }

    private void setupTestVideos() {
        testVideos = new ArrayList<>();

        Video video1 = new Video();
        video1.setId(1L);
        video1.setTitle("Vidéo 1");
        video1.setVideoType(VideoType.EMBED);
        video1.setEmbedCode("https://www.youtube.com/embed/test123");
        testVideos.add(video1);

        Video video2 = new Video();
        video2.setId(2L);
        video2.setTitle("Vidéo 2");
        video2.setVideoType(VideoType.UPLOADED_FILE);
        video2.setFilename("video2.mp4");
        testVideos.add(video2);
    }

    // ========================================================================
    // TESTS DE LA PAGE GALERIE (avec gallery-page.css)
    // ========================================================================

    @Test
    @WithAnonymousUser
    void testShowGallery_ReturnsGalleryView() throws Exception {
        when(photoService.getAllPhotos()).thenReturn(testPhotos);

        mockMvc.perform(get("/galerie"))
                .andExpect(status().isOk())
                .andExpect(view().name("galerie"))
                .andExpect(model().attributeExists("photos"))
                .andExpect(model().attribute("photos", testPhotos));

        verify(photoService, times(1)).getAllPhotos();
    }

    @Test
    @WithAnonymousUser
    void testShowGallery_WithEmptyPhotoList() throws Exception {
        when(photoService.getAllPhotos()).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/galerie"))
                .andExpect(status().isOk())
                .andExpect(view().name("galerie"))
                .andExpect(model().attributeExists("photos"));

        verify(photoService, times(1)).getAllPhotos();
    }

    @Test
    @WithAnonymousUser
    void testShowGallery_MultiplePhotos() throws Exception {
        // Ajouter plus de photos pour tester la pagination
        for (int i = 3; i <= 20; i++) {
            Photo photo = new Photo();
            photo.setId((long) i);
            photo.setFilename("photo" + i + ".jpg");
            photo.setTitle("Photo " + i);
            photo.setDisplayOrder(i);
            testPhotos.add(photo);
        }

        when(photoService.getAllPhotos()).thenReturn(testPhotos);

        mockMvc.perform(get("/galerie"))
                .andExpect(status().isOk())
                .andExpect(view().name("galerie"))
                .andExpect(model().attribute("photos", testPhotos));

        verify(photoService, times(1)).getAllPhotos();
    }

    // ========================================================================
    // TESTS DE LA PAGE MUSIQUE (avec music-page.css)
    // ========================================================================

    @Test
    @WithAnonymousUser
    void testShowMusic_ReturnsMusiqueView() throws Exception {
        when(trackService.getAllTracks()).thenReturn(testTracks);

        mockMvc.perform(get("/musique"))
                .andExpect(status().isOk())
                .andExpect(view().name("musique"))
                .andExpect(model().attributeExists("tracks"))
                .andExpect(model().attribute("tracks", testTracks));

        verify(trackService, times(1)).getAllTracks();
    }

    @Test
    @WithAnonymousUser
    void testShowMusic_WithEmptyTrackList() throws Exception {
        when(trackService.getAllTracks()).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/musique"))
                .andExpect(status().isOk())
                .andExpect(view().name("musique"))
                .andExpect(model().attributeExists("tracks"));

        verify(trackService, times(1)).getAllTracks();
    }

    @Test
    @WithAnonymousUser
    void testShowMusic_WithMultipleTracks() throws Exception {
        // Ajouter plus de morceaux
        for (int i = 3; i <= 15; i++) {
            Track track = new Track();
            track.setId((long) i);
            track.setTitle("Morceau " + i);
            track.setArtist("Duo Black & White");
            track.setTrackType(TrackType.EMBED);
            testTracks.add(track);
        }

        when(trackService.getAllTracks()).thenReturn(testTracks);

        mockMvc.perform(get("/musique"))
                .andExpect(status().isOk())
                .andExpect(view().name("musique"))
                .andExpect(model().attribute("tracks", testTracks));

        verify(trackService, times(1)).getAllTracks();
    }

    // ========================================================================
    // TESTS DE LA PAGE VIDEOS (avec video-page.css)
    // ========================================================================

    @Test
    @WithAnonymousUser
    void testShowVideos_ReturnsVideosView() throws Exception {
        when(videoService.getAllVideos()).thenReturn(testVideos);

        mockMvc.perform(get("/videos"))
                .andExpect(status().isOk())
                .andExpect(view().name("videos"))
                .andExpect(model().attributeExists("videos"))
                .andExpect(model().attribute("videos", testVideos));

        verify(videoService, times(1)).getAllVideos();
    }

    @Test
    @WithAnonymousUser
    void testShowVideos_WithEmptyVideoList() throws Exception {
        when(videoService.getAllVideos()).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/videos"))
                .andExpect(status().isOk())
                .andExpect(view().name("videos"))
                .andExpect(model().attributeExists("videos"));

        verify(videoService, times(1)).getAllVideos();
    }

    @Test
    @WithAnonymousUser
    void testShowVideos_WithMultipleVideos() throws Exception {
        // Ajouter plus de vidéos
        for (int i = 3; i <= 12; i++) {
            Video video = new Video();
            video.setId((long) i);
            video.setTitle("Vidéo " + i);
            video.setVideoType(VideoType.EMBED);
            video.setEmbedCode("https://www.youtube.com/embed/test" + i);
            testVideos.add(video);
        }

        when(videoService.getAllVideos()).thenReturn(testVideos);

        mockMvc.perform(get("/videos"))
                .andExpect(status().isOk())
                .andExpect(view().name("videos"))
                .andExpect(model().attribute("videos", testVideos));

        verify(videoService, times(1)).getAllVideos();
    }

    @Test
    @WithAnonymousUser
    void testShowVideos_WithMixedVideoTypes() throws Exception {
        // Vérifier que différents types de vidéos sont supportés
        when(videoService.getAllVideos()).thenReturn(testVideos);

        mockMvc.perform(get("/videos"))
                .andExpect(status().isOk())
                .andExpect(view().name("videos"));

        verify(videoService, times(1)).getAllVideos();
    }

    // ========================================================================
    // TESTS DE BIOGRAPHIE (pour compléter la couverture)
    // ========================================================================

    @Test
    @WithAnonymousUser
    void testShowBiography_ReturnsBiographyView() throws Exception {
        Biography testBiography = new Biography();
        testBiography.setId(1L);
        testBiography.setContent("Duo Black & White est un duo de musiciens passionnés...");

        when(biographyService.getBiography()).thenReturn(testBiography);

        mockMvc.perform(get("/biographie"))
                .andExpect(status().isOk())
                .andExpect(view().name("biographie"))
                .andExpect(model().attributeExists("biography"))
                .andExpect(model().attribute("biography", testBiography));

        verify(biographyService, times(1)).getBiography();
    }

    // ========================================================================
    // TESTS AVEC AUTHENTIFICATION
    // ========================================================================

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testShowGallery_WithAuthenticatedUser() throws Exception {
        when(photoService.getAllPhotos()).thenReturn(testPhotos);

        mockMvc.perform(get("/galerie"))
                .andExpect(status().isOk())
                .andExpect(view().name("galerie"))
                .andExpect(model().attributeExists("photos"));

        verify(photoService, times(1)).getAllPhotos();
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testShowMusic_WithAdminUser() throws Exception {
        when(trackService.getAllTracks()).thenReturn(testTracks);

        mockMvc.perform(get("/musique"))
                .andExpect(status().isOk())
                .andExpect(view().name("musique"))
                .andExpect(model().attributeExists("tracks"));

        verify(trackService, times(1)).getAllTracks();
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testShowVideos_WithAuthenticatedUser() throws Exception {
        when(videoService.getAllVideos()).thenReturn(testVideos);

        mockMvc.perform(get("/videos"))
                .andExpect(status().isOk())
                .andExpect(view().name("videos"))
                .andExpect(model().attributeExists("videos"));

        verify(videoService, times(1)).getAllVideos();
    }
}
