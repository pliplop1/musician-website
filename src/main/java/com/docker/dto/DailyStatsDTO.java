package com.docker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour les statistiques journalières
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyStatsDTO {
    private String date;
    private long totalAttempts;
    private long successfulAttempts;
    private long failedAttempts;

    public double getSuccessRate() {
        if (totalAttempts == 0) return 0;
        return (successfulAttempts * 100.0) / totalAttempts;
    }
}
