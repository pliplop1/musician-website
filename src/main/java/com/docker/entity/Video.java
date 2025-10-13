// /src/main/java/com/docker/entity/Video.java
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
public class Video {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description; // Description détaillée de la vidéo

    @Enumerated(EnumType.STRING)
    private VideoType videoType; // EMBED ou UPLOADED_FILE

    @Column(columnDefinition = "TEXT")
    private String embedCode; // Code <iframe> pour vidéos YouTube/Vimeo

    private String filename; // Nom du fichier vidéo si uploadé

    private String thumbnailFilename; // Miniature de la vidéo (image de couverture)

    private Integer durationSeconds; // Durée de la vidéo en secondes

    private String artist; // Artiste/Groupe (ex: "Duo Black & White")

    private String category; // Catégorie (Live, Studio, Interview, Behind the Scenes)

    @Column(name = "view_count", columnDefinition = "BIGINT DEFAULT 0")
    private Long viewCount = 0L; // Nombre de vues

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
    private Boolean isFeatured = false; // Vidéo mise en avant

    @Column(name = "is_published")
    private Boolean isPublished = true; // Publiée ou brouillon

    // Tags séparés par des virgules (ex: "rock,live,paris,2024")
    @Column(columnDefinition = "TEXT")
    private String tags;

    // Relations many-to-many avec les utilisateurs qui ont liké
    @ManyToMany
    @JoinTable(
        name = "video_likes",
        joinColumns = @JoinColumn(name = "video_id"),
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

    // Méthode utilitaire pour incrémenter les vues
    public void incrementViewCount() {
        this.viewCount = (this.viewCount == null ? 0L : this.viewCount) + 1;
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

    // Vérifier si un utilisateur a liké cette vidéo
    public boolean isLikedBy(User user) {
        return likedByUsers.contains(user);
    }
}
