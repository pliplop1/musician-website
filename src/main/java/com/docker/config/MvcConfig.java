package com.docker.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(MvcConfig.class);

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        logger.info("🔧 Configuration des ResourceHandlers...");

        // IMPORTANT: Sur Windows, utiliser file:/ (pas file:///)
        String baseDir = System.getProperty("user.dir").replace("\\", "/");

        // Règle pour les photos
        String photoUploadDir = "file:/" + baseDir + "/uploaded-photos/";
        logger.info("  📸 Photos: /uploaded-photos/** -> {}", photoUploadDir);
        registry.addResourceHandler("/uploaded-photos/**")
                .addResourceLocations(photoUploadDir);

        // Règle pour la musique
        String musicUploadDir = "file:/" + baseDir + "/uploaded-music/";
        logger.info("  🎵 Music: /uploaded-music/** -> {}", musicUploadDir);
        registry.addResourceHandler("/uploaded-music/**")
                .addResourceLocations(musicUploadDir);

        // AJOUT : Règle pour les avatars
        String avatarUploadDir = "file:/" + baseDir + "/uploaded-avatars/";
        logger.info("  👤 Avatars: /uploaded-avatars/** -> {}", avatarUploadDir);
        registry.addResourceHandler("/uploaded-avatars/**")
                .addResourceLocations(avatarUploadDir);

        logger.info("✅ ResourceHandlers configurés avec succès");
    }
}