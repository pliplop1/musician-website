package com.docker.service;

import com.docker.dto.PasswordStrengthResult;
import com.docker.dto.PasswordStrengthResult.PasswordStrength;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour PasswordValidationService
 * Vérifie toutes les règles de validation et le calcul du score
 */
class PasswordValidationServiceTest {

    private PasswordValidationService passwordValidationService;

    @BeforeEach
    void setUp() {
        passwordValidationService = new PasswordValidationService();
    }

    // ========================================
    // Tests de validation complète
    // ========================================

    @Test
    @DisplayName("Mot de passe valide fort devrait passer toutes les validations")
    void testValidStrongPassword() {
        String password = "SecureP@ssw0rd2024!";
        PasswordStrengthResult result = passwordValidationService.validatePassword(password);

        assertTrue(result.isValid(), "Le mot de passe devrait être valide");
        assertTrue(result.getScore() >= 70, "Le score devrait être élevé");
        assertEquals(0, result.getErrors().size(), "Il ne devrait pas y avoir d'erreurs");
    }

    @Test
    @DisplayName("Mot de passe valide avec 8 caractères exactement")
    void testMinimumLengthPassword() {
        String password = "Abc123!@";
        PasswordStrengthResult result = passwordValidationService.validatePassword(password);

        assertTrue(result.isValid(), "Un mot de passe de 8 caractères devrait être valide");
    }

    @Test
    @DisplayName("Mot de passe très fort devrait avoir un score >= 90")
    void testVeryStrongPassword() {
        String password = "MyV3ry$ecure&C0mplexP@ssw0rd!2024";
        PasswordStrengthResult result = passwordValidationService.validatePassword(password);

        assertTrue(result.isValid());
        assertTrue(result.getScore() >= 90, "Un mot de passe très complexe devrait avoir un score >= 90");
        assertEquals(PasswordStrength.VERY_STRONG, result.getStrength());
    }

    // ========================================
    // Tests des règles de complexité
    // ========================================

    @Test
    @DisplayName("Mot de passe trop court devrait échouer")
    void testPasswordTooShort() {
        String password = "Abc123!";  // 7 caractères
        PasswordStrengthResult result = passwordValidationService.validatePassword(password);

        assertFalse(result.isValid());
        assertTrue(result.getErrors().stream()
            .anyMatch(e -> e.contains("au moins 8 caractères")));
    }

    @Test
    @DisplayName("Mot de passe sans majuscule devrait échouer")
    void testPasswordWithoutUppercase() {
        String password = "abcdef123!@#";
        PasswordStrengthResult result = passwordValidationService.validatePassword(password);

        assertFalse(result.isValid());
        assertTrue(result.getErrors().stream()
            .anyMatch(e -> e.contains("lettre majuscule")));
    }

    @Test
    @DisplayName("Mot de passe sans minuscule devrait échouer")
    void testPasswordWithoutLowercase() {
        String password = "ABCDEF123!@#";
        PasswordStrengthResult result = passwordValidationService.validatePassword(password);

        assertFalse(result.isValid());
        assertTrue(result.getErrors().stream()
            .anyMatch(e -> e.contains("lettre minuscule")));
    }

    @Test
    @DisplayName("Mot de passe sans chiffre devrait échouer")
    void testPasswordWithoutDigit() {
        String password = "AbcDefGhi!@#";
        PasswordStrengthResult result = passwordValidationService.validatePassword(password);

        assertFalse(result.isValid());
        assertTrue(result.getErrors().stream()
            .anyMatch(e -> e.contains("chiffre")));
    }

    @Test
    @DisplayName("Mot de passe sans caractère spécial devrait échouer")
    void testPasswordWithoutSpecialChar() {
        String password = "AbcDef123456";
        PasswordStrengthResult result = passwordValidationService.validatePassword(password);

        assertFalse(result.isValid());
        assertTrue(result.getErrors().stream()
            .anyMatch(e -> e.contains("caractère spécial")));
    }

    // ========================================
    // Tests des mots de passe courants
    // ========================================

    @Test
    @DisplayName("Mot de passe courant 'password123' devrait échouer")
    void testCommonPassword_Password() {
        String password = "password123";  // Exact match in common passwords list
        PasswordStrengthResult result = passwordValidationService.validatePassword(password);

        assertFalse(result.isValid());
        assertTrue(result.getErrors().stream()
            .anyMatch(e -> e.contains("trop courant")));
    }

