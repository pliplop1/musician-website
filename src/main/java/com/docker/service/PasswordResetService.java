package com.docker.service;

import com.docker.entity.PasswordResetToken;
import com.docker.entity.User;
import com.docker.repository.PasswordResetTokenRepository;
import com.docker.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * Service pour gérer la réinitialisation de mot de passe
 * Utilise un système de token sécurisé envoyé par email
 */
@Service
@Transactional
public class PasswordResetService {

    private static final Logger logger = LoggerFactory.getLogger(PasswordResetService.class);
    private static final int TOKEN_EXPIRATION_MINUTES = 60; // 1 heure

    private final PasswordResetTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    public PasswordResetService(PasswordResetTokenRepository tokenRepository,
                                UserRepository userRepository,
                                PasswordEncoder passwordEncoder,
                                EmailService emailService) {
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    /**
     * Crée un token de réinitialisation et envoie l'email
     *
     * @param email Email de l'utilisateur
     * @return true si l'email a été envoyé
     */
    public boolean createPasswordResetTokenForUser(String email) {
        User user = userRepository.findByEmail(email);

        if (user == null) {
            // Pour des raisons de sécurité, on ne révèle pas si l'email existe ou non
            logger.warn("Tentative de réinitialisation pour un email inexistant: {}", email);
            return true; // On retourne true quand même pour ne pas révéler l'existence du compte
        }

        // Invalider tous les anciens tokens de cet utilisateur
        tokenRepository.deleteByUser(user);

        // Générer un nouveau token sécurisé
        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken(token, user, TOKEN_EXPIRATION_MINUTES);
        tokenRepository.save(resetToken);

        // Envoyer l'email avec le lien de réinitialisation
        String resetUrl = baseUrl + "/reset-password?token=" + token;
        emailService.sendPasswordResetEmail(user.getEmail(), user.getUsername(), resetUrl);

        logger.info("Token de réinitialisation créé pour l'utilisateur: {}", user.getUsername());
        return true;
    }

    /**
     * Valide un token de réinitialisation
     *
     * @param token Le token à valider
     * @return Optional contenant le token s'il est valide
     */
    @Transactional(readOnly = true)
    public Optional<PasswordResetToken> validatePasswordResetToken(String token) {
        Optional<PasswordResetToken> tokenOpt = tokenRepository.findByToken(token);

        if (tokenOpt.isEmpty()) {
            logger.warn("Token de réinitialisation introuvable: {}", token);
            return Optional.empty();
        }

        PasswordResetToken resetToken = tokenOpt.get();

        if (resetToken.isUsed()) {
            logger.warn("Token de réinitialisation déjà utilisé: {}", token);
            return Optional.empty();
        }

        if (resetToken.isExpired()) {
            logger.warn("Token de réinitialisation expiré: {}", token);
            return Optional.empty();
        }

        return Optional.of(resetToken);
    }

    /**
     * Change le mot de passe en utilisant un token valide
     *
     * @param token Le token de réinitialisation
     * @param newPassword Le nouveau mot de passe
     * @return true si le changement a réussi
     */
    public boolean resetPassword(String token, String newPassword) {
        Optional<PasswordResetToken> tokenOpt = validatePasswordResetToken(token);

        if (tokenOpt.isEmpty()) {
            return false;
        }

        PasswordResetToken resetToken = tokenOpt.get();
        User user = resetToken.getUser();

        // Changer le mot de passe
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Marquer le token comme utilisé
        resetToken.setUsed(true);
        tokenRepository.save(resetToken);

        logger.info("Mot de passe réinitialisé avec succès pour l'utilisateur: {}", user.getUsername());
        return true;
    }

    /**
     * Nettoie les tokens expirés (à appeler périodiquement)
     */
    @Transactional
    public void cleanupExpiredTokens() {
        tokenRepository.deleteByExpiryDateBefore(LocalDateTime.now());
        logger.info("Nettoyage des tokens de réinitialisation expirés effectué");
    }
}
