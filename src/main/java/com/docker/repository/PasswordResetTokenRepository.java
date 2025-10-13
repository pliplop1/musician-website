package com.docker.repository;

import com.docker.entity.PasswordResetToken;
import com.docker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Repository pour gérer les tokens de réinitialisation de mot de passe
 */
@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    /**
     * Trouve un token par sa valeur
     */
    Optional<PasswordResetToken> findByToken(String token);

    /**
     * Trouve un token valide (non utilisé et non expiré) pour un token donné
     */
    Optional<PasswordResetToken> findByTokenAndUsedFalseAndExpiryDateAfter(String token, LocalDateTime now);

    /**
     * Trouve tous les tokens d'un utilisateur
     */
    java.util.List<PasswordResetToken> findByUser(User user);

    /**
     * Supprime tous les tokens expirés
     */
    void deleteByExpiryDateBefore(LocalDateTime now);

    /**
     * Supprime tous les tokens d'un utilisateur
     */
    void deleteByUser(User user);
}
