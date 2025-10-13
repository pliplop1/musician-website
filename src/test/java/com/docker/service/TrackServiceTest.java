package com.docker.service;

import com.docker.entity.Track;
import com.docker.entity.TrackType;
import com.docker.repository.TrackRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour TrackService
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class TrackServiceTest {

    @Autowired
    private TrackService trackService;

    @Autowired
    private TrackRepository trackRepository;

    @Test
    void testGetAllTracks_ReturnsAllTracks() {
        // Given
        createAndSaveEmbedTrack("Track 1", "<iframe>embed1</iframe>");
        createAndSaveEmbedTrack("Track 2", "<iframe>embed2</iframe>");

        // When
        List<Track> tracks = trackService.getAllTracks();

        // Then
        assertTrue(tracks.size() >= 2);
    }

    @Test
    void testSaveEmbedTrack_Success() {
        // Given
        String title = "Test Track";
        String embedCode = "<iframe src='spotify'></iframe>";

        // When
        trackService.saveEmbedTrack(title, embedCode);

        // Then
        List<Track> tracks = trackRepository.findAll();
        Track savedTrack = tracks.stream()
                .filter(t -> t.getTitle().equals(title))
                .findFirst()
                .orElse(null);

        assertNotNull(savedTrack);
        assertEquals(title, savedTrack.getTitle());
        assertEquals(embedCode, savedTrack.getEmbedCode());
        assertEquals(TrackType.EMBED, savedTrack.getTrackType());
    }

    @Test
    void testDeleteTrack_ExistingEmbedTrack_Success() throws Exception {
        // Given
        Track track = createAndSaveEmbedTrack("Delete Me", "<iframe>test</iframe>");

        // When
        trackService.deleteTrack(track.getId());

        // Then
        assertFalse(trackRepository.findById(track.getId()).isPresent());
    }

    /**
     * Helper method to create and save an embed track
     */
    private Track createAndSaveEmbedTrack(String title, String embedCode) {
        trackService.saveEmbedTrack(title, embedCode);
        return trackRepository.findAll().stream()
                .filter(t -> t.getTitle().equals(title))
                .findFirst()
                .orElseThrow();
    }
}
