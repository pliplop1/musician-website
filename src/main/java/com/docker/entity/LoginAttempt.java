package com.docker.entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Entité pour tracker les tentatives de connexion et implémenter la protection anti-brute force
 * Permet de bloquer les comptes après plusieurs échecs consécutifs
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "login_attempts", indexes = {
    @Index(name = "idx_username", columnList = "username"),
    @Index(name = "idx_ip_address", columnList = "ip_address"),
    @Index(name = "idx_attempt_time", columnList = "attempt_time")
})
public class LoginAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nom d'utilisateur utilisé pour la tentative de connexion
     */
    @Column(nullable = false, length = 50)
    private String username;

    /**
     * Adresse IP depuis laquelle la tentative a été effectuée
     */
    @Column(name = "ip_address", nullable = false, length = 45)
    private String ipAddress;

    /**
     * Date et heure de la tentative
     */
    @Column(name = "attempt_time", nullable = false)
    private LocalDateTime attemptTime;

    /**
     * Succès ou échec de la tentative
     */
    @Column(nullable = false)
    private boolean success;

    /**
     * Message d'erreur en cas d'échec (optionnel)
     */
    @Column(length = 500)
    private String failureReason;

    /**
     * User-Agent du navigateur (pour détecter les connexions suspectes)
     */
    @Column(name = "user_agent", length = 500)
    private String userAgent;

    /**
     * Constructeur pour une nouvelle tentative de connexion
     */
    public LoginAttempt(String username, String ipAddress, boolean success) {
        this.username = username;
        this.ipAddress = ipAddress;
        this.attemptTime = LocalDateTime.now();
        this.success = success;
    }

    /**
     * Constructeur avec raison d'échec
     */
    public LoginAttempt(String username, String ipAddress, boolean success, String failureReason) {
        this(username, ipAddress, success);
        this.failureReason = failureReason;
    }

    @PrePersist
    protected void onCreate() {
        if (attemptTime == null) {
            attemptTime = LocalDateTime.now();
        }
    }
}
