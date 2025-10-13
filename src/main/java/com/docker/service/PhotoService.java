// /src/main/java/com/docker/service/PhotoService.java

package com.docker.service;

import com.docker.entity.Photo;
import com.docker.repository.PhotoRepository;
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
	private final PhotoRepository photoRepository;
	private final Path rootLocation = Paths.get("uploaded-photos");

	public PhotoService(PhotoRepository photoRepository) {
		this.photoRepository = photoRepository;
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
		if (file.isEmpty()) {
			throw new IOException("Impossible de sauvegarder un fichier vide.");
		}

		String contentType = file.getContentType();
		if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType)) {
			throw new IOException(
					"Type de fichier non autorisé. Seuls les fichiers JPEG, PNG, GIF et WebP sont acceptés.");
		}
		// Crée un nom de fichier unique pour éviter les doublons
		String originalFilename = file.getOriginalFilename();
		String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
		String uniqueFilename = UUID.randomUUID().toString() + extension;

		Path destinationFile = this.rootLocation.resolve(Paths.get(uniqueFilename)).normalize().toAbsolutePath();

		try (InputStream inputStream = file.getInputStream()) {
			Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
		}

		Photo photo = new Photo();
		photo.setFilename(uniqueFilename);
		photoRepository.save(photo);
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