// /src/main/java/com/docker/service/PhotoService.java

package com.docker.service;

import com.docker.entity.Photo;
import com.docker.repository.PhotoRepository;
import org.springframework.beans.factory.annotation.Value;
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
public class PhotoService {

	private static final List<String> ALLOWED_IMAGE_TYPES = List.of("image/jpeg", "image/png", "image/gif",
			"image/webp");
	private static final List<String> ALLOWED_CATEGORIES = List.of("Concert", "Studio", "Backstage", "Promo", "Portrait");
	private static final int MAX_TITLE_LENGTH = 255;
	private static final int MAX_DESCRIPTION_LENGTH = 2000;
	private static final int MAX_TAGS_LENGTH = 500;
	private final PhotoRepository photoRepository;
	private final Path rootLocation;

	public PhotoService(PhotoRepository photoRepository,
			@Value("${musician.upload.photos-dir}") String photosDir) {
		this.photoRepository = photoRepository;
		this.rootLocation = Paths.get(photosDir);
		// Créer le dossier s'il n'existe pas au démarrage
		try {
			Files.createDirectories(rootLocation);
		} catch (IOException e) {
			throw new RuntimeException("Ne peut pas initialiser le dossier de stockage des photos", e);
		}
	}

	public List<Photo> getAllPhotos() {
		return photoRepository.findAllByOrderByDisplayOrderAsc();
	}

	/**
	 * Récupère une photo par son ID
	 */
	public Photo findById(Long id) {
		return photoRepository.findById(id).orElse(null);
	}

	/**
	 * Sauvegarde une photo
	 */
	public Photo save(Photo photo) {
		return photoRepository.save(photo);
	}

	public void savePhoto(MultipartFile file) throws IOException {
		savePhoto(file, null, null, null, null);
	}

	public void savePhoto(MultipartFile file, String title, String description, String tags, String category) throws IOException {
		if (file.isEmpty()) {
			throw new IOException("Impossible de sauvegarder un fichier vide.");
		}

		String contentType = file.getContentType();
		if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType)) {
			throw new IOException(
					"Type de fichier non autorisé. Seuls les fichiers JPEG, PNG, GIF et WebP sont acceptés.");
		}

		validateMetadata(title, description, tags, category);

		String originalFilename = file.getOriginalFilename();
		String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
		String uniqueFilename = UUID.randomUUID().toString() + extension;

		Path destinationFile = this.rootLocation.resolve(Paths.get(uniqueFilename)).normalize().toAbsolutePath();

		try (InputStream inputStream = file.getInputStream()) {
			Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
		}

		Photo photo = new Photo();
		photo.setFilename(uniqueFilename);
		photo.setOriginalFilename(originalFilename);
		photo.setFileSize(file.getSize());
		photo.setMimeType(contentType);
		applyMetadata(photo, title, description, tags, category);
		photoRepository.save(photo);
	}

	public void updatePhotoMetadata(Long id, String title, String description, String tags, String category) {
		Photo photo = photoRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Photo non trouvée avec l'id : " + id));
		validateMetadata(title, description, tags, category);
		applyMetadata(photo, title, description, tags, category);
		photoRepository.save(photo);
	}

	private void validateMetadata(String title, String description, String tags, String category) {
		if (title != null && title.trim().length() > MAX_TITLE_LENGTH) {
			throw new IllegalArgumentException("Le titre ne peut pas dépasser " + MAX_TITLE_LENGTH + " caractères.");
		}
		if (description != null && description.trim().length() > MAX_DESCRIPTION_LENGTH) {
			throw new IllegalArgumentException("La description ne peut pas dépasser " + MAX_DESCRIPTION_LENGTH + " caractères.");
		}
		if (tags != null && tags.trim().length() > MAX_TAGS_LENGTH) {
			throw new IllegalArgumentException("Les tags ne peuvent pas dépasser " + MAX_TAGS_LENGTH + " caractères.");
		}
		if (category != null && !category.isBlank() && !ALLOWED_CATEGORIES.contains(category.trim())) {
			throw new IllegalArgumentException("Catégorie non autorisée.");
		}
	}

	private void applyMetadata(Photo photo, String title, String description, String tags, String category) {
		photo.setTitle(title != null && !title.isBlank() ? title.trim() : null);
		photo.setDescription(description != null && !description.isBlank() ? description.trim() : null);
		photo.setTags(tags != null && !tags.isBlank() ? tags.trim() : null);
		photo.setCategory(category != null && !category.isBlank() ? category.trim() : null);
	}

	public void deletePhoto(Long id) throws IOException {
		Photo photo = photoRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Photo non trouvée avec l'id : " + id));

		// Supprimer le fichier du disque
		Path fileToDelete = rootLocation.resolve(photo.getFilename());
		Files.deleteIfExists(fileToDelete);

		// Supprimer l'entrée de la base de données
		photoRepository.deleteById(id);
	}
}