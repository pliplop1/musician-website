package com.docker.service;

import com.docker.entity.Playlist;
import com.docker.entity.Track;
import com.docker.entity.TrackType;
import com.docker.repository.PlaylistRepository;
import com.docker.repository.TrackRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour PlaylistService
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class PlaylistServiceTest {

    @Autowired
    private PlaylistService playlistService;

    @Autowired
    private PlaylistRepository playlistRepository;

    @Autowired
    private TrackService trackService;

    @Autowired
    private TrackRepository trackRepository;

    @Test
    void testSavePlaylist_Success() {
        // Given
        Playlist playlist = new Playlist();
        playlist.setTitle("Ma Playlist");
        playlist.setDescription("Description test");
        playlist.setIsPublic(true);

        // When
        Playlist saved = playlistService.save(playlist);

        // Then
        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals("Ma Playlist", saved.getTitle());
        assertTrue(saved.getIsPublic());
    }

    @Test
    void testGetPublicPlaylists_ReturnsOnlyPublic() {
        // Given
        Playlist publicPlaylist = createAndSavePlaylist("Public", true, false);
        Playlist privatePlaylist = createAndSavePlaylist("Private", false, false);

        // When
        List<Playlist> publicPlaylists = playlistService.getPublicPlaylists();

        // Then
        assertTrue(publicPlaylists.stream().anyMatch(p -> p.getTitle().equals("Public")));
        assertFalse(publicPlaylists.stream().anyMatch(p -> p.getTitle().equals("Private")));
    }

    @Test
    void testGetFeaturedPlaylists_ReturnsOnlyFeatured() {
        // Given
        Playlist featured = createAndSavePlaylist("Featured", true, true);
        Playlist normal = createAndSavePlaylist("Normal", true, false);

        // When
        List<Playlist> featuredPlaylists = playlistService.getFeaturedPlaylists();

        // Then
        assertTrue(featuredPlaylists.stream().anyMatch(p -> p.getTitle().equals("Featured")));
    }

    @Test
    void testAddTrackToPlaylist_Success() {
        // Given
        Playlist playlist = createAndSavePlaylist("Playlist", true, false);
        trackService.saveEmbedTrack("Track 1", "https://spotify.com/1");
        Track track = trackRepository.findAll().stream()
                .filter(t -> t.getTitle().equals("Track 1"))
                .findFirst().orElseThrow();

        // When
        playlistService.addTrackToPlaylist(playlist.getId(), track.getId());

        // Then
        Playlist updated = playlistService.findById(playlist.getId());
        assertEquals(1, updated.getTrackCount());
        assertTrue(updated.getTracks().contains(track));
    }

    @Test
    void testRemoveTrackFromPlaylist_Success() {
        // Given
        Playlist playlist = createAndSavePlaylist("Playlist", true, false);
        trackService.saveEmbedTrack("Track 1", "https://spotify.com/1");
        Track track = trackRepository.findAll().stream()
                .filter(t -> t.getTitle().equals("Track 1"))
                .findFirst().orElseThrow();

        playlistService.addTrackToPlaylist(playlist.getId(), track.getId());

        // When
        playlistService.removeTrackFromPlaylist(playlist.getId(), track.getId());

        // Then
        Playlist updated = playlistService.findById(playlist.getId());
        assertEquals(0, updated.getTrackCount());
        assertFalse(updated.getTracks().contains(track));
    }

    @Test
    void testDeletePlaylist_Success() {
        // Given
        Playlist playlist = createAndSavePlaylist("Delete Me", true, false);
        Long id = playlist.getId();

        // When
        playlistService.deletePlaylist(id);

        // Then
        assertNull(playlistService.findById(id));
    }

    @Test
    void testGetTrackCount_ReturnsCorrectCount() {
        // Given
        Playlist playlist = new Playlist();
        playlist.setTitle("Test");

        trackService.saveEmbedTrack("Track 1", "url1");
        trackService.saveEmbedTrack("Track 2", "url2");

        Track track1 = trackRepository.findAll().get(0);
        Track track2 = trackRepository.findAll().get(1);

        playlist.addTrack(track1);
        playlist.addTrack(track2);

        // When
        int count = playlist.getTrackCount();

        // Then
        assertEquals(2, count);
    }

    /**
     * Helper method to create and save a playlist
     */
    private Playlist createAndSavePlaylist(String title, boolean isPublic, boolean isFeatured) {
        Playlist playlist = new Playlist();
        playlist.setTitle(title);
        playlist.setIsPublic(isPublic);
        playlist.setIsFeatured(isFeatured);
        return playlistService.save(playlist);
    }
}
