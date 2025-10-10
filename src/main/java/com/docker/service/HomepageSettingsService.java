package com.docker.service;

import com.docker.entity.HomepageSettings;
import com.docker.entity.Track;
import com.docker.entity.Video;
import com.docker.repository.HomepageSettingsRepository;
import com.docker.repository.TrackRepository;
import com.docker.repository.VideoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Service pour gérer les paramètres de la page d'accueil Vue.js
 */
@Service
@Transactional
public class HomepageSettingsService {

    private final HomepageSettingsRepository homepageSettingsRepository;
    private final VideoRepository videoRepository;
    private final TrackRepository trackRepository;

    public HomepageSettingsService(
            HomepageSettingsRepository homepageSettingsRepository,
            VideoRepository videoRepository,
            TrackRepository trackRepository) {
        this.homepageSettingsRepository = homepageSettingsRepository;
        this.videoRepository = videoRepository;
        this.trackRepository = trackRepository;
    }

    /**
     * Récupère les paramètres de la page d'accueil
     * S'ils n'existent pas, crée une instance par défaut
     * @return Les paramètres de la page d'accueil
     */
    @Transactional(readOnly = true)
    public HomepageSettings getSettings() {
        return homepageSettingsRepository.findFirstByOrderByIdAsc()
                .orElseGet(this::createDefaultSettings);
    }

    /**
     * Crée et sauvegarde les paramètres par défaut
     * @return Les paramètres créés
     */
    private HomepageSettings createDefaultSettings() {
        HomepageSettings settings = new HomepageSettings(
            "DUO BLACK & WHITE",
            "La musique qui vous transporte • France & Belgique"
        );
        settings.setWelcomeMessage("Bienvenue sur notre site officiel !");
        settings.setRegistrationMessage("Rejoignez notre communauté de fans !");
        return homepageSettingsRepository.save(settings);
    }

    /**
     * Met à jour les paramètres de base (titre, sous-titre)
     * @param heroTitle Titre principal
     * @param heroSubtitle Sous-titre
     * @return Les paramètres mis à jour
     */
    public HomepageSettings updateBasicSettings(String heroTitle, String heroSubtitle) {
        HomepageSettings settings = getSettings();
        settings.setHeroTitle(heroTitle);
        settings.setHeroSubtitle(heroSubtitle);
        return homepageSettingsRepository.save(settings);
    }

    /**
     * Met à jour les vidéos featured (max 3)
     * @param videoIds Liste des IDs des vidéos à afficher
     * @return Les paramètres mis à jour
     * @throws IllegalArgumentException si plus de 3 vidéos ou vidéo introuvable
     */
    public HomepageSettings updateFeaturedVideos(List<Long> videoIds) {
        if (videoIds == null) {
            videoIds = new ArrayList<>();
        }

        if (videoIds.size() > 3) {
            throw new IllegalArgumentException("Maximum 3 vidéos featured autorisées");
        }

        HomepageSettings settings = getSettings();
        List<Video> videos = new ArrayList<>();

        for (Long videoId : videoIds) {
            Video video = videoRepository.findById(videoId)
                    .orElseThrow(() -> new IllegalArgumentException("Vidéo introuvable : " + videoId));
            videos.add(video);
        }

        settings.setFeaturedVideos(videos);
        return homepageSettingsRepository.save(settings);
    }

    /**
     * Met à jour les tracks featured (max 3)
     * @param trackIds Liste des IDs des tracks à afficher
     * @return Les paramètres mis à jour
     * @throws IllegalArgumentException si plus de 3 tracks ou track introuvable
     */
    public HomepageSettings updateFeaturedTracks(List<Long> trackIds) {
        if (trackIds == null) {
            trackIds = new ArrayList<>();
        }

        if (trackIds.size() > 3) {
            throw new IllegalArgumentException("Maximum 3 tracks featured autorisés");
        }

        HomepageSettings settings = getSettings();
        List<Track> tracks = new ArrayList<>();

        for (Long trackId : trackIds) {
            Track track = trackRepository.findById(trackId)
                    .orElseThrow(() -> new IllegalArgumentException("Track introuvable : " + trackId));
            tracks.add(track);
        }

        settings.setFeaturedTracks(tracks);
        return homepageSettingsRepository.save(settings);
    }

    /**
     * Met à jour tous les paramètres en une seule fois
     * @param heroTitle Titre principal
     * @param heroSubtitle Sous-titre
     * @param videoIds IDs des vidéos featured
     * @param trackIds IDs des tracks featured
     * @return Les paramètres mis à jour
     */
    public HomepageSettings updateAllSettings(
            String heroTitle,
            String heroSubtitle,
            List<Long> videoIds,
            List<Long> trackIds) {

        HomepageSettings settings = getSettings();

        // Mise à jour des textes
        settings.setHeroTitle(heroTitle);
        settings.setHeroSubtitle(heroSubtitle);

        // Mise à jour des vidéos featured
        if (videoIds != null && !videoIds.isEmpty()) {
            if (videoIds.size() > 3) {
                throw new IllegalArgumentException("Maximum 3 vidéos featured autorisées");
            }
            List<Video> videos = new ArrayList<>();
            for (Long videoId : videoIds) {
                Video video = videoRepository.findById(videoId)
                        .orElseThrow(() -> new IllegalArgumentException("Vidéo introuvable : " + videoId));
                videos.add(video);
            }
            settings.setFeaturedVideos(videos);
        } else {
            settings.setFeaturedVideos(new ArrayList<>());
        }

        // Mise à jour des tracks featured
        if (trackIds != null && !trackIds.isEmpty()) {
            if (trackIds.size() > 3) {
                throw new IllegalArgumentException("Maximum 3 tracks featured autorisés");
            }
            List<Track> tracks = new ArrayList<>();
            for (Long trackId : trackIds) {
                Track track = trackRepository.findById(trackId)
                        .orElseThrow(() -> new IllegalArgumentException("Track introuvable : " + trackId));
                tracks.add(track);
            }
            settings.setFeaturedTracks(tracks);
        } else {
            settings.setFeaturedTracks(new ArrayList<>());
        }

        return homepageSettingsRepository.save(settings);
    }

    /**
     * Active ou désactive l'inscription publique
     * @param enabled true pour activer, false pour désactiver
     * @return Les paramètres mis à jour
     */
    public HomepageSettings setRegistrationEnabled(boolean enabled) {
        HomepageSettings settings = getSettings();
        settings.setRegistrationEnabled(enabled);
        return homepageSettingsRepository.save(settings);
    }

    /**
     * Met à jour le message d'accueil
     * @param welcomeMessage Le nouveau message
     * @return Les paramètres mis à jour
     */
    public HomepageSettings updateWelcomeMessage(String welcomeMessage) {
        HomepageSettings settings = getSettings();
        settings.setWelcomeMessage(welcomeMessage);
        return homepageSettingsRepository.save(settings);
    }

    /**
     * Met à jour l'URL de la vidéo de fond du Hero
     * @param videoUrl L'URL de la vidéo
     * @return Les paramètres mis à jour
     */
    public HomepageSettings updateHeroBackgroundVideo(String videoUrl) {
        HomepageSettings settings = getSettings();
        settings.setHeroBackgroundVideoUrl(videoUrl);
        return homepageSettingsRepository.save(settings);
    }
}
