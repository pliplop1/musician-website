package com.docker.service;

import com.docker.entity.*;
import com.docker.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour HomepageSettingsService
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class HomepageSettingsServiceTest {

    @Autowired
    private HomepageSettingsService homepageSettingsService;

    @Autowired
    private HomepageSettingsRepository homepageSettingsRepository;

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private TrackRepository trackRepository;

    @Autowired
    private PhotoRepository photoRepository;

    private Video testVideo1;
    private Video testVideo2;
    private Video testVideo3;
    private Video testVideo4;

    private Track testTrack1;
    private Track testTrack2;
    private Track testTrack3;
    private Track testTrack4;

    private Photo testPhoto1;
    private Photo testPhoto2;
    private Photo testPhoto3;
    private Photo testPhoto4;

    @BeforeEach
    void setUp() {
        // Créer des vidéos de test
        testVideo1 = createAndSaveVideo("Test Video 1", VideoType.EMBED, "https://youtube.com/embed/test1");
        testVideo2 = createAndSaveVideo("Test Video 2", VideoType.EMBED, "https://youtube.com/embed/test2");
        testVideo3 = createAndSaveVideo("Test Video 3", VideoType.UPLOADED_FILE, null);
        testVideo4 = createAndSaveVideo("Test Video 4", VideoType.EMBED, "https://youtube.com/embed/test4");

        // Créer des tracks de test
        testTrack1 = createAndSaveTrack("Test Track 1", TrackType.EMBED, "https://spotify.com/embed/test1");
        testTrack2 = createAndSaveTrack("Test Track 2", TrackType.EMBED, "https://spotify.com/embed/test2");
        testTrack3 = createAndSaveTrack("Test Track 3", TrackType.UPLOADED_FILE, null);
        testTrack4 = createAndSaveTrack("Test Track 4", TrackType.EMBED, "https://spotify.com/embed/test4");

        // Créer des photos de test
        testPhoto1 = createAndSavePhoto("photo1.jpg", 1);
        testPhoto2 = createAndSavePhoto("photo2.jpg", 2);
        testPhoto3 = createAndSavePhoto("photo3.jpg", 3);
        testPhoto4 = createAndSavePhoto("photo4.jpg", 4);
    }

    // ========================================================================
    // TESTS GET SETTINGS
    // ========================================================================

    @Test
    void testGetSettings_FirstTime_CreatesDefaultSettings() {
        // When
        HomepageSettings settings = homepageSettingsService.getSettings();

        // Then
        assertNotNull(settings);
        assertEquals("DUO BLACK & WHITE", settings.getHeroTitle());
        assertEquals("La musique qui vous transporte • France & Belgique", settings.getHeroSubtitle());
        assertNotNull(settings.getWelcomeMessage());
        assertNotNull(settings.getRegistrationMessage());
    }

    @Test
    void testGetSettings_MultipleCalls_ReturnsSameInstance() {
        // When
        HomepageSettings settings1 = homepageSettingsService.getSettings();
        HomepageSettings settings2 = homepageSettingsService.getSettings();

        // Then
        assertEquals(settings1.getId(), settings2.getId());
    }

    // ========================================================================
    // TESTS UPDATE BASIC SETTINGS
    // ========================================================================

    @Test
    void testUpdateBasicSettings_ValidData_UpdatesSuccessfully() {
        // Given
        String newTitle = "Nouveau Titre";
        String newSubtitle = "Nouveau Sous-titre";

        // When
        HomepageSettings updated = homepageSettingsService.updateBasicSettings(newTitle, newSubtitle);

        // Then
        assertNotNull(updated);
        assertEquals(newTitle, updated.getHeroTitle());
        assertEquals(newSubtitle, updated.getHeroSubtitle());

        // Vérifier la persistance
        HomepageSettings retrieved = homepageSettingsService.getSettings();
        assertEquals(newTitle, retrieved.getHeroTitle());
        assertEquals(newSubtitle, retrieved.getHeroSubtitle());
    }

    // ========================================================================
    // TESTS UPDATE FEATURED VIDEOS
    // ========================================================================

    @Test
    void testUpdateFeaturedVideos_OneVideo_UpdatesSuccessfully() {
        // Given
        List<Long> videoIds = Arrays.asList(testVideo1.getId());

        // When
        HomepageSettings updated = homepageSettingsService.updateFeaturedVideos(videoIds);

        // Then
        assertNotNull(updated);
        assertEquals(1, updated.getFeaturedVideos().size());
        assertEquals(testVideo1.getId(), updated.getFeaturedVideos().get(0).getId());
    }

    @Test
    void testUpdateFeaturedVideos_ThreeVideos_UpdatesSuccessfully() {
        // Given
        List<Long> videoIds = Arrays.asList(testVideo1.getId(), testVideo2.getId(), testVideo3.getId());

        // When
        HomepageSettings updated = homepageSettingsService.updateFeaturedVideos(videoIds);

        // Then
        assertNotNull(updated);
        assertEquals(3, updated.getFeaturedVideos().size());
    }

    @Test
    void testUpdateFeaturedVideos_MoreThanThree_ThrowsException() {
        // Given
        List<Long> videoIds = Arrays.asList(
            testVideo1.getId(),
            testVideo2.getId(),
            testVideo3.getId(),
            testVideo4.getId()
        );

        // When/Then
        assertThrows(IllegalArgumentException.class,
            () -> homepageSettingsService.updateFeaturedVideos(videoIds),
            "Maximum 3 vidéos featured autorisées");
    }

    @Test
    void testUpdateFeaturedVideos_NonExistentVideo_ThrowsException() {
        // Given
        List<Long> videoIds = Arrays.asList(999L);

        // When/Then
        assertThrows(IllegalArgumentException.class,
            () -> homepageSettingsService.updateFeaturedVideos(videoIds));
    }

    @Test
    void testUpdateFeaturedVideos_EmptyList_ClearsVideos() {
        // Given - D'abord ajouter des vidéos
        homepageSettingsService.updateFeaturedVideos(Arrays.asList(testVideo1.getId()));

        // When - Vider la liste
        HomepageSettings updated = homepageSettingsService.updateFeaturedVideos(new ArrayList<>());

        // Then
        assertNotNull(updated);
        assertEquals(0, updated.getFeaturedVideos().size());
    }

    @Test
    void testUpdateFeaturedVideos_NullList_ClearsVideos() {
        // Given - D'abord ajouter des vidéos
        homepageSettingsService.updateFeaturedVideos(Arrays.asList(testVideo1.getId()));

        // When - Passer null
        HomepageSettings updated = homepageSettingsService.updateFeaturedVideos(null);

        // Then
        assertNotNull(updated);
        assertEquals(0, updated.getFeaturedVideos().size());
    }

    // ========================================================================
    // TESTS UPDATE FEATURED TRACKS
    // ========================================================================

    @Test
    void testUpdateFeaturedTracks_OneTrack_UpdatesSuccessfully() {
        // Given
        List<Long> trackIds = Arrays.asList(testTrack1.getId());

        // When
        HomepageSettings updated = homepageSettingsService.updateFeaturedTracks(trackIds);

        // Then
        assertNotNull(updated);
        assertEquals(1, updated.getFeaturedTracks().size());
        assertEquals(testTrack1.getId(), updated.getFeaturedTracks().get(0).getId());
    }

    @Test
    void testUpdateFeaturedTracks_ThreeTracks_UpdatesSuccessfully() {
        // Given
        List<Long> trackIds = Arrays.asList(testTrack1.getId(), testTrack2.getId(), testTrack3.getId());

        // When
        HomepageSettings updated = homepageSettingsService.updateFeaturedTracks(trackIds);

        // Then
        assertNotNull(updated);
        assertEquals(3, updated.getFeaturedTracks().size());
    }

    @Test
    void testUpdateFeaturedTracks_MoreThanThree_ThrowsException() {
        // Given
        List<Long> trackIds = Arrays.asList(
            testTrack1.getId(),
            testTrack2.getId(),
            testTrack3.getId(),
            testTrack4.getId()
        );

        // When/Then
        assertThrows(IllegalArgumentException.class,
            () -> homepageSettingsService.updateFeaturedTracks(trackIds),
            "Maximum 3 tracks featured autorisés");
    }

    @Test
    void testUpdateFeaturedTracks_NonExistentTrack_ThrowsException() {
        // Given
        List<Long> trackIds = Arrays.asList(999L);

        // When/Then
        assertThrows(IllegalArgumentException.class,
            () -> homepageSettingsService.updateFeaturedTracks(trackIds));
    }

    // ========================================================================
    // TESTS UPDATE ALL SETTINGS
    // ========================================================================

    @Test
    void testUpdateAllSettings_ValidData_UpdatesSuccessfully() {
        // Given
        String newTitle = "Nouveau Titre";
        String newSubtitle = "Nouveau Sous-titre";
        List<Long> videoIds = Arrays.asList(testVideo1.getId(), testVideo2.getId());
        List<Long> trackIds = Arrays.asList(testTrack1.getId());
        List<Long> photoIds = Arrays.asList(testPhoto1.getId(), testPhoto2.getId());

        // When
        HomepageSettings updated = homepageSettingsService.updateAllSettings(
            newTitle, newSubtitle, videoIds, trackIds, photoIds
        );

        // Then
        assertNotNull(updated);
        assertEquals(newTitle, updated.getHeroTitle());
        assertEquals(newSubtitle, updated.getHeroSubtitle());
        assertEquals(2, updated.getFeaturedVideos().size());
        assertEquals(1, updated.getFeaturedTracks().size());
        assertEquals(2, updated.getFeaturedPhotos().size());
    }

    @Test
    void testUpdateAllSettings_MoreThanThreeVideos_ThrowsException() {
        // Given
        List<Long> videoIds = Arrays.asList(
            testVideo1.getId(), testVideo2.getId(), testVideo3.getId(), testVideo4.getId()
        );

        // When/Then
        assertThrows(IllegalArgumentException.class,
            () -> homepageSettingsService.updateAllSettings(
                "Title", "Subtitle", videoIds, new ArrayList<>(), new ArrayList<>()
            ));
    }

    @Test
    void testUpdateAllSettings_MoreThanThreeTracks_ThrowsException() {
        // Given
        List<Long> trackIds = Arrays.asList(
            testTrack1.getId(), testTrack2.getId(), testTrack3.getId(), testTrack4.getId()
        );

        // When/Then
        assertThrows(IllegalArgumentException.class,
            () -> homepageSettingsService.updateAllSettings(
                "Title", "Subtitle", new ArrayList<>(), trackIds, new ArrayList<>()
            ));
    }

    @Test
    void testUpdateAllSettings_MoreThanThreePhotos_ThrowsException() {
        // Given
        List<Long> photoIds = Arrays.asList(
            testPhoto1.getId(), testPhoto2.getId(), testPhoto3.getId(), testPhoto4.getId()
        );

        // When/Then
        assertThrows(IllegalArgumentException.class,
            () -> homepageSettingsService.updateAllSettings(
                "Title", "Subtitle", new ArrayList<>(), new ArrayList<>(), photoIds
            ));
    }

    // ========================================================================
    // TESTS REGISTRATION ENABLED
    // ========================================================================

    @Test
    void testSetRegistrationEnabled_True_UpdatesSuccessfully() {
        // When
        HomepageSettings updated = homepageSettingsService.setRegistrationEnabled(true);

        // Then
        assertTrue(updated.getRegistrationEnabled());
    }

    @Test
    void testSetRegistrationEnabled_False_UpdatesSuccessfully() {
        // When
        HomepageSettings updated = homepageSettingsService.setRegistrationEnabled(false);

        // Then
        assertFalse(updated.getRegistrationEnabled());
    }

    // ========================================================================
    // TESTS WELCOME MESSAGE
    // ========================================================================

    @Test
    void testUpdateWelcomeMessage_ValidMessage_UpdatesSuccessfully() {
        // Given
        String newMessage = "Bienvenue sur notre nouveau site !";

        // When
        HomepageSettings updated = homepageSettingsService.updateWelcomeMessage(newMessage);

        // Then
        assertEquals(newMessage, updated.getWelcomeMessage());
    }

    // ========================================================================
    // TESTS HERO BACKGROUND VIDEO
    // ========================================================================

    @Test
    void testUpdateHeroBackgroundVideo_ValidUrl_UpdatesSuccessfully() {
        // Given
        String videoUrl = "/videos/new-background.mp4";

        // When
        HomepageSettings updated = homepageSettingsService.updateHeroBackgroundVideo(videoUrl);

        // Then
        assertEquals(videoUrl, updated.getHeroBackgroundVideoUrl());
    }

    // ========================================================================
    // HELPER METHODS
    // ========================================================================

    private Video createAndSaveVideo(String title, VideoType type, String embedCode) {
        Video video = new Video();
        video.setTitle(title);
        video.setVideoType(type);
        video.setEmbedCode(embedCode);
        if (type == VideoType.UPLOADED_FILE) {
            video.setFilename("test-video.mp4");
        }
        return videoRepository.save(video);
    }

    private Track createAndSaveTrack(String title, TrackType type, String embedCode) {
        Track track = new Track();
        track.setTitle(title);
        track.setTrackType(type);
        track.setEmbedCode(embedCode);
        if (type == TrackType.UPLOADED_FILE) {
            track.setFilename("test-track.mp3");
        }
        return trackRepository.save(track);
    }

    private Photo createAndSavePhoto(String filename, int displayOrder) {
        Photo photo = new Photo();
        photo.setFilename(filename);
        photo.setDisplayOrder(displayOrder);
        return photoRepository.save(photo);
    }
}
