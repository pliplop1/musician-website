package com.docker.service;

import com.docker.entity.User;
import com.docker.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service pour l'export des données personnelles (RGPD - Article 20)
 * Permet aux utilisateurs d'exercer leur droit à la portabilité des données
 */
@Service
@Transactional(readOnly = true)
public class DataExportService {

    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    public DataExportService(UserRepository userRepository) {
        this.userRepository = userRepository;

        // Configuration de l'ObjectMapper pour générer un JSON lisible
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    /**
     * Exporte toutes les données personnelles d'un utilisateur au format JSON
     * Conforme au RGPD (Article 20 - Droit à la portabilité)
     *
     * @param userId ID de l'utilisateur dont on souhaite exporter les données
     * @return JSON contenant toutes les données personnelles
     * @throws IllegalArgumentException si l'utilisateur n'existe pas
     */
    public String exportUserData(Long userId) throws Exception {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable avec l'ID : " + userId));

        Map<String, Object> exportData = new HashMap<>();

        // Métadonnées de l'export
        exportData.put("export_metadata", Map.of(
            "export_date", LocalDateTime.now().toString(),
            "export_format", "JSON",
            "rgpd_article", "Article 20 - Droit à la portabilité",
            "data_controller", "Duo Black & White SARL"
        ));

        // Informations de compte
        Map<String, Object> accountInfo = new HashMap<>();
        accountInfo.put("user_id", user.getId());
        accountInfo.put("username", user.getUsername());
        accountInfo.put("email", user.getEmail());
        accountInfo.put("first_name", user.getFirstName());
        accountInfo.put("last_name", user.getLastName());
        accountInfo.put("bio", user.getBio());
        accountInfo.put("avatar_filename", user.getAvatarFilename());
        accountInfo.put("created_at", user.getCreatedAt() != null ? user.getCreatedAt().toString() : null);
        exportData.put("account", accountInfo);

        // Rôles
        if (user.getRoles() != null) {
            exportData.put("roles", user.getRoles().stream()
                .map(role -> role.getName())
                .collect(Collectors.toList()));
        }

        // Badges (si applicable)
        if (user.getBadges() != null) {
            exportData.put("badges", user.getBadges().stream()
                .map(userBadge -> Map.of(
                    "id", userBadge.getId(),
                    "badge_name", userBadge.getBadge().getName(),
                    "badge_description", userBadge.getBadge().getDescription() != null ? userBadge.getBadge().getDescription() : "",
                    "unlocked_at", userBadge.getUnlockedAt().toString()
                ))
                .collect(Collectors.toList()));
        }

        // Commentaires (si l'entité existe)
        // Note : À décommenter et adapter si vous avez une entité Comment
        /*
        if (user.getComments() != null) {
            exportData.put("comments", user.getComments().stream()
                .map(comment -> Map.of(
                    "id", comment.getId(),
                    "content", comment.getContent(),
                    "created_at", comment.getCreatedAt().toString(),
                    "article_id", comment.getArticle() != null ? comment.getArticle().getId() : null
                ))
                .collect(Collectors.toList()));
        }
        */

        // Messages de contact (si l'entité existe)
        // Note : À décommenter et adapter si vous avez une entité ContactMessage
        /*
        if (user.getMessages() != null) {
            exportData.put("messages", user.getMessages().stream()
                .map(message -> Map.of(
                    "id", message.getId(),
                    "subject", message.getSubject(),
                    "content", message.getContent(),
                    "sent_at", message.getSentAt().toString()
                ))
                .collect(Collectors.toList()));
        }
        */

        // Informations de sécurité (anonymisées)
        Map<String, Object> securityInfo = new HashMap<>();
        securityInfo.put("password_hashed", true);
        securityInfo.put("password_algorithm", "BCrypt");
        securityInfo.put("note", "Le mot de passe n'est jamais exporté en clair pour des raisons de sécurité");
        exportData.put("security", securityInfo);

        // Informations RGPD
        Map<String, Object> rgpdInfo = new HashMap<>();
        rgpdInfo.put("data_retention_period", "3 ans après dernière connexion");
        rgpdInfo.put("right_to_erasure", "Vous pouvez demander la suppression de vos données à tout moment");
        rgpdInfo.put("right_to_rectification", "Vous pouvez modifier vos données depuis votre profil");
        rgpdInfo.put("contact_dpo", "privacy@duoblackandwhite.fr");
        exportData.put("rgpd_information", rgpdInfo);

        // Convertir en JSON
        return objectMapper.writeValueAsString(exportData);
    }

    /**
     * Exporte les données d'un utilisateur identifié par son username
     *
     * @param username Nom d'utilisateur
     * @return JSON contenant toutes les données personnelles
     * @throws Exception si l'utilisateur n'existe pas ou erreur de sérialisation
     */
    public String exportUserDataByUsername(String username) throws Exception {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new IllegalArgumentException("Utilisateur introuvable : " + username);
        }
        return exportUserData(user.getId());
    }

    /**
     * Génère un nom de fichier pour l'export
     * Format : data-export-{username}-{timestamp}.json
     *
     * @param username Nom d'utilisateur
     * @return Nom du fichier d'export
     */
    public String generateExportFilename(String username) {
        String timestamp = LocalDateTime.now().toString()
            .replace(":", "-")
            .replace(".", "-")
            .substring(0, 19);  // Format: 2025-10-13T14-30-45

        return String.format("data-export-%s-%s.json", username, timestamp);
    }
}
