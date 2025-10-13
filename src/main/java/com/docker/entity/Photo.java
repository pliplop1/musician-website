// /src/main/java/com/docker/entity/Photo.java

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
public class Photo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String filename; // Nom du fichier image (ex: concert_paris_20240615.jpg)

    private String title; // Titre de la photo

    @Column(columnDefinition = "TEXT")
    private String description; // Description détaillée

    private String photographer; // Nom du photographe

    private String location; // Lieu de la prise de vue (ex: "Salle Pleyel, Paris")

    @Column(name = "taken_at")
    private LocalDateTime takenAt; // Date de la prise de vue

    private String category; // Catégorie (Concert, Studio, Backstage, Promo, Portrait)

    @Column(name = "view_count", columnDefinition = "BIGINT DEFAULT 0")
    private Long viewCount = 0L; // Nombre de vues

    @Column(name = "like_count", columnDefinition = "INT DEFAULT 0")
    private Integer likeCount = 0; // Nombre de likes

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt; // Date d'upload dans la BDD

    @Column(name = "updated_at")
    private LocalDateTime updatedAt; // Date de dernière modification

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder = 0; // Ordre d'affichage dans la galerie

    @Column(name = "is_featured")
    private Boolean isFeatured = false; // Photo mise en avant

    @Column(name = "is_published")
    private Boolean isPublished = true; // Publiée ou brouillon

    // Tags séparés par des virgules (ex: "concert,live,paris,2024")
    @Column(columnDefinition = "TEXT")
    private String tags;

    // Métadonnées techniques
    private String originalFilename; // Nom de fichier original avant upload
    private Long fileSize; // Taille du fichier en bytes
    private String mimeType; // Type MIME (image/jpeg, image/png, image/webp)
    private Integer width; // Largeur en pixels
    private Integer height; // Hauteur en pixels

    // Relations many-to-many avec les utilisateurs qui ont liké
    @ManyToMany
    @JoinTable(
        name = "photo_likes",
        joinColumns = @JoinColumn(name = "photo_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> likedByUsers = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (takenAt == null) {
            takenAt = LocalDateTime.now();
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

    // Vérifier si un utilisateur a liké cette photo
    public boolean isLikedBy(User user) {
        return likedByUsers.contains(user);
    }

    // Formater la taille du fichier en format lisible (KB, MB)
    public String getFormattedFileSize() {
        if (fileSize == null || fileSize == 0) {
            return "0 KB";
        }
        if (fileSize < 1024) {
            return fileSize + " B";
        } else if (fileSize < 1024 * 1024) {
            return String.format("%.1f KB", fileSize / 1024.0);
        } else {
            return String.format("%.1f MB", fileSize / (1024.0 * 1024.0));
        }
    }

    // Obtenir le ratio d'aspect (aspect ratio)
    public String getAspectRatio() {
        if (width == null || height == null || width == 0 || height == 0) {
            return "Unknown";
        }
        int gcd = gcd(width, height);
        return (width / gcd) + ":" + (height / gcd);
    }

    // Plus grand commun diviseur (pour le ratio d'aspect)
    private int gcd(int a, int b) {
        return b == 0 ? a : gcd(b, a % b);
    }
}