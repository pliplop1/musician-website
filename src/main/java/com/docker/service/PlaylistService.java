package com.docker.service;

import com.docker.entity.Playlist;
import com.docker.entity.Track;
import com.docker.repository.PlaylistRepository;
import com.docker.repository.TrackRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PlaylistService {
    private final PlaylistRepository playlistRepository;
    private final TrackRepository trackRepository;

    public PlaylistService(PlaylistRepository playlistRepository, TrackRepository trackRepository) {
        this.playlistRepository = playlistRepository;
        this.trackRepository = trackRepository;
    }

    public List<Playlist> getAllPlaylists() {
        return playlistRepository.findAll();
    }

    public List<Playlist> getPublicPlaylists() {
        return playlistRepository.findByIsPublicTrue();
    }

    public List<Playlist> getFeaturedPlaylists() {
        return playlistRepository.findByIsFeaturedTrue();
    }

    public Playlist findById(Long id) {
        return playlistRepository.findById(id).orElse(null);
    }

    @Transactional
    public Playlist save(Playlist playlist) {
        return playlistRepository.save(playlist);
    }

    @Transactional
    public void addTrackToPlaylist(Long playlistId, Long trackId) {
        Playlist playlist = findById(playlistId);
        Track track = trackRepository.findById(trackId).orElse(null);
        if (playlist != null && track != null) {
            playlist.addTrack(track);
            playlistRepository.save(playlist);
        }
    }

    @Transactional
    public void removeTrackFromPlaylist(Long playlistId, Long trackId) {
        Playlist playlist = findById(playlistId);
        Track track = trackRepository.findById(trackId).orElse(null);
        if (playlist != null && track != null) {
            playlist.removeTrack(track);
            playlistRepository.save(playlist);
        }
    }

    @Transactional
    public void reorderTrack(Long playlistId, int fromIndex, int toIndex) {
        Playlist playlist = findById(playlistId);
        if (playlist != null) {
            playlist.reorderTrack(fromIndex, toIndex);
            playlistRepository.save(playlist);
        }
    }

    @Transactional
    public void deletePlaylist(Long id) {
        playlistRepository.deleteById(id);
    }
}
