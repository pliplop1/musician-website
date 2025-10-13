package com.docker.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entité représentant une playlist de musiques
 * Permet de créer des collections organisées de musiques
 */
@Entity
@Getter
@Setter
public class Playlist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    // Image de couverture de la playlist
    @Column(name = "cover_image")
    private String coverImage;

    // Relation many-to-many avec Track
    @ManyToMany
    @JoinTable(
        name = "playlist_tracks",
        joinColumns = @JoinColumn(name = "playlist_id"),
        inverseJoinColumns = @JoinColumn(name = "track_id")
    )
    @OrderColumn(name = "track_order")
    private List<Track> tracks = new ArrayList<>();

    // Options de visibilité
    @Column(name = "is_public")
    private Boolean isPublic = true;

    @Column(name = "is_featured")
    private Boolean isFeatured = false;

    // Créateur de la playlist
    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

    // Timestamps
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Méthodes utilitaires
    public int getTrackCount() {
        return tracks != null ? tracks.size() : 0;
    }

    public Long getTotalDuration() {
        if (tracks == null || tracks.isEmpty()) {
            return 0L;
        }
        return tracks.stream()
            .filter(t -> t.getDurationSeconds() != null)
            .mapToLong(t -> t.getDurationSeconds().longValue())
            .sum();
    }

    public String getFormattedTotalDuration() {
        long totalSeconds = getTotalDuration();
        if (totalSeconds == 0) return "0:00";

        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;

        if (hours > 0) {
            return String.format("%d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%d:%02d", minutes, seconds);
        }
    }

    public void addTrack(Track track) {
        if (!tracks.contains(track)) {
            tracks.add(track);
        }
    }

    public void removeTrack(Track track) {
        tracks.remove(track);
    }

    public void reorderTrack(int fromIndex, int toIndex) {
        if (fromIndex >= 0 && fromIndex < tracks.size() &&
            toIndex >= 0 && toIndex < tracks.size()) {
            Track track = tracks.remove(fromIndex);
            tracks.add(toIndex, track);
        }
    }
}
