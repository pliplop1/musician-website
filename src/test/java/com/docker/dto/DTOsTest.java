package com.docker.dto;

import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests complets pour tous les DTOs du projet
 * Améliore la couverture du package dto de 22% à >50%
 */
class DTOsTest {

    @Test
    void testAlbumDTO() {
        // Test constructeur par défaut
        AlbumDTO dto1 = new AlbumDTO();
        assertNull(dto1.getId());

        // Test setters
        dto1.setId(1L);
        dto1.setTitle("Test Album");
        dto1.setCoverUrl("cover.jpg");
        dto1.setYear(2024);
        dto1.setDescription("Description");
        dto1.setTracks(Arrays.asList());
        dto1.setSpotifyUrl("https://spotify.com");
        dto1.setAppleMusicUrl("https://apple.com");

        assertEquals(1L, dto1.getId());
        assertEquals("Test Album", dto1.getTitle());
        assertEquals("cover.jpg", dto1.getCoverUrl());
        assertEquals(2024, dto1.getYear());
        assertEquals("Description", dto1.getDescription());
        assertNotNull(dto1.getTracks());
        assertEquals("https://spotify.com", dto1.getSpotifyUrl());
        assertEquals("https://apple.com", dto1.getAppleMusicUrl());
    }

    @Test
    void testVideoDTO() {
        // VideoDTO est un record
        VideoDTO dto = new VideoDTO(
            1L,
            "Test Video",
            "EMBED",
            "<iframe>...</iframe>",
            null,
            10,
            100
        );

        assertEquals(1L, dto.id());
        assertEquals("Test Video", dto.title());
        assertEquals("EMBED", dto.videoType());
        assertEquals("<iframe>...</iframe>", dto.embedCode());
        assertNull(dto.videoUrl());
        assertEquals(10, dto.likeCount());
        assertEquals(100, dto.viewCount());
    }

    @Test
    void testTrackDTO() {
        // TrackDTO est une classe POJO
        TrackDTO dto = new TrackDTO();
        dto.setId(1L);
        dto.setTitle("Test Track");
        dto.setTrackNumber(1);
        dto.setDuration("3:45");
        dto.setAudioUrl("track.mp3");
        dto.setSpotifyUrl("https://spotify.com");
        dto.setLikeCount(25);
        dto.setPlayCount(1000);

        assertEquals(1L, dto.getId());
        assertEquals("Test Track", dto.getTitle());
        assertEquals(1, dto.getTrackNumber());
        assertEquals("3:45", dto.getDuration());
        assertEquals("track.mp3", dto.getAudioUrl());
        assertEquals("https://spotify.com", dto.getSpotifyUrl());
        assertEquals(25, dto.getLikeCount());
        assertEquals(1000, dto.getPlayCount());

        // Test constructors
        TrackDTO dto2 = new TrackDTO(2L, "Track 2", 2, "4:00", "audio2.mp3", "spotify2");
        assertEquals(2L, dto2.getId());
        assertEquals(0, dto2.getLikeCount());

        TrackDTO dto3 = new TrackDTO(3L, "Track 3", 3, "5:00", "audio3.mp3", "spotify3", 10);
        assertEquals(10, dto3.getLikeCount());

        TrackDTO dto4 = new TrackDTO(4L, "Track 4", 4, "6:00", "audio4.mp3", "spotify4", 20, 50);
        assertEquals(20, dto4.getLikeCount());
        assertEquals(50, dto4.getPlayCount());
    }

    @Test
    void testBiographyDTO() {
        BiographyDTO dto = new BiographyDTO();
        dto.setContent("Bio content");
        dto.setTimeline(Arrays.asList());
        dto.setPhotoUrls(Arrays.asList("photo1.jpg", "photo2.jpg"));

        assertEquals("Bio content", dto.getContent());
        assertNotNull(dto.getTimeline());
        assertEquals(2, dto.getPhotoUrls().size());

        // Test constructor
        BiographyDTO dto2 = new BiographyDTO("Content", Arrays.asList(), Arrays.asList());
        assertEquals("Content", dto2.getContent());
    }

    @Test
    void testBiographyDTO_TimelineEventDTO() {
        BiographyDTO.TimelineEventDTO event = new BiographyDTO.TimelineEventDTO();
        event.setYear(2020);
        event.setTitle("Event Title");
        event.setDescription("Event Description");
        event.setPhotoUrl("event.jpg");

        assertEquals(2020, event.getYear());
        assertEquals("Event Title", event.getTitle());
        assertEquals("Event Description", event.getDescription());
        assertEquals("event.jpg", event.getPhotoUrl());

        // Test constructor
        BiographyDTO.TimelineEventDTO event2 = new BiographyDTO.TimelineEventDTO(2021, "Event 2", "Desc 2", "photo2.jpg");
        assertEquals(2021, event2.getYear());
    }

