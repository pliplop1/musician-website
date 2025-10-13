package com.docker.service;

import com.docker.dto.PasswordStrengthResult;
import com.docker.dto.PasswordStrengthResult.PasswordStrength;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Service de validation de la force et de la sécurité des mots de passe
 *
 * Règles de validation :
 * - Minimum 8 caractères
 * - Au moins une lettre majuscule
 * - Au moins une lettre minuscule
 * - Au moins un chiffre
 * - Au moins un caractère spécial
 * - Pas dans la liste des mots de passe courants
 */
@Service
public class PasswordValidationService {

    private static final Logger logger = LoggerFactory.getLogger(PasswordValidationService.class);

    // Configuration
    private static final int MIN_LENGTH = 8;
    private static final int PREFERRED_LENGTH = 12;

    // Patterns regex pour les règles de complexité
    private static final Pattern UPPERCASE_PATTERN = Pattern.compile("[A-Z]");
    private static final Pattern LOWERCASE_PATTERN = Pattern.compile("[a-z]");
    private static final Pattern DIGIT_PATTERN = Pattern.compile("[0-9]");
    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile("[!@#$%^&*(),.?\":{}|<>_\\-+=\\[\\]\\\\/'`;~]");

    // Top 100 mots de passe les plus courants à interdire
    private static final Set<String> COMMON_PASSWORDS = new HashSet<>(Arrays.asList(
        "password", "123456", "123456789", "12345678", "12345", "1234567", "password1",
        "123123", "1234567890", "000000", "abc123", "password123", "qwerty", "qwerty123",
        "111111", "123321", "1q2w3e4r", "admin", "letmein", "welcome", "monkey", "dragon",
        "master", "sunshine", "princess", "football", "batman", "shadow", "michael", "jennifer",
        "passw0rd", "password!", "qwerty1", "azerty", "azerty123", "superman", "iloveyou",
        "trustno1", "starwars", "hello", "welcome123", "access", "root", "admin123",
        "test", "test123", "guest", "user", "changeme", "letmein123", "qwertyuiop",
        "asdfghjkl", "zxcvbnm", "654321", "123qwe", "qweasd", "123abc", "abc1234",
        "password12", "admin1234", "user123", "demo", "temp", "temp123", "sample",
        "example", "pass", "pass123", "pass1234", "pwd", "pwd123", "pwd1234",
        "default", "secret", "secret123", "love", "love123", "god", "jesus", "hello123",
        "welcome1", "p@ssw0rd", "p@ssword", "pa55word", "pa55w0rd", "administrator",
        "administrator123", "admin1", "root123", "toor", "1234", "12345678901", "1111",
        "11111111", "qwerty12", "monkey123", "football123", "baseball", "basketball",
        "soccer", "hockey", "mustang", "ferrari", "porsche", "mercedes"
    ));

