package com.docker.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Entité pour gérer les paramètres de la page d'accueil Vue.js
 * Permet à l'admin de contrôler l'apparence et le contenu de la vitrine
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "homepage_settings")
public class HomepageSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ====== SECTION HERO ======

    /**
     * Titre principal affiché dans la section Hero
     * Ex: "DUO BLACK & WHITE"
     */
    @Column(nullable = false, length = 200)
    private String heroTitle = "DUO BLACK & WHITE";

    /**
     * Sous-titre/tagline affiché sous le titre principal
     * Ex: "La musique qui vous transporte • France & Belgique"
     */
    @Column(nullable = false, length = 500)
    private String heroSubtitle = "La musique qui vous transporte";

    /**
     * URL de la vidéo de fond pour la section Hero
     * Peut être une URL locale (/uploaded-videos/...) ou externe
     */
    @Column(length = 500)
    private String heroBackgroundVideoUrl;

    // ====== VIDÉOS FEATURED (1-3) ======

    /**
     * Liste des vidéos mises en avant sur la page d'accueil
     * Maximum 3 vidéos pour ne pas surcharger la page
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "homepage_featured_videos",
        joinColumns = @JoinColumn(name = "homepage_settings_id"),
        inverseJoinColumns = @JoinColumn(name = "video_id")
    )
    @OrderColumn(name = "display_order")
    private List<Video> featuredVideos = new ArrayList<>();

    // ====== TRACKS/SONS FEATURED (1-3) ======

    /**
     * Liste des morceaux/sons mis en avant sur la page d'accueil
     * Maximum 3 tracks pour ne pas surcharger la page
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "homepage_featured_tracks",
        joinColumns = @JoinColumn(name = "homepage_settings_id"),
        inverseJoinColumns = @JoinColumn(name = "track_id")
    )
    @OrderColumn(name = "display_order")
    private List<Track> featuredTracks = new ArrayList<>();

    // ====== PHOTOS FEATURED (1-3) ======

    /**
     * Liste des photos mises en avant sur la page d'accueil
     * Maximum 3 photos pour ne pas surcharger la page
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "homepage_featured_photos",
        joinColumns = @JoinColumn(name = "homepage_settings_id"),
        inverseJoinColumns = @JoinColumn(name = "photo_id")
    )
    @OrderColumn(name = "display_order")
    private List<Photo> featuredPhotos = new ArrayList<>();

    // ====== MESSAGE D'ACCUEIL ======

    /**
     * Message d'accueil personnalisé affiché sur la page
     * Support du Markdown pour la mise en forme
     */
    @Column(columnDefinition = "TEXT")
    private String welcomeMessage;

    // ====== PARAMÈTRES D'INSCRIPTION ======

    /**
     * Active/Désactive l'inscription publique depuis Vue.js
     * Si false, le formulaire d'inscription sera masqué
     */
    @Column(nullable = false)
    private Boolean registrationEnabled = true;

    /**
     * Message personnalisé affiché sur le formulaire d'inscription
     * Ex: "Rejoignez notre communauté de fans !"
     */
    @Column(length = 500)
    private String registrationMessage;

    // ====== ROTATION AUTOMATIQUE PAR TYPE DE CONTENU ======

    /**
     * Active/Désactive la rotation automatique des VIDÉOS toutes les 24h
     * Si true : sélection aléatoire de 3 vidéos toutes les 24h (côté frontend)
     * Si false : utilise les vidéos sélectionnées manuellement (featuredVideos)
     * Par défaut : true (rotation automatique activée)
     */
    @Column(nullable = false)
    private Boolean autoRotationEnabledVideos = true;

    /**
     * Active/Désactive la rotation automatique des MUSIQUES toutes les 24h
     * Si true : sélection aléatoire de 3 musiques toutes les 24h (côté frontend)
     * Si false : utilise les musiques sélectionnées manuellement (featuredTracks)
     * Par défaut : true (rotation automatique activée)
     */
    @Column(nullable = false)
    private Boolean autoRotationEnabledTracks = true;

    /**
     * Active/Désactive la rotation automatique de la GALERIE toutes les 24h
     * Si true : sélection aléatoire de 3 photos toutes les 24h (côté frontend)
     * Si false : utilise les photos sélectionnées manuellement (featuredPhotos)
     * Par défaut : true (rotation automatique activée)
     */
    @Column(nullable = false)
    private Boolean autoRotationEnabledGallery = true;

    // ====== CONSTRUCTEUR AVEC VALEURS PAR DÉFAUT ======

    /**
     * Constructeur avec valeurs par défaut pour initialisation
     */
    public HomepageSettings(String heroTitle, String heroSubtitle) {
        this.heroTitle = heroTitle;
        this.heroSubtitle = heroSubtitle;
        this.registrationEnabled = true;
        this.autoRotationEnabledVideos = true; // Rotation automatique vidéos activée par défaut
        this.autoRotationEnabledTracks = true; // Rotation automatique musiques activée par défaut
        this.autoRotationEnabledGallery = true; // Rotation automatique galerie activée par défaut
        this.featuredVideos = new ArrayList<>();
        this.featuredTracks = new ArrayList<>();
        this.featuredPhotos = new ArrayList<>();
    }

    // ====== MÉTHODES UTILITAIRES ======

    /**
     * Ajoute une vidéo à la liste des featured (max 3)
     * @param video La vidéo à ajouter
     * @throws IllegalStateException si déjà 3 vidéos
     */
    public void addFeaturedVideo(Video video) {
        if (featuredVideos.size() >= 3) {
            throw new IllegalStateException("Maximum 3 vidéos featured autorisées");
        }
        if (!featuredVideos.contains(video)) {
            featuredVideos.add(video);
        }
    }

    /**
     * Retire une vidéo de la liste des featured
     * @param video La vidéo à retirer
     */
    public void removeFeaturedVideo(Video video) {
        featuredVideos.remove(video);
    }

    /**
     * Ajoute un track à la liste des featured (max 3)
     * @param track Le track à ajouter
     * @throws IllegalStateException si déjà 3 tracks
     */
    public void addFeaturedTrack(Track track) {
        if (featuredTracks.size() >= 3) {
            throw new IllegalStateException("Maximum 3 tracks featured autorisés");
        }
        if (!featuredTracks.contains(track)) {
            featuredTracks.add(track);
        }
    }

    /**
     * Retire un track de la liste des featured
     * @param track Le track à retirer
     */
    public void removeFeaturedTrack(Track track) {
        featuredTracks.remove(track);
    }

    /**
     * Remplace complètement la liste des vidéos featured
     * @param videos Nouvelle liste de vidéos (max 3)
     * @throws IllegalArgumentException si plus de 3 vidéos
     */
    public void setFeaturedVideos(List<Video> videos) {
        if (videos.size() > 3) {
            throw new IllegalArgumentException("Maximum 3 vidéos featured autorisées");
        }
        this.featuredVideos = new ArrayList<>(videos);
    }

    /**
     * Remplace complètement la liste des tracks featured
     * @param tracks Nouvelle liste de tracks (max 3)
     * @throws IllegalArgumentException si plus de 3 tracks
     */
    public void setFeaturedTracks(List<Track> tracks) {
        if (tracks.size() > 3) {
            throw new IllegalArgumentException("Maximum 3 tracks featured autorisés");
        }
        this.featuredTracks = new ArrayList<>(tracks);
    }

    /**
     * Ajoute une photo à la liste des featured (max 3)
     * @param photo La photo à ajouter
     * @throws IllegalStateException si déjà 3 photos
     */
    public void addFeaturedPhoto(Photo photo) {
        if (featuredPhotos.size() >= 3) {
            throw new IllegalStateException("Maximum 3 photos featured autorisées");
        }
        if (!featuredPhotos.contains(photo)) {
            featuredPhotos.add(photo);
        }
    }

    /**
     * Retire une photo de la liste des featured
     * @param photo La photo à retirer
     */
    public void removeFeaturedPhoto(Photo photo) {
        featuredPhotos.remove(photo);
    }

    /**
     * Remplace complètement la liste des photos featured
     * @param photos Nouvelle liste de photos (max 3)
     * @throws IllegalArgumentException si plus de 3 photos
     */
    public void setFeaturedPhotos(List<Photo> photos) {
        if (photos.size() > 3) {
            throw new IllegalArgumentException("Maximum 3 photos featured autorisées");
        }
        this.featuredPhotos = new ArrayList<>(photos);
    }
}
