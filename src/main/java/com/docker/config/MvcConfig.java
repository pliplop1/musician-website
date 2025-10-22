package com.docker.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(MvcConfig.class);

    @Value("${musician.upload.photos-dir}")
    private String photosDir;

    @Value("${musician.upload.music-dir}")
    private String musicDir;

    @Value("${musician.upload.videos-dir}")
    private String videosDir;

    @Value("${musician.upload.avatars-dir}")
    private String avatarsDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        logger.info("🔧 Configuration des ResourceHandlers...");

        // Règle pour les photos
        String photoUploadDir = "file:" + photosDir + "/";
        logger.info("  📸 Photos: /uploaded-photos/** -> {}", photoUploadDir);
        registry.addResourceHandler("/uploaded-photos/**")
                .addResourceLocations(photoUploadDir);

        // Règle pour la musique
        String musicUploadDir = "file:" + musicDir + "/";
        logger.info("  🎵 Music: /uploaded-music/** -> {}", musicUploadDir);
        registry.addResourceHandler("/uploaded-music/**")
                .addResourceLocations(musicUploadDir);

        // Règle pour les vidéos
        String videoUploadDir = "file:" + videosDir + "/";
        logger.info("  🎬 Videos: /uploaded-videos/** -> {}", videoUploadDir);
        registry.addResourceHandler("/uploaded-videos/**")
                .addResourceLocations(videoUploadDir);

        // Règle pour les avatars
        String avatarUploadDir = "file:" + avatarsDir + "/";
        logger.info("  👤 Avatars: /uploaded-avatars/** -> {}", avatarUploadDir);
        registry.addResourceHandler("/uploaded-avatars/**")
                .addResourceLocations(avatarUploadDir);

        logger.info("✅ ResourceHandlers configurés avec succès");
    }
}