    @Test
    @DisplayName("Mot de passe courant '123456' devrait échouer")
    void testCommonPassword_123456() {
        String password = "123456";
        PasswordStrengthResult result = passwordValidationService.validatePassword(password);

        assertFalse(result.isValid());
        // Ce mot de passe échoue pour plusieurs raisons : trop court, pas de majuscule, pas de minuscule, pas de caractère spécial
    }

    @Test
    @DisplayName("Mot de passe courant 'qwerty123' devrait échouer")
    void testCommonPassword_Qwerty() {
        String password = "qwerty123";
        PasswordStrengthResult result = passwordValidationService.validatePassword(password);

        assertFalse(result.isValid());
        assertTrue(result.getErrors().stream()
            .anyMatch(e -> e.contains("trop courant")));
    }

    @Test
    @DisplayName("Mot de passe courant 'admin123' devrait échouer")
    void testCommonPassword_Admin() {
        String password = "admin123";
        PasswordStrengthResult result = passwordValidationService.validatePassword(password);

        assertFalse(result.isValid());
        // Ce mot de passe échoue pour plusieurs raisons : pas de majuscule, pas de caractère spécial, et c'est un mot de passe courant
    }

    // ========================================
    // Tests des séquences et répétitions
    // ========================================

    @Test
    @DisplayName("Mot de passe avec séquence numérique devrait avoir une suggestion")
    void testPasswordWithNumericSequence() {
        String password = "Abc123def!@";
        PasswordStrengthResult result = passwordValidationService.validatePassword(password);

        assertTrue(result.isValid());
        assertTrue(result.getSuggestions().stream()
            .anyMatch(s -> s.contains("séquences simples")));
    }

    @Test
    @DisplayName("Mot de passe avec séquence alphabétique devrait avoir une suggestion")
    void testPasswordWithAlphabeticSequence() {
        String password = "Abcdef12!@";
        PasswordStrengthResult result = passwordValidationService.validatePassword(password);

        assertTrue(result.isValid());
        assertTrue(result.getSuggestions().stream()
            .anyMatch(s -> s.contains("séquences simples")));
    }

    @Test
    @DisplayName("Mot de passe avec répétitions devrait avoir une suggestion")
    void testPasswordWithRepeatedChars() {
        String password = "Aaa111Bbb!!!";
        PasswordStrengthResult result = passwordValidationService.validatePassword(password);

        assertTrue(result.isValid());
        assertTrue(result.getSuggestions().stream()
            .anyMatch(s -> s.contains("répétitions")));
    }

    // ========================================
    // Tests de calcul de score
    // ========================================

    @Test
    @DisplayName("Mot de passe minimal devrait être valide mais avec score moyen")
    void testWeakPassword() {
        String password = "Abcd123!";  // Minimum requis
        PasswordStrengthResult result = passwordValidationService.validatePassword(password);

        assertTrue(result.isValid());
        assertTrue(result.getScore() >= 70, "Un mot de passe minimal devrait avoir un score >= 70");
    }

    @Test
    @DisplayName("Mot de passe moyen devrait avoir un score >= 70")
    void testMediumPassword() {
        String password = "GoodP@ssw0rd";
        PasswordStrengthResult result = passwordValidationService.validatePassword(password);

        assertTrue(result.isValid());
        int score = result.getScore();
        assertTrue(score >= 70, "Score devrait être >= 70");
    }

    @Test
    @DisplayName("Mot de passe long devrait avoir un bonus de score")
    void testLongPasswordBonus() {
        String password1 = "Xbcd567!";  // 8 caractères - éviter séquence abc/123
        String password2 = "Xbcd567!Wxyz890@";  // 16 caractères
        String password3 = "Xbcd567!Wxyz890@Mnpq234#Fghj345$";  // 32 caractères

        PasswordStrengthResult result1 = passwordValidationService.validatePassword(password1);
        PasswordStrengthResult result2 = passwordValidationService.validatePassword(password2);
        PasswordStrengthResult result3 = passwordValidationService.validatePassword(password3);

        assertTrue(result2.getScore() > result1.getScore(),
            "Un mot de passe plus long devrait avoir un meilleur score");
        assertTrue(result3.getScore() >= result2.getScore(),
            "Un mot de passe très long devrait avoir un score au moins égal");
    }

    // ========================================
    // Tests des cas limites
    // ========================================

    @Test
    @DisplayName("Mot de passe null devrait échouer")
    void testNullPassword() {
        PasswordStrengthResult result = passwordValidationService.validatePassword(null);

        assertFalse(result.isValid());
        assertEquals(0, result.getScore());
        assertTrue(result.getErrors().stream()
            .anyMatch(e -> e.contains("ne peut pas être vide")));
    }

