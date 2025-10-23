package com.docker.config;

import com.docker.entity.Track;
import com.docker.repository.TrackRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * Initialiseur qui associe automatiquement les fichiers MP3 existants
 * dans uploaded-music/ aux tracks qui n'ont pas de fichier
 */
@Component
@Profile({"dev", "prod"})
@Order(10) // Après les autres initialiseurs
public class TrackMusicFileInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(TrackMusicFileInitializer.class);
    private final TrackRepository trackRepository;

    public TrackMusicFileInitializer(TrackRepository trackRepository) {
        this.trackRepository = trackRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        logger.info("🎵 Association automatique des fichiers MP3 aux tracks...");

        // Dossier contenant les fichiers MP3
        File musicDir = new File("uploaded-music");

        if (!musicDir.exists() || !musicDir.isDirectory()) {
            logger.warn("⚠️ Le dossier 'uploaded-music' n'existe pas");
            return;
        }

        // Lister les fichiers MP3
        File[] mp3Files = musicDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".mp3"));

        if (mp3Files == null || mp3Files.length == 0) {
            logger.info("ℹ️ Aucun fichier MP3 trouvé dans 'uploaded-music'");
            return;
        }

        logger.info("📂 {} fichier(s) MP3 trouvé(s)", mp3Files.length);

        // Récupérer les tracks sans fichier
        List<Track> tracksWithoutFile = trackRepository.findAll().stream()
            .filter(track -> track.getFilename() == null || track.getFilename().isEmpty())
            .toList();

        if (tracksWithoutFile.isEmpty()) {
            logger.info("✅ Tous les tracks ont déjà des fichiers associés");
            return;
        }

        logger.info("🔗 {} track(s) sans fichier trouvé(s)", tracksWithoutFile.size());

        // Associer les fichiers MP3 aux tracks
        int count = 0;
        for (int i = 0; i < Math.min(tracksWithoutFile.size(), mp3Files.length); i++) {
            Track track = tracksWithoutFile.get(i);
            String filename = mp3Files[i].getName();

            track.setFilename(filename);
            trackRepository.save(track);

            logger.info("  ✓ Track '{}' → {}", track.getTitle(), filename);
            count++;
        }

        logger.info("✅ {} fichier(s) MP3 associé(s) avec succès", count);
    }
}
