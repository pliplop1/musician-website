package com.docker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour les IPs suspectes avec nombreuses tentatives échouées
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SuspiciousIpDTO {
    private String ipAddress;
    private long failedAttempts;
    private long successfulAttempts;
    private String lastUsername;
    private String lastAttemptTime;

    /**
     * Obtient le niveau de menace
     */
    public String getThreatLevel() {
        if (failedAttempts >= 10) return "Élevé";
        if (failedAttempts >= 5) return "Moyen";
        return "Faible";
    }

    /**
     * Obtient la classe CSS pour le niveau de menace
     */
    public String getThreatClass() {
        if (failedAttempts >= 10) return "threat-high";
        if (failedAttempts >= 5) return "threat-medium";
        return "threat-low";
    }
}
