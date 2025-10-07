// /src/main/java/com/docker/controller/TrackStreamController.java
package com.docker.controller.api;

import com.docker.entity.Track;
import com.docker.repository.TrackRepository;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
// IMPORTEZ RestController AU LIEU DE Controller
import org.springframework.web.bind.annotation.RestController; 
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController // <-- CORRECTION : Changez @Controller par @RestController
public class TrackStreamController {

    private final TrackRepository trackRepository;
    private final Path rootLocation = Paths.get("uploaded-music");

    public TrackStreamController(TrackRepository trackRepository) {
        this.trackRepository = trackRepository;
    }

    @GetMapping("/api/tracks/stream/{id}")
    public ResponseEntity<Resource> streamTrack(@PathVariable Long id) {
        Track track = trackRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Piste non trouvée !"));

        try {
            Path file = rootLocation.resolve(track.getFilename());
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                // On peut être plus précis sur le type de contenu si on le connaît
                String contentType = "audio/mpeg"; // Pour les MP3 par exemple

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