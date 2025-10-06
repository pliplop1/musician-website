// /src/main/java/com/docker/config/MvcConfig.java

package com.docker.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Règle pour les photos 
        String photoUploadDir = "file:///" + System.getProperty("user.dir").replace("\\", "/") + "/uploaded-photos/";
        registry.addResourceHandler("/uploaded-photos/**")
                .addResourceLocations(photoUploadDir);
                
        // AJOUT : Règle pour la musique
        String musicUploadDir = "file:///" + System.getProperty("user.dir").replace("\\", "/") + "/uploaded-music/";
        registry.addResourceHandler("/uploaded-music/**")
                .addResourceLocations(musicUploadDir);
    }
}