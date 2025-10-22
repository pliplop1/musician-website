package com.docker.service;

import com.docker.entity.Track;
import com.docker.entity.TrackType;
import com.docker.repository.TrackRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
public class TrackService {
	private static final List<String> ALLOWED_AUDIO_TYPES = List.of("audio/mpeg", "audio/wav", "audio/ogg");
	private final TrackRepository trackRepository;
	private final Path rootLocation;

	public TrackService(TrackRepository trackRepository,
			@Value("${musician.upload.music-dir}") String musicDir) {
		this.trackRepository = trackRepository;
		this.rootLocation = Paths.get(musicDir);
		// Crée le dossier s'il n'existe pas au démarrage
		try {
			Files.createDirectories(rootLocation);
		} catch (IOException e) {
			throw new RuntimeException("Ne peut pas initialiser le dossier de stockage de la musique", e);
		}
	}

	/**
	 * Récupère toutes les pistes et, pour les fichiers uploadés, lit le fichier
	 * audio et l'encode en Base64 pour l'injecter dans la page.
	 */
	public List<Track> getAllTracks() {
        return trackRepository.findAll();
    }

	/**
	 * Récupère une piste par son ID
	 */
	public Track findById(Long id) {
		return trackRepository.findById(id).orElse(null);
	}

	/**
	 * Sauvegarde une piste
	 */
	public Track save(Track track) {
		return trackRepository.save(track);
	}

	// Méthode pour sauvegarder une piste de type EMBED
	public void saveEmbedTrack(String title, String embedCode) {
		Track track = new Track();
		track.setTitle(title);
		track.setTrackType(TrackType.EMBED);
		track.setEmbedCode(embedCode);
		trackRepository.save(track);
	}

	// Méthode pour sauvegarder une piste de type UPLOADED_FILE
	public void saveUploadedTrack(String title, MultipartFile file) throws IOException {
		if (file.isEmpty()) {
			throw new IOException("Impossible de sauvegarder un fichier vide.");
		}

		String contentType = file.getContentType();
		if (contentType == null || !ALLOWED_AUDIO_TYPES.contains(contentType)) {
			throw new IOException("Type de fichier non autorisé. Seuls les fichiers MP3, WAV et OGG sont acceptés.");
		}

		String originalFilename = file.getOriginalFilename();
		String uniqueFilename = UUID.randomUUID().toString() + "_" + originalFilename;

		Path destinationFile = this.rootLocation.resolve(Paths.get(uniqueFilename)).normalize().toAbsolutePath();

		try (InputStream inputStream = file.getInputStream()) {
			Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
		}

		Track track = new Track();
		track.setTitle(title);
		track.setTrackType(TrackType.UPLOADED_FILE);
		track.setFilename(uniqueFilename);
		trackRepository.save(track);
	}

	// Méthode pour supprimer une piste
	public void deleteTrack(Long id) throws IOException {
		Track track = trackRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Piste non trouvée avec l'id : " + id));

		// Si c'est un fichier uploadé, il faut aussi le supprimer du disque dur
		if (track.getTrackType() == TrackType.UPLOADED_FILE && track.getFilename() != null) {
			Path fileToDelete = rootLocation.resolve(track.getFilename());
			Files.deleteIfExists(fileToDelete);
		}

		trackRepository.deleteById(id);
	}
}