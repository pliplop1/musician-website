package com.docker.repository;

import com.docker.entity.LoginAttempt;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository pour gérer les tentatives de connexion
 * Utilisé pour la protection anti-brute force
 */
@Repository
public interface LoginAttemptRepository extends JpaRepository<LoginAttempt, Long> {

    /**
     * Compte le nombre d'échecs de connexion pour un username depuis une date donnée
     *
     * @param username Nom d'utilisateur
     * @param since Date à partir de laquelle compter
     * @return Nombre d'échecs
     */
    @Query("SELECT COUNT(la) FROM LoginAttempt la WHERE la.username = :username AND la.success = false AND la.attemptTime >= :since")
    long countFailedAttemptsByUsernameSince(@Param("username") String username, @Param("since") LocalDateTime since);

    /**
     * Compte le nombre d'échecs de connexion depuis une IP donnée depuis une date
     *
     * @param ipAddress Adresse IP
     * @param since Date à partir de laquelle compter
     * @return Nombre d'échecs
     */
    @Query("SELECT COUNT(la) FROM LoginAttempt la WHERE la.ipAddress = :ipAddress AND la.success = false AND la.attemptTime >= :since")
    long countFailedAttemptsByIpSince(@Param("ipAddress") String ipAddress, @Param("since") LocalDateTime since);

    /**
     * Récupère toutes les tentatives pour un utilisateur depuis une date
     *
     * @param username Nom d'utilisateur
     * @param since Date à partir de laquelle récupérer
     * @return Liste des tentatives
     */
    List<LoginAttempt> findByUsernameAndAttemptTimeGreaterThanEqualOrderByAttemptTimeDesc(String username, LocalDateTime since);

    /**
     * Récupère toutes les tentatives depuis une IP depuis une date
     *
     * @param ipAddress Adresse IP
     * @param since Date à partir de laquelle récupérer
     * @return Liste des tentatives
     */
    List<LoginAttempt> findByIpAddressAndAttemptTimeGreaterThanEqualOrderByAttemptTimeDesc(String ipAddress, LocalDateTime since);

    /**
     * Récupère les dernières tentatives réussies pour un utilisateur
     *
     * @param username Nom d'utilisateur
     * @param limit Nombre maximum de résultats
     * @return Liste des tentatives réussies
     */
    @Query("SELECT la FROM LoginAttempt la WHERE la.username = :username AND la.success = true ORDER BY la.attemptTime DESC")
    List<LoginAttempt> findLastSuccessfulAttempts(@Param("username") String username);

    /**
     * Récupère la dernière tentative réussie pour un utilisateur
     *
     * @param username Nom d'utilisateur
     * @return Dernière tentative réussie ou null
     */
    LoginAttempt findFirstByUsernameAndSuccessTrueOrderByAttemptTimeDesc(String username);

    /**
     * Supprime les tentatives plus anciennes qu'une date donnée (nettoyage)
     *
     * @param before Date avant laquelle supprimer
     */
    void deleteByAttemptTimeBefore(LocalDateTime before);

    /**
     * Supprime toutes les tentatives échouées pour un utilisateur
     * Utilisé pour réinitialiser le compteur après une connexion réussie
     *
     * @param username Nom d'utilisateur
     */
    void deleteByUsernameAndSuccessFalse(String username);

    void deleteByUsername(String username);

    // ========================================
    // MÉTHODES POUR LE DASHBOARD ADMIN
    // ========================================

    /**
     * Compte le nombre total de tentatives par statut
     *
     * @param success Statut (true pour succès, false pour échecs)
     * @return Nombre de tentatives
     */
    long countBySuccess(boolean success);

    /**
     * Récupère les 50 dernières tentatives (toutes)
     *
     * @return Liste des 50 dernières tentatives
     */
    List<LoginAttempt> findTop50ByOrderByAttemptTimeDesc();

    /**
     * Récupère toutes les tentatives depuis une date donnée
     *
     * @param since Date à partir de laquelle récupérer
     * @return Liste des tentatives
     */
    List<LoginAttempt> findByAttemptTimeGreaterThanEqualOrderByAttemptTimeDesc(LocalDateTime since);

    /**
     * Récupère la liste des usernames distincts avec échecs récents
     *
     * @param since Date à partir de laquelle chercher
     * @return Liste des usernames
     */
    @Query("SELECT DISTINCT la.username FROM LoginAttempt la WHERE la.success = false AND la.attemptTime >= :since")
    List<String> findDistinctUsernamesBySuccessFalseAndAttemptTimeGreaterThanEqual(@Param("since") LocalDateTime since);

    /**
     * Récupère l'historique paginé des tentatives pour un utilisateur
     *
     * @param username Nom d'utilisateur
     * @param since Date à partir de laquelle récupérer
     * @param pageable Paramètres de pagination et tri
     * @return Page de tentatives
     */
    Page<LoginAttempt> findByUsernameAndAttemptTimeGreaterThanEqual(String username, LocalDateTime since, Pageable pageable);
}
