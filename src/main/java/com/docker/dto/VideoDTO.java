package com.docker.dto;

/**
 * DTO pour les vidéos featured
 * Utilisé pour envoyer les vidéos à Vue.js
 */
public record VideoDTO(
    Long id,
    String title,
    String videoType,      // "EMBED" ou "UPLOADED_FILE"
    String embedCode,      // Code embed YouTube/Vimeo si type = EMBED
    String videoUrl        // URL du fichier uploadé si type = UPLOADED_FILE
) {
}
