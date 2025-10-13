package com.docker.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour afficher les tentatives de connexion dans le dashboard admin
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginAttemptDTO {
    private Long id;
    private String username;
    private String ipAddress;
    private LocalDateTime attemptTime;
    private boolean success;
    private String failureReason;
    private String userAgent;

    /**
     * Obtient le statut formaté pour l'affichage
     */
    public String getStatusLabel() {
        return success ? "✅ Succès" : "❌ Échec";
    }

    /**
     * Obtient la classe CSS pour le statut
     */
    public String getStatusClass() {
        return success ? "success" : "failure";
    }
}
