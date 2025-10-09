// /src/main/java/com/docker/controller/VideoStreamController.java
package com.docker.controller.api;

import com.docker.entity.Video;
import com.docker.repository.VideoRepository;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
public class VideoStreamController {

    private final VideoRepository videoRepository;
    private final Path rootLocation = Paths.get("uploaded-videos");

    public VideoStreamController(VideoRepository videoRepository) {
        this.videoRepository = videoRepository;
    }

    @GetMapping("/api/videos/stream/{id}")
    public ResponseEntity<Resource> streamVideo(@PathVariable Long id) {
        Video video = videoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vidéo non trouvée !"));

        try {
            Path file = rootLocation.resolve(video.getFilename());
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                // Type de contenu pour les vidéos
                String contentType = "video/mp4"; // Pour les MP4 par exemple

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                throw new RuntimeException("Impossible de lire le fichier !");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Erreur lors de la lecture du fichier : " + e.getMessage());
        }
    }
}
