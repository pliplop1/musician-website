package com.docker.controller;

import com.docker.entity.HomepageSettings;
import com.docker.entity.Photo;
import com.docker.entity.Track;
import com.docker.entity.Video;
import com.docker.service.HomepageSettingsService;
import com.docker.service.PhotoService;
import com.docker.service.TrackService;
import com.docker.service.VideoService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

/**
 * Contrôleur pour gérer l'apparence de la page d'accueil Vue.js depuis l'admin
 */
@Controller
@RequestMapping("/admin/homepage")
@PreAuthorize("hasRole('ADMIN')")
public class AdminHomepageController {

    private final HomepageSettingsService homepageSettingsService;
    private final VideoService videoService;
    private final TrackService trackService;
    private final PhotoService photoService;

    public AdminHomepageController(
            HomepageSettingsService homepageSettingsService,
            VideoService videoService,
            TrackService trackService,
            PhotoService photoService) {
        this.homepageSettingsService = homepageSettingsService;
        this.videoService = videoService;
        this.trackService = trackService;
        this.photoService = photoService;
    }

    /**
     * Page de gestion de l'apparence Vue.js
     */
    @GetMapping("/settings")
    public String showHomepageSettings(Model model) {
        HomepageSettings settings = homepageSettingsService.getSettings();

        // Toutes les vidéos disponibles
        List<Video> allVideos = videoService.getAllVideos();

        // Tous les tracks disponibles
        List<Track> allTracks = trackService.getAllTracks();

        // Toutes les photos disponibles
        List<Photo> allPhotos = photoService.getAllPhotos();

        // Récupérer les IDs des vidéos featured actuelles
        List<Long> featuredVideoIds = settings.getFeaturedVideos().stream()
                .map(Video::getId)
                .toList();

        // Récupérer les IDs des tracks featured actuels
        List<Long> featuredTrackIds = settings.getFeaturedTracks().stream()
                .map(Track::getId)
                .toList();

        // Récupérer les IDs des photos featured actuelles
        List<Long> featuredPhotoIds = settings.getFeaturedPhotos().stream()
                .map(Photo::getId)
                .toList();

        model.addAttribute("settings", settings);
        model.addAttribute("allVideos", allVideos);
        model.addAttribute("allTracks", allTracks);
        model.addAttribute("allPhotos", allPhotos);
        model.addAttribute("featuredVideoIds", featuredVideoIds);
        model.addAttribute("featuredTrackIds", featuredTrackIds);
        model.addAttribute("featuredPhotoIds", featuredPhotoIds);

        return "admin/homepage-settings";
    }

    /**
     * Sauvegarder les paramètres de la page d'accueil
     */
    @PostMapping("/settings/save")
    public String saveHomepageSettings(
            @RequestParam("heroTitle") String heroTitle,
            @RequestParam("heroSubtitle") String heroSubtitle,
            @RequestParam(value = "heroBackgroundVideoUrl", required = false) String heroBackgroundVideoUrl,
            @RequestParam(value = "welcomeMessage", required = false) String welcomeMessage,
            @RequestParam(value = "registrationEnabled", defaultValue = "false") Boolean registrationEnabled,
            @RequestParam(value = "registrationMessage", required = false) String registrationMessage,
            @RequestParam(value = "autoRotationEnabledVideos", defaultValue = "false") Boolean autoRotationEnabledVideos,
            @RequestParam(value = "autoRotationEnabledTracks", defaultValue = "false") Boolean autoRotationEnabledTracks,
            @RequestParam(value = "autoRotationEnabledGallery", defaultValue = "false") Boolean autoRotationEnabledGallery,
            @RequestParam(value = "featuredVideoIds", required = false) List<Long> videoIds,
            @RequestParam(value = "featuredTrackIds", required = false) List<Long> trackIds,
            @RequestParam(value = "featuredPhotoIds", required = false) List<Long> photoIds,
            RedirectAttributes redirectAttributes) {

        try {
            // Mise à jour des paramètres principaux et featured videos/tracks/photos
            HomepageSettings settings = homepageSettingsService.updateAllSettings(
                    heroTitle,
                    heroSubtitle,
                    videoIds != null ? videoIds : new ArrayList<>(),
                    trackIds != null ? trackIds : new ArrayList<>(),
                    photoIds != null ? photoIds : new ArrayList<>()
            );

            // Mise à jour des autres paramètres
            settings.setHeroBackgroundVideoUrl(heroBackgroundVideoUrl);
            settings.setWelcomeMessage(welcomeMessage);
            settings.setRegistrationEnabled(registrationEnabled);
            settings.setRegistrationMessage(registrationMessage);
            settings.setAutoRotationEnabledVideos(autoRotationEnabledVideos);
            settings.setAutoRotationEnabledTracks(autoRotationEnabledTracks);
            settings.setAutoRotationEnabledGallery(autoRotationEnabledGallery);

            // Sauvegarde finale de toutes les modifications
            homepageSettingsService.save(settings);

            redirectAttributes.addFlashAttribute("successMessage",
                    "✅ Paramètres de la page d'accueil sauvegardés avec succès !");

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "❌ Erreur : " + e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "❌ Erreur lors de la sauvegarde : " + e.getMessage());
        }

        return "redirect:/admin/homepage/settings";
    }

    /**
     * Activer/Désactiver l'inscription publique
     */
    @PostMapping("/settings/toggle-registration")
    public String toggleRegistration(
            @RequestParam("enabled") Boolean enabled,
            RedirectAttributes redirectAttributes) {

        try {
            homepageSettingsService.setRegistrationEnabled(enabled);
            String status = enabled ? "activée" : "désactivée";
            redirectAttributes.addFlashAttribute("successMessage",
                    "✅ Inscription publique " + status + " !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "❌ Erreur : " + e.getMessage());
        }

        return "redirect:/admin/homepage/settings";
    }
}