    @Test
    void testConcertDTO() {
        ConcertDTO dto = new ConcertDTO();
        dto.setId(1L);
        dto.setLocation("Paris");
        dto.setDescription("Concert description");
        dto.setTicketUrl("https://tickets.com");

        assertEquals(1L, dto.getId());
        assertEquals("Paris", dto.getLocation());
        assertEquals("Concert description", dto.getDescription());
        assertEquals("https://tickets.com", dto.getTicketUrl());
    }

    @Test
    void testDailyStatsDTO() {
        // DailyStatsDTO uses Lombok @Data
        DailyStatsDTO dto = new DailyStatsDTO();
        dto.setDate("2024-01-01");
        dto.setTotalAttempts(50);
        dto.setSuccessfulAttempts(45);
        dto.setFailedAttempts(5);

        assertEquals("2024-01-01", dto.getDate());
        assertEquals(50, dto.getTotalAttempts());
        assertEquals(45, dto.getSuccessfulAttempts());
        assertEquals(5, dto.getFailedAttempts());
        assertEquals(90.0, dto.getSuccessRate(), 0.01);

        // Test constructor
        DailyStatsDTO dto2 = new DailyStatsDTO("2024-01-02", 100, 90, 10);
        assertEquals("2024-01-02", dto2.getDate());
        assertEquals(90.0, dto2.getSuccessRate(), 0.01);

        // Test zero attempts
        DailyStatsDTO dto3 = new DailyStatsDTO("2024-01-03", 0, 0, 0);
        assertEquals(0.0, dto3.getSuccessRate(), 0.01);
    }

    @Test
    void testHeroDataDTO() {
        HeroDataDTO dto = new HeroDataDTO();
        dto.setArtistName("Artist Name");
        dto.setTagline("Tagline");
        dto.setVideoUrl("video.mp4");
        dto.setPosterImageUrl("poster.jpg");

        assertEquals("Artist Name", dto.getArtistName());
        assertEquals("Tagline", dto.getTagline());
        assertEquals("video.mp4", dto.getVideoUrl());
        assertEquals("poster.jpg", dto.getPosterImageUrl());

        // Test constructor
        HeroDataDTO dto2 = new HeroDataDTO("Artist 2", "Tag 2", "video2.mp4", "poster2.jpg", null);
        assertEquals("Artist 2", dto2.getArtistName());
    }

    @Test
    void testHeroDataDTO_LatestReleaseDTO() {
        HeroDataDTO.LatestReleaseDTO release = new HeroDataDTO.LatestReleaseDTO();
        release.setTitle("New Album");
        release.setCoverUrl("cover.jpg");
        release.setSpotifyUrl("spotify");
        release.setAppleMusicUrl("apple");

        assertEquals("New Album", release.getTitle());
        assertEquals("cover.jpg", release.getCoverUrl());
        assertEquals("spotify", release.getSpotifyUrl());
        assertEquals("apple", release.getAppleMusicUrl());

        // Test constructor
        HeroDataDTO.LatestReleaseDTO release2 = new HeroDataDTO.LatestReleaseDTO("Album 2", "cover2.jpg", "spotify2", "apple2");
        assertEquals("Album 2", release2.getTitle());
    }

    @Test
    void testHomepageSettingsDTO() {
        // HomepageSettingsDTO est un record
        HomepageSettingsDTO dto = new HomepageSettingsDTO(
            "Hero Title",
            "Hero Subtitle",
            "video.mp4",
            "Welcome!",
            true,
            "Registration is open",
            true,
            false,
            true
        );

        assertEquals("Hero Title", dto.heroTitle());
        assertEquals("Hero Subtitle", dto.heroSubtitle());
        assertEquals("video.mp4", dto.heroBackgroundVideoUrl());
        assertEquals("Welcome!", dto.welcomeMessage());
        assertTrue(dto.registrationEnabled());
        assertEquals("Registration is open", dto.registrationMessage());
        assertTrue(dto.autoRotationEnabledVideos());
        assertFalse(dto.autoRotationEnabledTracks());
        assertTrue(dto.autoRotationEnabledGallery());
    }

    @Test
    void testLoginAttemptDTO() {
        LoginAttemptDTO dto = new LoginAttemptDTO();
        dto.setUsername("testuser");
        dto.setIpAddress("192.168.1.1");
        dto.setSuccess(true);
        dto.setFailureReason(null);
        dto.setUserAgent("Mozilla/5.0");

        assertEquals("testuser", dto.getUsername());
        assertEquals("192.168.1.1", dto.getIpAddress());
        assertTrue(dto.isSuccess());
        assertNull(dto.getFailureReason());
        assertEquals("Mozilla/5.0", dto.getUserAgent());

        // Test failure
        dto.setSuccess(false);
        dto.setFailureReason("Invalid password");
        assertFalse(dto.isSuccess());
        assertEquals("Invalid password", dto.getFailureReason());
    }

