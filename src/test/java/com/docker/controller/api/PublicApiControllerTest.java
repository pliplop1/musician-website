package com.docker.controller.api;

import com.docker.entity.*;
import com.docker.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

/**
 * Tests unitaires pour PublicApiController
 * Teste tous les endpoints publics accessibles sans authentification
 */
@WebMvcTest(PublicApiController.class)
@ActiveProfiles("test")
class PublicApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BiographyService biographyService;

    @MockBean
    private ConcertService concertService;

    @MockBean
    private PhotoService photoService;

    @MockBean
    private TrackService trackService;

    @MockBean
    private VideoService videoService;

    @MockBean
    private HomepageSettingsService homepageSettingsService;

    @MockBean
    private UserService userService;

    private Biography testBiography;
    private List<Concert> testConcerts;
    private List<Photo> testPhotos;
    private List<Video> testVideos;
    private List<Track> testTracks;
    private HomepageSettings testSettings;

    @BeforeEach
    void setUp() {
        // Préparer les données de test
        setupTestBiography();
        setupTestConcerts();
        setupTestPhotos();
        setupTestVideos();
        setupTestTracks();
        setupTestHomepageSettings();
    }

    private void setupTestBiography() {
        testBiography = new Biography();
        testBiography.setId(1L);
        testBiography.setContent("Duo Black & White est un duo de musiciens passionnés...");
    }

    private void setupTestConcerts() {
        testConcerts = new ArrayList<>();

        Concert futureConcert = new Concert();
        futureConcert.setId(1L);
        futureConcert.setLocation("Paris");
        futureConcert.setDate(LocalDate.now().plusDays(30));
        futureConcert.setDescription("Concert à Paris");
        testConcerts.add(futureConcert);

        Concert pastConcert = new Concert();
        pastConcert.setId(2L);
        pastConcert.setLocation("Lyon");
        pastConcert.setDate(LocalDate.now().minusDays(30));
        pastConcert.setDescription("Concert à Lyon");
        testConcerts.add(pastConcert);
    }

    private void setupTestPhotos() {
        testPhotos = new ArrayList<>();

        Photo photo1 = new Photo();
        photo1.setId(1L);
        photo1.setFilename("photo1.jpg");
        photo1.setDisplayOrder(1);
        testPhotos.add(photo1);

        Photo photo2 = new Photo();
        photo2.setId(2L);
        photo2.setFilename("photo2.jpg");
        photo2.setDisplayOrder(2);
        testPhotos.add(photo2);
    }

    private void setupTestVideos() {
        testVideos = new ArrayList<>();

        Video video1 = new Video();
        video1.setId(1L);
        video1.setTitle("Video 1");
        video1.setVideoType(VideoType.EMBED);
        video1.setEmbedCode("https://www.youtube.com/embed/test123");
        testVideos.add(video1);

        Video video2 = new Video();
        video2.setId(2L);
        video2.setTitle("Video 2");
        video2.setVideoType(VideoType.UPLOADED_FILE);
        video2.setFilename("video2.mp4");
        testVideos.add(video2);
    }

    private void setupTestTracks() {
        testTracks = new ArrayList<>();

        Track track1 = new Track();
        track1.setId(1L);
        track1.setTitle("Track 1");
        track1.setTrackType(TrackType.EMBED);
        track1.setEmbedCode("https://open.spotify.com/embed/track/test123");
        testTracks.add(track1);

        Track track2 = new Track();
        track2.setId(2L);
        track2.setTitle("Track 2");
        track2.setTrackType(TrackType.UPLOADED_FILE);
        track2.setFilename("track2.mp3");
        testTracks.add(track2);
    }

    private void setupTestHomepageSettings() {
        testSettings = new HomepageSettings(
            "DUO BLACK & WHITE",
            "La musique qui vous transporte"
        );
        testSettings.setWelcomeMessage("Bienvenue !");
        testSettings.setRegistrationEnabled(true);
        testSettings.setRegistrationMessage("Rejoignez-nous !");

        // Featured videos (max 3)
        testSettings.setFeaturedVideos(testVideos.subList(0, Math.min(2, testVideos.size())));

        // Featured tracks (max 3)
        testSettings.setFeaturedTracks(testTracks.subList(0, Math.min(2, testTracks.size())));

        // Featured photos (max 3)
        testSettings.setFeaturedPhotos(testPhotos.subList(0, Math.min(2, testPhotos.size())));
    }

    // ========================================================================
    // TESTS DES ENDPOINTS PRINCIPAUX
    // ========================================================================

    @Test
    void testGetHeroData_ReturnsValidJson() throws Exception {
        mockMvc.perform(get("/api/public/hero")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value("DUO BLACK & WHITE"))
                .andExpect(jsonPath("$.subtitle").exists())
                .andExpect(jsonPath("$.backgroundVideoUrl").exists());
    }

    @Test
    void testGetBiography_ValidBiography_ReturnsOk() throws Exception {
        when(biographyService.getBiography()).thenReturn(testBiography);

        mockMvc.perform(get("/api/public/biography")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").value(testBiography.getContent()))
                .andExpect(jsonPath("$.timeline").isArray());

        verify(biographyService, times(1)).getBiography();
    }

    @Test
    void testGetBiography_EmptyContent_Returns404() throws Exception {
        Biography emptyBio = new Biography();
        emptyBio.setContent("");
        when(biographyService.getBiography()).thenReturn(emptyBio);

        mockMvc.perform(get("/api/public/biography")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(biographyService, times(1)).getBiography();
    }

    @Test
    void testGetBiography_NullBiography_Returns404() throws Exception {
        when(biographyService.getBiography()).thenReturn(null);

        mockMvc.perform(get("/api/public/biography")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetUpcomingConcerts_ReturnsFutureConcerts() throws Exception {
        when(concertService.findAllConcerts()).thenReturn(testConcerts);

        mockMvc.perform(get("/api/public/concerts/upcoming")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].location").value("Paris"))
                .andExpect(jsonPath("$[0].isPast").value(false));

        verify(concertService, times(1)).findAllConcerts();
    }

    @Test
    void testGetPastConcerts_ReturnsPastConcerts() throws Exception {
        when(concertService.findAllConcerts()).thenReturn(testConcerts);

        mockMvc.perform(get("/api/public/concerts/past")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].location").value("Lyon"))
                .andExpect(jsonPath("$[0].isPast").value(true));

        verify(concertService, times(1)).findAllConcerts();
    }

    @Test
    void testGetGallery_ReturnsAllPhotos() throws Exception {
        when(photoService.getAllPhotos()).thenReturn(testPhotos);

        mockMvc.perform(get("/api/public/gallery")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].url").value("/uploaded-photos/photo1.jpg"));

        verify(photoService, times(1)).getAllPhotos();
    }

    @Test
    void testGetStats_ReturnsValidStats() throws Exception {
        when(concertService.findAllConcerts()).thenReturn(testConcerts);
        when(photoService.getAllPhotos()).thenReturn(testPhotos);
        when(trackService.getAllTracks()).thenReturn(testTracks);

        mockMvc.perform(get("/api/public/stats")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.totalConcerts").value(2))
                .andExpect(jsonPath("$.totalPhotos").value(2))
                .andExpect(jsonPath("$.totalTracks").value(2))
                .andExpect(jsonPath("$.upcomingConcerts").value(1));

        verify(concertService, times(1)).findAllConcerts();
        verify(photoService, times(1)).getAllPhotos();
        verify(trackService, times(1)).getAllTracks();
    }

    // ========================================================================
    // TESTS DES ENDPOINTS HOMEPAGE SETTINGS
    // ========================================================================

    @Test
    void testGetHomepageSettings_ReturnsSettings() throws Exception {
        when(homepageSettingsService.getSettings()).thenReturn(testSettings);

        mockMvc.perform(get("/api/public/homepage-settings")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.heroTitle").value("DUO BLACK & WHITE"))
                .andExpect(jsonPath("$.heroSubtitle").value("La musique qui vous transporte"))
                .andExpect(jsonPath("$.registrationEnabled").value(true))
                .andExpect(jsonPath("$.welcomeMessage").value("Bienvenue !"));

        verify(homepageSettingsService, times(1)).getSettings();
    }

    @Test
    void testGetFeaturedVideos_ReturnsMaxThreeVideos() throws Exception {
        when(homepageSettingsService.getSettings()).thenReturn(testSettings);

        mockMvc.perform(get("/api/public/featured/videos")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(lessThanOrEqualTo(3)))
                .andExpect(jsonPath("$[0].title").value("Video 1"));

        verify(homepageSettingsService, times(1)).getSettings();
    }

    @Test
    void testGetAllVideos_ReturnsAllVideos() throws Exception {
        when(videoService.getAllVideos()).thenReturn(testVideos);

        mockMvc.perform(get("/api/public/videos")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].videoType").value("EMBED"))
                .andExpect(jsonPath("$[1].videoType").value("UPLOADED_FILE"));

        verify(videoService, times(1)).getAllVideos();
    }

    @Test
    void testGetFeaturedTracks_ReturnsMaxThreeTracks() throws Exception {
        when(homepageSettingsService.getSettings()).thenReturn(testSettings);

        mockMvc.perform(get("/api/public/featured/tracks")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(lessThanOrEqualTo(3)))
                .andExpect(jsonPath("$[0].title").value("Track 1"));

        verify(homepageSettingsService, times(1)).getSettings();
    }

    @Test
    void testGetAllTracks_ReturnsAllTracks() throws Exception {
        when(trackService.getAllTracks()).thenReturn(testTracks);

        mockMvc.perform(get("/api/public/tracks")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));

        verify(trackService, times(1)).getAllTracks();
    }

    @Test
    void testGetFeaturedPhotos_ReturnsMaxThreePhotos() throws Exception {
        when(homepageSettingsService.getSettings()).thenReturn(testSettings);

        mockMvc.perform(get("/api/public/featured/photos")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(lessThanOrEqualTo(3)))
                .andExpect(jsonPath("$[0].url").value("/uploaded-photos/photo1.jpg"));

        verify(homepageSettingsService, times(1)).getSettings();
    }

    @Test
    void testGetAllPhotos_ReturnsAllPhotos() throws Exception {
        when(photoService.getAllPhotos()).thenReturn(testPhotos);

        mockMvc.perform(get("/api/public/photos")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));

        verify(photoService, times(1)).getAllPhotos();
    }

    @Test
    void testGetDiscography_ReturnsAlbumWithTracks() throws Exception {
        when(trackService.getAllTracks()).thenReturn(testTracks);

        mockMvc.perform(get("/api/public/discography")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].title").exists())
                .andExpect(jsonPath("$[0].tracks").isArray());

        verify(trackService, times(1)).getAllTracks();
    }

    // ========================================================================
    // TESTS DE L'ENDPOINT AUTH STATUS
    // ========================================================================

    @Test
    void testGetAuthStatus_NoAuthentication_ReturnsUnauthenticated() throws Exception {
        mockMvc.perform(get("/api/public/auth/status")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.authenticated").value(false))
                .andExpect(jsonPath("$.username").isEmpty())
                .andExpect(jsonPath("$.isAdmin").value(false))
                .andExpect(jsonPath("$.roles").isArray())
                .andExpect(jsonPath("$.roles").isEmpty());
    }
}
