package com.docker.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Résultat de la validation de la force d'un mot de passe
 */
public class PasswordStrengthResult {

    private boolean valid;
    private PasswordStrength strength;
    private int score; // 0-100
    private List<String> errors;
    private List<String> suggestions;

    public PasswordStrengthResult() {
        this.errors = new ArrayList<>();
        this.suggestions = new ArrayList<>();
        this.valid = true;
        this.strength = PasswordStrength.WEAK;
        this.score = 0;
    }

    public enum PasswordStrength {
        VERY_WEAK("Très faible", 0),
        WEAK("Faible", 25),
        MEDIUM("Moyen", 50),
        STRONG("Fort", 75),
        VERY_STRONG("Très fort", 100);

        private final String label;
        private final int minScore;

        PasswordStrength(String label, int minScore) {
            this.label = label;
            this.minScore = minScore;
        }

        public String getLabel() {
            return label;
        }

        public int getMinScore() {
            return minScore;
        }

        public static PasswordStrength fromScore(int score) {
            if (score >= 90) return VERY_STRONG;
            if (score >= 70) return STRONG;
            if (score >= 50) return MEDIUM;
            if (score >= 30) return WEAK;
            return VERY_WEAK;
        }
    }

    // Getters et Setters

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public PasswordStrength getStrength() {
        return strength;
    }

    public void setStrength(PasswordStrength strength) {
        this.strength = strength;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
        this.strength = PasswordStrength.fromScore(score);
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public void addError(String error) {
        this.errors.add(error);
        this.valid = false;
    }

    public List<String> getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(List<String> suggestions) {
        this.suggestions = suggestions;
    }

    public void addSuggestion(String suggestion) {
        this.suggestions.add(suggestion);
    }

    public String getStrengthLabel() {
        return strength.getLabel();
    }

    public String getStrengthColor() {
        switch (strength) {
            case VERY_WEAK:
            case WEAK:
                return "red";
            case MEDIUM:
                return "orange";
            case STRONG:
                return "yellow";
            case VERY_STRONG:
                return "green";
            default:
                return "gray";
        }
    }
}