    @Test
    void testPasswordStrengthResult() {
        PasswordStrengthResult dto = new PasswordStrengthResult();

        // Test initial state
        assertTrue(dto.isValid());
        assertEquals(0, dto.getScore());
        assertNotNull(dto.getErrors());
        assertNotNull(dto.getSuggestions());

        // Test setters
        dto.setValid(true);
        dto.setScore(85);
        dto.addError("Too short");
        dto.addSuggestion("Add special characters");

        assertEquals(85, dto.getScore());
        assertEquals(PasswordStrengthResult.PasswordStrength.STRONG, dto.getStrength());
        assertEquals("Fort", dto.getStrengthLabel());
        assertEquals("yellow", dto.getStrengthColor());
        assertEquals(1, dto.getErrors().size());
        assertEquals(1, dto.getSuggestions().size());
        assertFalse(dto.isValid()); // Adding error sets valid to false

        // Test password strength levels
        PasswordStrengthResult weak = new PasswordStrengthResult();
        weak.setScore(20);
        assertEquals(PasswordStrengthResult.PasswordStrength.VERY_WEAK, weak.getStrength());
        assertEquals("red", weak.getStrengthColor());

        PasswordStrengthResult medium = new PasswordStrengthResult();
        medium.setScore(55);
        assertEquals(PasswordStrengthResult.PasswordStrength.MEDIUM, medium.getStrength());
        assertEquals("orange", medium.getStrengthColor());

        PasswordStrengthResult veryStrong = new PasswordStrengthResult();
        veryStrong.setScore(95);
        assertEquals(PasswordStrengthResult.PasswordStrength.VERY_STRONG, veryStrong.getStrength());
        assertEquals("green", veryStrong.getStrengthColor());
    }

    @Test
    void testPasswordStrengthResult_PasswordStrength() {
        // Test enum values and fromScore method
        assertEquals(PasswordStrengthResult.PasswordStrength.VERY_STRONG,
                     PasswordStrengthResult.PasswordStrength.fromScore(95));
        assertEquals(PasswordStrengthResult.PasswordStrength.STRONG,
                     PasswordStrengthResult.PasswordStrength.fromScore(75));
        assertEquals(PasswordStrengthResult.PasswordStrength.MEDIUM,
                     PasswordStrengthResult.PasswordStrength.fromScore(55));
        assertEquals(PasswordStrengthResult.PasswordStrength.WEAK,
                     PasswordStrengthResult.PasswordStrength.fromScore(35));
        assertEquals(PasswordStrengthResult.PasswordStrength.VERY_WEAK,
                     PasswordStrengthResult.PasswordStrength.fromScore(15));
    }

    @Test
    void testPhotoDTO() {
        PhotoDTO dto = new PhotoDTO();
        dto.setId(1L);
        dto.setUrl("/photos/test.jpg");
        dto.setThumbnailUrl("/thumbnails/test.jpg");
        dto.setCaption("Photo Caption");
        dto.setCategory("Concerts");
        dto.setDisplayOrder(1);
        dto.setLikeCount(15);
        dto.setViewCount(100);

        assertEquals(1L, dto.getId());
        assertEquals("/photos/test.jpg", dto.getUrl());
        assertEquals("/thumbnails/test.jpg", dto.getThumbnailUrl());
        assertEquals("Photo Caption", dto.getCaption());
        assertEquals("Concerts", dto.getCategory());
        assertEquals(1, dto.getDisplayOrder());
        assertEquals(15, dto.getLikeCount());
        assertEquals(100, dto.getViewCount());

        // Test constructors
        PhotoDTO dto2 = new PhotoDTO(2L, "url2.jpg", "thumb2.jpg", "Caption 2", "Category 2", 2);
        assertEquals(2L, dto2.getId());
        assertEquals(0, dto2.getLikeCount());

        PhotoDTO dto3 = new PhotoDTO(3L, "url3.jpg", "thumb3.jpg", "Caption 3", "Category 3", 3, 10);
        assertEquals(10, dto3.getLikeCount());

        PhotoDTO dto4 = new PhotoDTO(4L, "url4.jpg", "thumb4.jpg", "Caption 4", "Category 4", 4, 20, 50);
        assertEquals(20, dto4.getLikeCount());
        assertEquals(50, dto4.getViewCount());
    }

