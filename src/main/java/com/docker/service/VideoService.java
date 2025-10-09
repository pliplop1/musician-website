package com.docker.service;

import com.docker.entity.Video;
import com.docker.entity.VideoType;
import com.docker.repository.VideoRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
public class VideoService {
	private static final List<String> ALLOWED_VIDEO_TYPES = List.of("video/mp4", "video/webm", "video/ogg");
	private final VideoRepository videoRepository;
	private final Path rootLocation = Paths.get("uploaded-videos");

	public VideoService(VideoRepository videoRepository) {
		this.videoRepository = videoRepository;
		// Crée le dossier s'il n'existe pas au démarrage
		try {
			Files.createDirectories(rootLocation);
		} catch (IOException e) {
			throw new RuntimeException("Ne peut pas initialiser le dossier de stockage", e);
		}
	}

	/**
	 * Récupère toutes les vidéos
	 */
	public List<Video> getAllVideos() {
        return videoRepository.findAll();
    }

	// Méthode pour sauvegarder une vidéo de type EMBED
	public void saveEmbedVideo(String title, String embedCode) {
		Video video = new Video();
		video.setTitle(title);
		video.setVideoType(VideoType.EMBED);
		video.setEmbedCode(embedCode);
		videoRepository.save(video);
	}

	// Méthode pour sauvegarder une vidéo de type UPLOADED_FILE
	public void saveUploadedVideo(String title, MultipartFile file) throws IOException {
		if (file.isEmpty()) {
			throw new IOException("Impossible de sauvegarder un fichier vide.");
		}

		String contentType = file.getContentType();
		if (contentType == null || !ALLOWED_VIDEO_TYPES.contains(contentType)) {
			throw new IOException("Type de fichier non autorisé. Seuls les fichiers MP4, WebM et OGG sont acceptés.");
		}

		String originalFilename = file.getOriginalFilename();
		String uniqueFilename = UUID.randomUUID().toString() + "_" + originalFilename;

		Path destinationFile = this.rootLocation.resolve(Paths.get(uniqueFilename)).normalize().toAbsolutePath();

		try (InputStream inputStream = file.getInputStream()) {
			Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
		}

		Video video = new Video();
		video.setTitle(title);
		video.setVideoType(VideoType.UPLOADED_FILE);
		video.setFilename(uniqueFilename);
		videoRepository.save(video);
	}

	// Méthode pour supprimer une vidéo
	public void deleteVideo(Long id) throws IOException {
		Video video = videoRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Vidéo non trouvée avec l'id : " + id));

		// Si c'est un fichier uploadé, il faut aussi le supprimer du disque dur
		if (video.getVideoType() == VideoType.UPLOADED_FILE && video.getFilename() != null) {
			Path fileToDelete = rootLocation.resolve(video.getFilename());
			Files.deleteIfExists(fileToDelete);
		}

		videoRepository.deleteById(id);
	}
}