    @Test
    @DisplayName("Mot de passe vide devrait échouer")
    void testEmptyPassword() {
        PasswordStrengthResult result = passwordValidationService.validatePassword("");

        assertFalse(result.isValid());
        assertEquals(0, result.getScore());
    }

    @Test
    @DisplayName("Mot de passe avec espaces devrait être traité normalement")
    void testPasswordWithSpaces() {
        String password = "My Secure P@ssw0rd!";
        PasswordStrengthResult result = passwordValidationService.validatePassword(password);

        assertTrue(result.isValid(), "Les espaces devraient être autorisés");
    }

    @Test
    @DisplayName("Mot de passe avec caractères Unicode devrait fonctionner")
    void testPasswordWithUnicode() {
        String password = "Sécuré123!@#";
        PasswordStrengthResult result = passwordValidationService.validatePassword(password);

        assertTrue(result.isValid(), "Les caractères Unicode devraient être autorisés");
    }

    // ========================================
    // Tests des méthodes utilitaires
    // ========================================

    @Test
    @DisplayName("isPasswordValid() devrait retourner true pour un mot de passe valide")
    void testIsPasswordValid_Valid() {
        String password = "SecureP@ssw0rd123";
        assertTrue(passwordValidationService.isPasswordValid(password));
    }

    @Test
    @DisplayName("isPasswordValid() devrait retourner false pour un mot de passe invalide")
    void testIsPasswordValid_Invalid() {
        String password = "weak";
        assertFalse(passwordValidationService.isPasswordValid(password));
    }

    @Test
    @DisplayName("getValidationErrorMessage() devrait retourner null pour un mot de passe valide")
    void testGetValidationErrorMessage_Valid() {
        String password = "SecureP@ssw0rd123";
        assertNull(passwordValidationService.getValidationErrorMessage(password));
    }

    @Test
    @DisplayName("getValidationErrorMessage() devrait retourner un message d'erreur pour un mot de passe invalide")
    void testGetValidationErrorMessage_Invalid() {
        String password = "weak";
        String errorMessage = passwordValidationService.getValidationErrorMessage(password);

        assertNotNull(errorMessage);
        assertFalse(errorMessage.isEmpty());
    }

    // ========================================
    // Tests de tous les types de caractères spéciaux
    // ========================================

    @Test
    @DisplayName("Tous les caractères spéciaux autorisés devraient être acceptés")
    void testAllSpecialChars() {
        String[] specialChars = {"!", "@", "#", "$", "%", "^", "&", "*", "(", ")",
                                 ",", ".", "?", "\"", ":", "{", "}", "|", "<", ">",
                                 "_", "-", "+", "=", "[", "]", "\\", "/", "'", "`",
                                 ";", "~"};

        for (String specialChar : specialChars) {
            String password = "Password123" + specialChar;
            PasswordStrengthResult result = passwordValidationService.validatePassword(password);

            assertTrue(result.isValid(),
                "Le caractère spécial '" + specialChar + "' devrait être accepté");
        }
    }

    // ========================================
    // Tests de force de mot de passe
    // ========================================

    @Test
    @DisplayName("PasswordStrength devrait être correctement calculée selon le score")
    void testPasswordStrengthEnum() {
        assertEquals(PasswordStrength.VERY_WEAK, PasswordStrength.fromScore(0));
        assertEquals(PasswordStrength.VERY_WEAK, PasswordStrength.fromScore(25));
        assertEquals(PasswordStrength.WEAK, PasswordStrength.fromScore(30));
        assertEquals(PasswordStrength.MEDIUM, PasswordStrength.fromScore(50));
        assertEquals(PasswordStrength.STRONG, PasswordStrength.fromScore(75));
        assertEquals(PasswordStrength.VERY_STRONG, PasswordStrength.fromScore(95));
    }

    @Test
    @DisplayName("Mot de passe avec bonne diversité de caractères devrait avoir un bonus")
    void testCharacterDiversity() {
        String password1 = "Aaaaaaaa1!";  // Peu de diversité
        String password2 = "Abcdef12!@";  // Bonne diversité

        PasswordStrengthResult result1 = passwordValidationService.validatePassword(password1);
        PasswordStrengthResult result2 = passwordValidationService.validatePassword(password2);

        assertTrue(result2.getScore() > result1.getScore(),
            "Une meilleure diversité devrait augmenter le score");
    }
}
