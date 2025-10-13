package com.docker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * DTO pour les statistiques de sécurité du dashboard admin
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SecurityStatsDTO {

    // Statistiques globales
    private long totalAttempts;
    private long successfulAttempts;
    private long failedAttempts;
    private long blockedAccounts;

    // Dernières tentatives (50 dernières)
    private List<LoginAttemptDTO> recentAttempts;

    // IPs suspectes (plus de 3 échecs dans les dernières 24h)
    private List<SuspiciousIpDTO> suspiciousIps;

    // Statistiques par jour (7 derniers jours)
    private Map<String, DailyStatsDTO> dailyStats;

    /**
     * Calcule le taux de réussite en pourcentage
     */
    public double getSuccessRate() {
        if (totalAttempts == 0) return 0;
        return (successfulAttempts * 100.0) / totalAttempts;
    }

    /**
     * Calcule le taux d'échec en pourcentage
     */
    public double getFailureRate() {
        if (totalAttempts == 0) return 0;
        return (failedAttempts * 100.0) / totalAttempts;
    }
}
