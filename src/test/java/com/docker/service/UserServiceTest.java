package com.docker.service;

import com.docker.entity.Role;
import com.docker.entity.User;
import com.docker.repository.ConcertRepository;
import com.docker.repository.RoleRepository;
import com.docker.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour UserService
 * Vérifie toutes les fonctionnalités CRUD et métier du service utilisateur
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Role userRole;

    @BeforeEach
    void setUp() {
        // Créer le rôle USER s'il n'existe pas
        userRole = roleRepository.findByName("ROLE_USER");
        if (userRole == null) {
            userRole = new Role();
            userRole.setName("ROLE_USER");
            roleRepository.save(userRole);
        }
    }

    @Test
    void testSaveUser_ValidPassword_Success() {
        // Given
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("Password123!");  // Mot de passe valide

        // When
        userService.saveUser(user);

        // Then
        User savedUser = userRepository.findByUsername("testuser");
        assertNotNull(savedUser);
        assertEquals("testuser", savedUser.getUsername());
        assertEquals("test@example.com", savedUser.getEmail());
        assertTrue(passwordEncoder.matches("Password123!", savedUser.getPassword()));
        assertTrue(savedUser.getRoles().stream().anyMatch(r -> r.getName().equals("ROLE_USER")));
    }

    @Test
    void testSaveUser_InvalidPassword_ThrowsException() {
        // Given
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("weak");  // Mot de passe invalide

        // When/Then
        assertThrows(IllegalArgumentException.class, () -> userService.saveUser(user),
                "Le mot de passe ne respecte pas les critères de sécurité.");
    }

    @Test
    void testFindByUsername_ExistingUser_ReturnsUser() {
        // Given
        User user = createAndSaveTestUser("john", "john@example.com");

        // When
        User found = userService.findByUsername("john");

        // Then
        assertNotNull(found);
        assertEquals("john", found.getUsername());
    }

    @Test
    void testFindByUsername_NonExistingUser_ReturnsNull() {
        // When
        User found = userService.findByUsername("nonexistent");

        // Then
        assertNull(found);
    }

    @Test
    void testFindByEmail_ExistingEmail_ReturnsUser() {
        // Given
        createAndSaveTestUser("jane", "jane@example.com");

        // When
        User found = userService.findByEmail("jane@example.com");

        // Then
        assertNotNull(found);
        assertEquals("jane@example.com", found.getEmail());
    }

    @Test
    void testUpdateUserProfile_ValidData_Success() {
        // Given
        createAndSaveTestUser("olduser", "old@example.com");

        // When
        userService.updateUserProfile("olduser", "newuser", "new@example.com");

        // Then
        User updated = userRepository.findByUsername("newuser");
        assertNotNull(updated);
        assertEquals("newuser", updated.getUsername());
        assertEquals("new@example.com", updated.getEmail());
    }

    @Test
    void testUpdateUserProfile_DuplicateUsername_ThrowsException() {
        // Given
        createAndSaveTestUser("user1", "user1@example.com");
        createAndSaveTestUser("user2", "user2@example.com");

        // When/Then
        assertThrows(IllegalArgumentException.class,
                () -> userService.updateUserProfile("user1", "user2", "user1@example.com"),
                "Ce nom d'utilisateur est déjà pris.");
    }

    @Test
    void testChangeUserPassword_ValidOldPassword_Success() {
        // Given
        User user = createAndSaveTestUser("testuser", "test@example.com");

        // When
        userService.changeUserPassword("testuser", "Password123!", "NewPassword123!", "NewPassword123!");

        // Then
        User updated = userRepository.findByUsername("testuser");
        assertTrue(passwordEncoder.matches("NewPassword123!", updated.getPassword()));
    }

    @Test
    void testChangeUserPassword_WrongOldPassword_ThrowsException() {
        // Given
        createAndSaveTestUser("testuser", "test@example.com");

        // When/Then
        assertThrows(IllegalArgumentException.class,
                () -> userService.changeUserPassword("testuser", "WrongPassword!", "NewPassword123!", "NewPassword123!"),
                "L'ancien mot de passe est incorrect.");
    }

    @Test
    void testChangeUserPassword_PasswordMismatch_ThrowsException() {
        // Given
        createAndSaveTestUser("testuser", "test@example.com");

        // When/Then
        assertThrows(IllegalArgumentException.class,
                () -> userService.changeUserPassword("testuser", "Password123!", "NewPassword123!", "DifferentPassword123!"),
                "Le nouveau mot de passe et sa confirmation ne correspondent pas.");
    }

    @Test
    void testFindAllUsers_ReturnsAllUsers() {
        // Given
        createAndSaveTestUser("user1", "user1@example.com");
        createAndSaveTestUser("user2", "user2@example.com");
        createAndSaveTestUser("user3", "user3@example.com");

        // When
        List<User> users = userService.findAllUsers();

        // Then
        assertTrue(users.size() >= 3);
    }

    @Test
    void testFindUserById_ExistingId_ReturnsUser() {
        // Given
        User user = createAndSaveTestUser("testuser", "test@example.com");

        // When
        Optional<User> found = userService.findUserById(user.getId());

        // Then
        assertTrue(found.isPresent());
        assertEquals("testuser", found.get().getUsername());
    }

    @Test
    void testDeleteUser_ExistingUser_Success() {
        // Given
        User user = createAndSaveTestUser("deleteMe", "delete@example.com");
        Long userId = user.getId();

        // When
        userService.deleteUser(userId);

        // Then
        Optional<User> deleted = userRepository.findById(userId);
        assertFalse(deleted.isPresent());
    }

    @Test
    void testCountUsers_ReturnsCorrectCount() {
        // Given
        long initialCount = userService.countUsers();
        createAndSaveTestUser("user1", "user1@example.com");
        createAndSaveTestUser("user2", "user2@example.com");

        // When
        long newCount = userService.countUsers();

        // Then
        assertEquals(initialCount + 2, newCount);
    }

    @Test
    void testUpdateProfileDetails_ValidData_Success() {
        // Given
        createAndSaveTestUser("testuser", "test@example.com");

        // When
        userService.updateProfileDetails("testuser", "John", "Doe", "Test bio");

        // Then
        User updated = userRepository.findByUsername("testuser");
        assertEquals("John", updated.getFirstName());
        assertEquals("Doe", updated.getLastName());
        assertEquals("Test bio", updated.getBio());
    }

    @Test
    void testUpdateProfileDetails_TooLongBio_ThrowsException() {
        // Given
        createAndSaveTestUser("testuser", "test@example.com");
        String longBio = "a".repeat(501);  // Plus de 500 caractères

        // When/Then
        assertThrows(IllegalArgumentException.class,
                () -> userService.updateProfileDetails("testuser", "John", "Doe", longBio),
                "La biographie ne peut pas dépasser 500 caractères.");
    }

    /**
     * Méthode utilitaire pour créer et sauvegarder un utilisateur de test
     */
    private User createAndSaveTestUser(String username, String email) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword("Password123!");
        userService.saveUser(user);
        return userRepository.findByUsername(username);
    }
}
