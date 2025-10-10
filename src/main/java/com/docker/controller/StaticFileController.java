package com.docker.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Contrôleur pour servir les fichiers statiques uploadés
 * (MP3, photos, vidéos, avatars) sans authentification
 */
@RestController
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:8106"})
public class StaticFileController {

    private static final Logger logger = LoggerFactory.getLogger(StaticFileController.class);
    private final String baseDir = System.getProperty("user.dir");

    /**
     * Servir les fichiers MP3
     */
    @GetMapping("/uploaded-music/{filename:.+}")
    public ResponseEntity<Resource> serveMusic(@PathVariable String filename) {
        return serveFile("uploaded-music", filename, "audio/mpeg");
    }

    /**
     * Servir les photos
     */
    @GetMapping("/uploaded-photos/{filename:.+}")
    public ResponseEntity<Resource> servePhoto(@PathVariable String filename) {
        return serveFile("uploaded-photos", filename, "image/jpeg");
    }

    /**
     * Servir les vidéos
     */
    @GetMapping("/uploaded-videos/{filename:.+}")
    public ResponseEntity<Resource> serveVideo(@PathVariable String filename) {
        return serveFile("uploaded-videos", filename, "video/mp4");
    }

    /**
     * Servir les avatars
     */
    @GetMapping("/uploaded-avatars/{filename:.+}")
    public ResponseEntity<Resource> serveAvatar(@PathVariable String filename) {
        return serveFile("uploaded-avatars", filename, "image/png");
    }

    /**
     * Méthode générique pour servir un fichier
     */
    private ResponseEntity<Resource> serveFile(String directory, String filename, String defaultContentType) {
        try {
            // Construire le chemin du fichier
            Path filePath = Paths.get(baseDir, directory, filename);
            File file = filePath.toFile();

            logger.info("📂 Tentative de lecture du fichier: {}", filePath);

            // Vérifier que le fichier existe
            if (!file.exists() || !file.isFile()) {
                logger.warn("❌ Fichier non trouvé: {}", filePath);
                return ResponseEntity.notFound().build();
            }

            // Vérifier que le fichier est bien dans le répertoire autorisé (sécurité)
            String canonicalPath = file.getCanonicalPath();
            String allowedPath = Paths.get(baseDir, directory).toFile().getCanonicalPath();
            if (!canonicalPath.startsWith(allowedPath)) {
                logger.error("🚨 Tentative d'accès non autorisé: {}", canonicalPath);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            // Créer la ressource
            Resource resource = new FileSystemResource(file);

            // Déterminer le Content-Type
            String contentType = defaultContentType;
            try {
                String detectedType = Files.probeContentType(filePath);
                if (detectedType != null) {
                    contentType = detectedType;
                }
            } catch (Exception e) {
                logger.warn("Impossible de détecter le type MIME, utilisation du type par défaut: {}", defaultContentType);
            }

            logger.info("✅ Fichier servi avec succès: {} ({})", filename, contentType);

            // Retourner le fichier avec les headers appropriés
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                    .header(HttpHeaders.CACHE_CONTROL, "max-age=3600")
                    .body(resource);

        } catch (Exception e) {
            logger.error("❌ Erreur lors de la lecture du fichier {}/{}: {}", directory, filename, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