    @Test
    void testSecurityStatsDTO() {
        // SecurityStatsDTO uses Lombok @Data
        SecurityStatsDTO dto = new SecurityStatsDTO();
        dto.setTotalAttempts(1000);
        dto.setSuccessfulAttempts(950);
        dto.setFailedAttempts(50);
        dto.setBlockedAccounts(5);
        dto.setRecentAttempts(Arrays.asList());
        dto.setSuspiciousIps(Arrays.asList());
        dto.setDailyStats(new HashMap<>());

        assertEquals(1000, dto.getTotalAttempts());
        assertEquals(950, dto.getSuccessfulAttempts());
        assertEquals(50, dto.getFailedAttempts());
        assertEquals(5, dto.getBlockedAccounts());
        assertEquals(95.0, dto.getSuccessRate(), 0.01);
        assertEquals(5.0, dto.getFailureRate(), 0.01);

        // Test constructor
        Map<String, DailyStatsDTO> dailyStats = new HashMap<>();
        SecurityStatsDTO dto2 = new SecurityStatsDTO(100, 90, 10, 1, Arrays.asList(), Arrays.asList(), dailyStats);
        assertEquals(100, dto2.getTotalAttempts());
        assertEquals(90.0, dto2.getSuccessRate(), 0.01);

        // Test zero attempts
        SecurityStatsDTO dto3 = new SecurityStatsDTO(0, 0, 0, 0, Arrays.asList(), Arrays.asList(), new HashMap<>());
        assertEquals(0.0, dto3.getSuccessRate(), 0.01);
        assertEquals(0.0, dto3.getFailureRate(), 0.01);
    }

    @Test
    void testSuspiciousIpDTO() {
        // SuspiciousIpDTO uses Lombok @Data
        SuspiciousIpDTO dto = new SuspiciousIpDTO();
        dto.setIpAddress("192.168.1.1");
        dto.setFailedAttempts(12);
        dto.setSuccessfulAttempts(2);
        dto.setLastUsername("testuser");
        dto.setLastAttemptTime("2024-01-01 12:00:00");

        assertEquals("192.168.1.1", dto.getIpAddress());
        assertEquals(12, dto.getFailedAttempts());
        assertEquals(2, dto.getSuccessfulAttempts());
        assertEquals("testuser", dto.getLastUsername());
        assertEquals("2024-01-01 12:00:00", dto.getLastAttemptTime());

        // Test threat levels
        assertEquals("Élevé", dto.getThreatLevel());
        assertEquals("threat-high", dto.getThreatClass());

        // Test medium threat
        dto.setFailedAttempts(7);
        assertEquals("Moyen", dto.getThreatLevel());
        assertEquals("threat-medium", dto.getThreatClass());

        // Test low threat
        dto.setFailedAttempts(3);
        assertEquals("Faible", dto.getThreatLevel());
        assertEquals("threat-low", dto.getThreatClass());

        // Test constructor
        SuspiciousIpDTO dto2 = new SuspiciousIpDTO("10.0.0.1", 15, 1, "user2", "2024-01-02 13:00:00");
        assertEquals("10.0.0.1", dto2.getIpAddress());
        assertEquals("Élevé", dto2.getThreatLevel());
    }

    @Test
    void testUserRegistrationDTO() {
        // UserRegistrationDTO est un record
        UserRegistrationDTO dto = new UserRegistrationDTO(
            "testuser",
            "test@example.com",
            "SecurePass123!"
        );

        assertEquals("testuser", dto.username());
        assertEquals("test@example.com", dto.email());
        assertEquals("SecurePass123!", dto.password());
    }

    @Test
    void testDTOsWithNullValues() {
        // Test que les DTOs acceptent les valeurs null
        AlbumDTO album = new AlbumDTO();
        album.setId(null);
        album.setTitle(null);
        album.setTracks(null);

        assertNull(album.getId());
        assertNull(album.getTitle());
        assertNull(album.getTracks());

        BiographyDTO bio = new BiographyDTO();
        bio.setPhotoUrls(null);
        assertNull(bio.getPhotoUrls());

        LoginAttemptDTO login = new LoginAttemptDTO();
        login.setFailureReason(null);
        assertNull(login.getFailureReason());
    }

    @Test
    void testDTOsWithEmptyCollections() {
        // Test avec collections vides
        AlbumDTO album = new AlbumDTO();
        album.setTracks(Arrays.asList());
        assertNotNull(album.getTracks());
        assertTrue(album.getTracks().isEmpty());

        BiographyDTO bio = new BiographyDTO();
        bio.setTimeline(Arrays.asList());
        bio.setPhotoUrls(Arrays.asList());

        assertNotNull(bio.getTimeline());
        assertTrue(bio.getTimeline().isEmpty());
        assertNotNull(bio.getPhotoUrls());
        assertTrue(bio.getPhotoUrls().isEmpty());
    }
}