    /**
     * Valide un mot de passe selon toutes les règles de sécurité
     *
     * @param password Le mot de passe à valider
     * @return PasswordStrengthResult avec le résultat de la validation
     */
    public PasswordStrengthResult validatePassword(String password) {
        PasswordStrengthResult result = new PasswordStrengthResult();

        if (password == null || password.isEmpty()) {
            result.addError("Le mot de passe ne peut pas être vide");
            result.setScore(0);
            return result;
        }

        int score = 0;
        String passwordLower = password.toLowerCase();

        // 1. Vérifier la longueur minimale (obligatoire)
        if (password.length() < MIN_LENGTH) {
            result.addError("Le mot de passe doit contenir au moins " + MIN_LENGTH + " caractères");
        } else {
            score += 20;

            // Bonus pour longueur supérieure
            if (password.length() >= PREFERRED_LENGTH) {
                score += 10;
                result.addSuggestion("Excellente longueur de mot de passe !");
            } else {
                result.addSuggestion("Pour plus de sécurité, utilisez au moins " + PREFERRED_LENGTH + " caractères");
            }

            // Bonus additionnel pour très longues phrases
            if (password.length() >= 16) {
                score += 5;
            }
        }

        // 2. Vérifier la présence de majuscules (obligatoire)
        if (!UPPERCASE_PATTERN.matcher(password).find()) {
            result.addError("Le mot de passe doit contenir au moins une lettre majuscule (A-Z)");
        } else {
            score += 15;
        }

        // 3. Vérifier la présence de minuscules (obligatoire)
        if (!LOWERCASE_PATTERN.matcher(password).find()) {
            result.addError("Le mot de passe doit contenir au moins une lettre minuscule (a-z)");
        } else {
            score += 15;
        }

        // 4. Vérifier la présence de chiffres (obligatoire)
        if (!DIGIT_PATTERN.matcher(password).find()) {
            result.addError("Le mot de passe doit contenir au moins un chiffre (0-9)");
        } else {
            score += 15;
        }

        // 5. Vérifier la présence de caractères spéciaux (obligatoire)
        if (!SPECIAL_CHAR_PATTERN.matcher(password).find()) {
            result.addError("Le mot de passe doit contenir au moins un caractère spécial (!@#$%^&*...)");
        } else {
            score += 15;
        }

        // 6. Vérifier contre les mots de passe courants (obligatoire)
        if (COMMON_PASSWORDS.contains(passwordLower)) {
            result.addError("Ce mot de passe est trop courant et facilement devinable");
            score = Math.max(0, score - 30); // Pénalité importante
        }

        // 7. Vérifier les séquences simples (bonus)
        if (containsSequentialChars(password)) {
            result.addSuggestion("Évitez les séquences simples comme '123' ou 'abc'");
            score -= 5;
        }

        // 8. Vérifier les répétitions (bonus)
        if (containsRepeatedChars(password)) {
            result.addSuggestion("Évitez les répétitions comme 'aaa' ou '111'");
            score -= 5;
        }

        // 9. Bonus pour diversité de caractères
        int uniqueChars = (int) password.chars().distinct().count();
        if (uniqueChars > password.length() * 0.7) {
            score += 10;
            result.addSuggestion("Bonne diversité de caractères !");
        }

        // 10. Bonus pour mélange de types de caractères
        int charTypes = 0;
        if (UPPERCASE_PATTERN.matcher(password).find()) charTypes++;
        if (LOWERCASE_PATTERN.matcher(password).find()) charTypes++;
        if (DIGIT_PATTERN.matcher(password).find()) charTypes++;
        if (SPECIAL_CHAR_PATTERN.matcher(password).find()) charTypes++;

        if (charTypes == 4) {
            score += 10;
        }

        // Normaliser le score entre 0 et 100
        score = Math.max(0, Math.min(100, score));
        result.setScore(score);

        // Ajouter des suggestions générales
        if (result.isValid()) {
            if (score < 70) {
                result.addSuggestion("Votre mot de passe pourrait être renforcé");
            } else if (score >= 90) {
                result.addSuggestion("Excellent mot de passe, très sécurisé !");
            }
        }

        logger.info("Validation mot de passe - Score: {}/100, Force: {}, Valide: {}",
                    score, result.getStrength(), result.isValid());

        return result;
    }

    /**
     * Vérifie si le mot de passe contient des séquences consécutives
     */
    private boolean containsSequentialChars(String password) {
        String lower = password.toLowerCase();

        // Séquences numériques (123, 321, etc.)
        for (int i = 0; i < lower.length() - 2; i++) {
            if (Character.isDigit(lower.charAt(i))) {
                char c1 = lower.charAt(i);
                char c2 = lower.charAt(i + 1);
                char c3 = lower.charAt(i + 2);

                if (c2 == c1 + 1 && c3 == c2 + 1) return true; // 123
                if (c2 == c1 - 1 && c3 == c2 - 1) return true; // 321
            }
        }

        // Séquences alphabétiques (abc, xyz, cba, etc.)
        for (int i = 0; i < lower.length() - 2; i++) {
            if (Character.isLetter(lower.charAt(i))) {
                char c1 = lower.charAt(i);
                char c2 = lower.charAt(i + 1);
                char c3 = lower.charAt(i + 2);

                if (c2 == c1 + 1 && c3 == c2 + 1) return true; // abc
                if (c2 == c1 - 1 && c3 == c2 - 1) return true; // cba
            }
        }

        return false;
    }

    /**
     * Vérifie si le mot de passe contient des répétitions de caractères
     */
    private boolean containsRepeatedChars(String password) {
        for (int i = 0; i < password.length() - 2; i++) {
            char c = password.charAt(i);
            if (c == password.charAt(i + 1) && c == password.charAt(i + 2)) {
                return true; // 3 caractères identiques consécutifs
            }
        }
        return false;
    }

    /**
     * Vérifie simplement si un mot de passe respecte les exigences minimales
     * (sans calcul de score détaillé)
     */
    public boolean isPasswordValid(String password) {
        return validatePassword(password).isValid();
    }

    /**
     * Retourne un message d'erreur simple si le mot de passe est invalide
     */
    public String getValidationErrorMessage(String password) {
        PasswordStrengthResult result = validatePassword(password);

        if (result.isValid()) {
            return null;
        }

        return String.join("\n", result.getErrors());
    }
}
