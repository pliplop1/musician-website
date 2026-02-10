package com.docker.service;

import com.docker.entity.PasswordResetToken;
import com.docker.entity.User;
import com.docker.repository.PasswordResetTokenRepository;
import com.docker.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires pour PasswordResetService
 * Teste la génération de tokens, validation et réinitialisation de mots de passe
 */
@ExtendWith(MockitoExtension.class)
class PasswordResetServiceTest {

    @Mock
    private PasswordResetTokenRepository tokenRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private PasswordResetService passwordResetService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedPassword");
    }

    // ========================================
    // Tests de création de token
    // ========================================

    @Test
    @DisplayName("Création de token pour un email existant devrait réussir")
    void testCreatePasswordResetToken_UserExists() {
        // Arrange
        when(userRepository.findByEmail("test@example.com")).thenReturn(testUser);
        when(tokenRepository.save(any(PasswordResetToken.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        boolean result = passwordResetService.createPasswordResetTokenForUser("test@example.com");

        // Assert
        assertTrue(result, "La création du token devrait réussir");
        verify(userRepository).findByEmail("test@example.com");
        verify(tokenRepository).deleteByUser(testUser); // Suppression des anciens tokens
        verify(tokenRepository).save(any(PasswordResetToken.class));
        verify(emailService).sendPasswordResetEmail(
            eq("test@example.com"),
            eq("testuser"),
            anyString()
        );
    }

    @Test
    @DisplayName("Création de token pour un email inexistant devrait retourner true (sécurité)")
    void testCreatePasswordResetToken_UserNotExists() {
        // Arrange
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(null);

        // Act
        boolean result = passwordResetService.createPasswordResetTokenForUser("nonexistent@example.com");

        // Assert
        assertTrue(result, "Devrait retourner true pour ne pas révéler l'existence du compte");
        verify(userRepository).findByEmail("nonexistent@example.com");
        verify(tokenRepository, never()).save(any());
        verify(emailService, never()).sendPasswordResetEmail(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("Création de token devrait générer un token UUID valide")
    void testCreatePasswordResetToken_TokenFormat() {
        // Arrange
        when(userRepository.findByEmail("test@example.com")).thenReturn(testUser);
        ArgumentCaptor<PasswordResetToken> tokenCaptor = ArgumentCaptor.forClass(PasswordResetToken.class);

        // Act
        passwordResetService.createPasswordResetTokenForUser("test@example.com");

        // Assert
        verify(tokenRepository).save(tokenCaptor.capture());
        PasswordResetToken savedToken = tokenCaptor.getValue();

        assertNotNull(savedToken.getToken());
        assertTrue(savedToken.getToken().length() == 36, "Token devrait être un UUID (36 caractères)");
        assertTrue(savedToken.getToken().contains("-"), "Token devrait être au format UUID");
        assertEquals(testUser, savedToken.getUser());
        assertFalse(savedToken.isUsed());
    }

    @Test
    @DisplayName("Création de token devrait supprimer les anciens tokens")
    void testCreatePasswordResetToken_DeletesOldTokens() {
        // Arrange
        when(userRepository.findByEmail("test@example.com")).thenReturn(testUser);

        // Act
        passwordResetService.createPasswordResetTokenForUser("test@example.com");

        // Assert
        verify(tokenRepository).deleteByUser(testUser);
    }

    @Test
    @DisplayName("Création de token devrait envoyer un email avec l'URL correcte")
    void testCreatePasswordResetToken_EmailUrl() {
        // Arrange
        when(userRepository.findByEmail("test@example.com")).thenReturn(testUser);
        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);

        // Act
        passwordResetService.createPasswordResetTokenForUser("test@example.com");

        // Assert
        verify(emailService).sendPasswordResetEmail(
            eq("test@example.com"),
            eq("testuser"),
            urlCaptor.capture()
        );

        String capturedUrl = urlCaptor.getValue();
        assertTrue(capturedUrl.contains("/reset-password?token="));
    }

    // ========================================
    // Tests de validation de token
    // ========================================

    @Test
    @DisplayName("Validation d'un token valide devrait réussir")
    void testValidatePasswordResetToken_Valid() {
        // Arrange
        PasswordResetToken token = new PasswordResetToken("valid-token", testUser, 60);
        when(tokenRepository.findByToken("valid-token")).thenReturn(Optional.of(token));

        // Act
        Optional<PasswordResetToken> result = passwordResetService.validatePasswordResetToken("valid-token");

        // Assert
        assertTrue(result.isPresent(), "Token valide devrait être retourné");
        assertEquals(token, result.get());
        verify(tokenRepository).findByToken("valid-token");
    }

    @Test
    @DisplayName("Validation d'un token inexistant devrait échouer")
    void testValidatePasswordResetToken_NotFound() {
        // Arrange
        when(tokenRepository.findByToken("nonexistent-token")).thenReturn(Optional.empty());

        // Act
        Optional<PasswordResetToken> result = passwordResetService.validatePasswordResetToken("nonexistent-token");

        // Assert
        assertFalse(result.isPresent(), "Token inexistant ne devrait pas être retourné");
        verify(tokenRepository).findByToken("nonexistent-token");
    }

    @Test
    @DisplayName("Validation d'un token expiré devrait échouer")
    void testValidatePasswordResetToken_Expired() {
        // Arrange
        PasswordResetToken token = new PasswordResetToken("expired-token", testUser, 60);
        // Forcer l'expiration en modifiant la date d'expiration
        token.setExpiryDate(LocalDateTime.now().minusHours(2));
        when(tokenRepository.findByToken("expired-token")).thenReturn(Optional.of(token));

        // Act
        Optional<PasswordResetToken> result = passwordResetService.validatePasswordResetToken("expired-token");

        // Assert
        assertFalse(result.isPresent(), "Token expiré ne devrait pas être retourné");
    }

    @Test
    @DisplayName("Validation d'un token déjà utilisé devrait échouer")
    void testValidatePasswordResetToken_AlreadyUsed() {
        // Arrange
        PasswordResetToken token = new PasswordResetToken("used-token", testUser, 60);
        token.setUsed(true);
        when(tokenRepository.findByToken("used-token")).thenReturn(Optional.of(token));

        // Act
        Optional<PasswordResetToken> result = passwordResetService.validatePasswordResetToken("used-token");

        // Assert
        assertFalse(result.isPresent(), "Token déjà utilisé ne devrait pas être retourné");
    }

    // ========================================
    // Tests de réinitialisation de mot de passe
    // ========================================

    @Test
    @DisplayName("Réinitialisation avec un token valide devrait réussir")
    void testResetPassword_Success() {
        // Arrange
        PasswordResetToken token = new PasswordResetToken("valid-token", testUser, 60);
        when(tokenRepository.findByToken("valid-token")).thenReturn(Optional.of(token));
        when(passwordEncoder.encode("NewP@ssw0rd!")).thenReturn("encodedNewPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(tokenRepository.save(any(PasswordResetToken.class))).thenReturn(token);

        // Act
        boolean result = passwordResetService.resetPassword("valid-token", "NewP@ssw0rd!");

        // Assert
        assertTrue(result, "La réinitialisation devrait réussir");
        verify(passwordEncoder).encode("NewP@ssw0rd!");
        verify(userRepository).save(testUser);
        verify(tokenRepository).save(token);
        assertEquals("encodedNewPassword", testUser.getPassword());
        assertTrue(token.isUsed(), "Token devrait être marqué comme utilisé");
    }

    @Test
    @DisplayName("Réinitialisation avec un token invalide devrait échouer")
    void testResetPassword_InvalidToken() {
        // Arrange
        when(tokenRepository.findByToken("invalid-token")).thenReturn(Optional.empty());

        // Act
        boolean result = passwordResetService.resetPassword("invalid-token", "NewP@ssw0rd!");

        // Assert
        assertFalse(result, "La réinitialisation devrait échouer");
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Réinitialisation avec un token expiré devrait échouer")
    void testResetPassword_ExpiredToken() {
        // Arrange
        PasswordResetToken token = new PasswordResetToken("expired-token", testUser, 60);
        token.setExpiryDate(LocalDateTime.now().minusHours(2));
        when(tokenRepository.findByToken("expired-token")).thenReturn(Optional.of(token));

        // Act
        boolean result = passwordResetService.resetPassword("expired-token", "NewP@ssw0rd!");

        // Assert
        assertFalse(result, "La réinitialisation avec un token expiré devrait échouer");
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Réinitialisation devrait encoder le nouveau mot de passe")
    void testResetPassword_PasswordEncoded() {
        // Arrange
        PasswordResetToken token = new PasswordResetToken("valid-token", testUser, 60);
        when(tokenRepository.findByToken("valid-token")).thenReturn(Optional.of(token));
        when(passwordEncoder.encode("NewP@ssw0rd!")).thenReturn("encodedNewPassword123");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(tokenRepository.save(any(PasswordResetToken.class))).thenReturn(token);

        // Act
        passwordResetService.resetPassword("valid-token", "NewP@ssw0rd!");

        // Assert
        verify(passwordEncoder).encode("NewP@ssw0rd!");
        assertEquals("encodedNewPassword123", testUser.getPassword());
    }

    @Test
    @DisplayName("Réinitialisation devrait marquer le token comme utilisé")
    void testResetPassword_TokenMarkedAsUsed() {
        // Arrange
        PasswordResetToken token = new PasswordResetToken("valid-token", testUser, 60);
        when(tokenRepository.findByToken("valid-token")).thenReturn(Optional.of(token));
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(tokenRepository.save(any(PasswordResetToken.class))).thenReturn(token);

        // Act
        passwordResetService.resetPassword("valid-token", "NewP@ssw0rd!");

        // Assert
        assertTrue(token.isUsed(), "Token devrait être marqué comme utilisé");
        verify(tokenRepository).save(token);
    }

    // ========================================
    // Tests de nettoyage des tokens expirés
    // ========================================

    @Test
    @DisplayName("Nettoyage devrait supprimer les tokens expirés")
    void testCleanupExpiredTokens() {
        // Arrange
        doNothing().when(tokenRepository).deleteByExpiryDateBefore(any(LocalDateTime.class));

        // Act
        passwordResetService.cleanupExpiredTokens();

        // Assert
        verify(tokenRepository).deleteByExpiryDateBefore(any(LocalDateTime.class));
    }

    @Test
    @DisplayName("Nettoyage devrait utiliser la date actuelle comme référence")
    void testCleanupExpiredTokens_UsesCurrentTime() {
        // Arrange
        ArgumentCaptor<LocalDateTime> dateCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        doNothing().when(tokenRepository).deleteByExpiryDateBefore(any(LocalDateTime.class));

        // Act
        LocalDateTime before = LocalDateTime.now().minusSeconds(1);
        passwordResetService.cleanupExpiredTokens();
        LocalDateTime after = LocalDateTime.now().plusSeconds(1);

        // Assert
        verify(tokenRepository).deleteByExpiryDateBefore(dateCaptor.capture());
        LocalDateTime capturedDate = dateCaptor.getValue();

        assertTrue(capturedDate.isAfter(before) && capturedDate.isBefore(after),
            "La date de référence devrait être l'heure actuelle");
    }

    // ========================================
    // Tests d'intégration des scénarios complets
    // ========================================

    @Test
    @DisplayName("Scénario complet: Création de token puis réinitialisation réussie")
    void testCompletePasswordResetFlow_Success() {
        // Arrange - Création de token
        when(userRepository.findByEmail("test@example.com")).thenReturn(testUser);
        ArgumentCaptor<PasswordResetToken> tokenCaptor = ArgumentCaptor.forClass(PasswordResetToken.class);
        when(tokenRepository.save(any(PasswordResetToken.class))).thenAnswer(i -> i.getArgument(0));

        // Act - Créer le token
        passwordResetService.createPasswordResetTokenForUser("test@example.com");

        // Assert - Token créé
        verify(tokenRepository).save(tokenCaptor.capture());
        String createdToken = tokenCaptor.getValue().getToken();

        // Arrange - Réinitialisation
        PasswordResetToken savedToken = tokenCaptor.getValue();
        when(tokenRepository.findByToken(createdToken)).thenReturn(Optional.of(savedToken));
        when(passwordEncoder.encode("NewP@ssw0rd!")).thenReturn("encodedNewPassword");

        // Act - Réinitialiser le mot de passe
        boolean resetResult = passwordResetService.resetPassword(createdToken, "NewP@ssw0rd!");

        // Assert - Réinitialisation réussie
        assertTrue(resetResult);
        assertEquals("encodedNewPassword", testUser.getPassword());
        assertTrue(savedToken.isUsed());
    }

    @Test
    @DisplayName("Scénario complet: Token ne peut pas être réutilisé")
    void testCompletePasswordResetFlow_TokenCantBeReused() {
        // Arrange
        PasswordResetToken token = new PasswordResetToken("valid-token", testUser, 60);
        when(tokenRepository.findByToken("valid-token")).thenReturn(Optional.of(token));
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(tokenRepository.save(any(PasswordResetToken.class))).thenReturn(token);

        // Act - Première utilisation
        boolean firstReset = passwordResetService.resetPassword("valid-token", "NewP@ssw0rd!");

        // Assert - Première utilisation réussie
        assertTrue(firstReset);
        assertTrue(token.isUsed());

        // Act - Deuxième tentative avec le même token
        boolean secondReset = passwordResetService.resetPassword("valid-token", "AnotherP@ssw0rd!");

        // Assert - Deuxième utilisation échouée
        assertFalse(secondReset, "Token déjà utilisé ne devrait pas fonctionner");
    }

    @Test
    @DisplayName("Scénario: Plusieurs demandes devraient invalider les anciens tokens")
    void testMultipleResetRequests_InvalidatesOldTokens() {
        // Arrange
        when(userRepository.findByEmail("test@example.com")).thenReturn(testUser);

        // Act - Première demande
        passwordResetService.createPasswordResetTokenForUser("test@example.com");

        // Act - Deuxième demande
        passwordResetService.createPasswordResetTokenForUser("test@example.com");

        // Assert - Les anciens tokens devraient être supprimés 2 fois
        verify(tokenRepository, times(2)).deleteByUser(testUser);
    }
}
