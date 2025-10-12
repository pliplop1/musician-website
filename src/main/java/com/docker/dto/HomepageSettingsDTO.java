package com.docker.dto;

/**
 * DTO pour les paramètres de la page d'accueil
 * Utilisé pour envoyer les données à Vue.js
 */
public record HomepageSettingsDTO(
    String heroTitle,
    String heroSubtitle,
    String heroBackgroundVideoUrl,
    String welcomeMessage,
    Boolean registrationEnabled,
    String registrationMessage,
    Boolean autoRotationEnabledVideos,
    Boolean autoRotationEnabledTracks,
    Boolean autoRotationEnabledGallery
) {
}
