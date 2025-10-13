// /src/main/java/com/docker/entity/Track.java
package com.docker.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
public class Track {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description; // Description de la chanson

    @Enumerated(EnumType.STRING)
    private TrackType trackType; // EMBED ou UPLOADED_FILE

    @Column(columnDefinition = "TEXT")
    private String embedCode; // Code <iframe> pour Spotify/SoundCloud/YouTube

    private String filename; // Nom du fichier audio si uploadé

    private String coverImageFilename; // Image de couverture de l'album/single

    private Integer durationSeconds; // Durée de la chanson en secondes

    private String artist; // Artiste/Groupe (ex: "Duo Black & White")

    private String album; // Nom de l'album

    private String genre; // Genre musical (Rock, Jazz, Pop, Classical, etc.)

    private String category; // Catégorie (Live, Studio, Acoustic, Remix)

    private Integer trackNumber; // Numéro de piste dans l'album

    private Integer releaseYear; // Année de sortie

    @Column(name = "play_count", columnDefinition = "BIGINT DEFAULT 0")
    private Long playCount = 0L; // Nombre d'écoutes

    @Column(name = "like_count", columnDefinition = "INT DEFAULT 0")
    private Integer likeCount = 0; // Nombre de likes

    @Column(name = "published_at")
    private LocalDateTime publishedAt; // Date de publication

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt; // Date de création dans la BDD

    @Column(name = "updated_at")
    private LocalDateTime updatedAt; // Date de dernière modification

    @Column(name = "display_order")
    private Integer displayOrder = 0; // Ordre d'affichage

    @Column(name = "is_featured")
    private Boolean isFeatured = false; // Chanson mise en avant

    @Column(name = "is_published")
    private Boolean isPublished = true; // Publiée ou brouillon

    // Tags séparés par des virgules (ex: "rock,guitar,instrumental,2024")
    @Column(columnDefinition = "TEXT")
    private String tags;

    // URL externe (ex: Spotify link, Apple Music, Deezer)
    @Column(length = 500)
    private String externalUrl;

    // Relations many-to-many avec les utilisateurs qui ont liké
    @ManyToMany
    @JoinTable(
        name = "track_likes",
        joinColumns = @JoinColumn(name = "track_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> likedByUsers = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (publishedAt == null) {
            publishedAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Méthode utilitaire pour incrémenter les écoutes
    public void incrementPlayCount() {
        this.playCount = (this.playCount == null ? 0L : this.playCount) + 1;
    }

    // Méthode utilitaire pour ajouter un like
    public void addLike(User user) {
        if (likedByUsers.add(user)) {
            this.likeCount = (this.likeCount == null ? 0 : this.likeCount) + 1;
        }
    }

    // Méthode utilitaire pour retirer un like
    public void removeLike(User user) {
        if (likedByUsers.remove(user)) {
            this.likeCount = Math.max(0, (this.likeCount == null ? 0 : this.likeCount) - 1);
        }
    }

    // Vérifier si un utilisateur a liké cette chanson
    public boolean isLikedBy(User user) {
        return likedByUsers.contains(user);
    }

    // Formater la durée en format MM:SS
    public String getFormattedDuration() {
        if (durationSeconds == null || durationSeconds == 0) {
            return "0:00";
        }
        int minutes = durationSeconds / 60;
        int seconds = durationSeconds % 60;
        return String.format("%d:%02d", minutes, seconds);
    }
}