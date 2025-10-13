package com.docker.service;

import com.docker.entity.Concert;
import com.docker.entity.Role;
import com.docker.entity.User;
import com.docker.repository.ConcertRepository;
import com.docker.repository.RoleRepository;
import com.docker.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ConcertRepository concertRepository;
    private final PasswordEncoder passwordEncoder;
    private BadgeService badgeService; // Injection tardive pour éviter les dépendances circulaires

    private static final String PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";



    public UserService(UserRepository userRepository, RoleRepository roleRepository, ConcertRepository concertRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.concertRepository = concertRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Injection du BadgeService après la construction pour éviter les dépendances circulaires
    @org.springframework.beans.factory.annotation.Autowired(required = false)
    public void setBadgeService(BadgeService badgeService) {
        this.badgeService = badgeService;
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional
    public void saveUser(User user) {
        if (!user.getPassword().matches(PASSWORD_PATTERN)) {
             throw new IllegalArgumentException("Le mot de passe ne respecte pas les critères de sécurité.");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        Role userRole = roleRepository.findByName("ROLE_USER");
        if (userRole == null) {
            throw new RuntimeException("Error: Role 'ROLE_USER' is not found.");
        }
        
        user.setRoles(new HashSet<>(Collections.singletonList(userRole)));
        userRepository.save(user);
    }
    
    @Transactional
    public void updateUserFromAdmin(Long userId, String username, String email, Set<Role> roles) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + userId));
        
        existingUser.setUsername(username);
        existingUser.setEmail(email);
        existingUser.setRoles(roles);
        
        userRepository.save(existingUser);
    }
    
    @Transactional
    public void updateUserProfile(String currentUsername, String newUsername, String newEmail) {
        User currentUser = userRepository.findByUsername(currentUsername);
        if (currentUser == null) {
            throw new IllegalArgumentException("Utilisateur non trouvé.");
        }

        User userByNewUsername = userRepository.findByUsername(newUsername);
        if (userByNewUsername != null && !userByNewUsername.getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("Ce nom d'utilisateur est déjà pris.");
        }

        User userByNewEmail = userRepository.findByEmail(newEmail);
        if (userByNewEmail != null && !userByNewEmail.getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("Cet email est déjà utilisé.");
        }

        currentUser.setUsername(newUsername);
        currentUser.setEmail(newEmail);
        userRepository.save(currentUser);
    }

    @Transactional
    public void changeUserPassword(String username, String oldPassword, String newPassword, String confirmPassword) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new IllegalArgumentException("Utilisateur non trouvé.");
        }

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("L'ancien mot de passe est incorrect.");
        }

        if (!newPassword.equals(confirmPassword)) {
            throw new IllegalArgumentException("Le nouveau mot de passe et sa confirmation ne correspondent pas.");
        }

        if (!newPassword.matches(PASSWORD_PATTERN)) {
            String errorMessage = "Le mot de passe doit contenir au moins :\n"
                                + "- 8 caractères\n"
                                + "- Une lettre majuscule\n"
                                + "- Une lettre minuscule\n"
                                + "- Un chiffre\n"
                                + "- Un caractère spécial (@, $, !, %, *, ?, &)";
            throw new IllegalArgumentException(errorMessage);
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }
    
    public Optional<User> findUserById(Long id) {
        return userRepository.findById(id);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
    
    public Page<User> searchUsers(String keyword, Pageable pageable) {
        if (keyword != null && !keyword.isEmpty()) {
            return userRepository.searchUsers(keyword, pageable);
        }
        return userRepository.findAll(pageable);
    }
    
    public long countUsers() {
        return userRepository.count();
    }

    @Transactional
    public void addFavoriteConcert(String username, Long concertId) {
        User user = userRepository.findByUsername(username);
        Concert concert = concertRepository.findById(concertId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid concert Id:" + concertId));
        user.getFavoriteConcerts().add(concert);
        userRepository.save(user);

        // Vérifier et débloquer les badges après l'ajout d'un concert favori
        if (badgeService != null) {
            badgeService.checkAndUnlockBadges(user);
        }
    }

    @Transactional
    public void removeFavoriteConcert(String username, Long concertId) {
        User user = userRepository.findByUsername(username);
        Concert concert = concertRepository.findById(concertId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid concert Id:" + concertId));
        user.getFavoriteConcerts().remove(concert);
        userRepository.save(user);
    } 
 // ============================================
    // GESTION DE LA PHOTO DE PROFIL ET BIO
    // ============================================
    
    private final Path avatarLocation = Paths.get("uploaded-avatars");
    private static final List<String> ALLOWED_AVATAR_TYPES = List.of("image/jpeg", "image/png", "image/gif", "image/webp");
    
    @Transactional
    public void updateProfileDetails(String currentUsername, String firstName, String lastName, String bio) {
        User user = userRepository.findByUsername(currentUsername);
        if (user == null) {
            throw new IllegalArgumentException("Utilisateur non trouvé.");
        }
        
        // Validation de la longueur
        if (firstName != null && firstName.length() > 50) {
            throw new IllegalArgumentException("Le prénom ne peut pas dépasser 50 caractères.");
        }
        if (lastName != null && lastName.length() > 50) {
            throw new IllegalArgumentException("Le nom ne peut pas dépasser 50 caractères.");
        }
        if (bio != null && bio.length() > 500) {
            throw new IllegalArgumentException("La biographie ne peut pas dépasser 500 caractères.");
        }
        
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setBio(bio);
        userRepository.save(user);
    }
    
    @Transactional
    public void updateAvatar(String username, MultipartFile file) throws IOException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new IllegalArgumentException("Utilisateur non trouvé.");
        }
        
        // Validation du fichier
        if (file.isEmpty()) {
            throw new IOException("Impossible de sauvegarder un fichier vide.");
        }
        
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_AVATAR_TYPES.contains(contentType)) {
            throw new IOException("Type de fichier non autorisé. Seuls les fichiers JPEG, PNG, GIF et WebP sont acceptés.");
        }
        
        // Vérification de la taille (max 5MB)
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new IOException("Le fichier est trop volumineux. Taille maximale : 5MB.");
        }
        
        // Créer le dossier s'il n'existe pas
        try {
            Files.createDirectories(avatarLocation);
        } catch (IOException e) {
            throw new RuntimeException("Impossible de créer le dossier de stockage des avatars", e);
        }
        
        // Supprimer l'ancien avatar si existant
        if (user.getAvatarFilename() != null) {
            Path oldFile = avatarLocation.resolve(user.getAvatarFilename());
            Files.deleteIfExists(oldFile);
        }
        
        // Créer un nom de fichier unique
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String uniqueFilename = UUID.randomUUID().toString() + extension;
        
        // Sauvegarder le nouveau fichier
        Path destinationFile = avatarLocation.resolve(uniqueFilename).normalize().toAbsolutePath();
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
        }
        
        // Mettre à jour la base de données
        user.setAvatarFilename(uniqueFilename);
        userRepository.save(user);
    }
    
    @Transactional
    public void deleteAvatar(String username) throws IOException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new IllegalArgumentException("Utilisateur non trouvé.");
        }

        if (user.getAvatarFilename() != null) {
            Path fileToDelete = avatarLocation.resolve(user.getAvatarFilename());
            Files.deleteIfExists(fileToDelete);
            user.setAvatarFilename(null);
            userRepository.save(user);
        }
    }

    /**
     * Vérifie si le mot de passe fourni correspond au mot de passe de l'utilisateur
     * Utilisé pour la confirmation avant suppression de compte (RGPD)
     *
     * @param user Utilisateur
     * @param rawPassword Mot de passe en clair à vérifier
     * @return true si le mot de passe correspond, false sinon
     */
    public boolean verifyPassword(User user, String rawPassword) {
        return passwordEncoder.matches(rawPassword, user.getPassword());
    }
}