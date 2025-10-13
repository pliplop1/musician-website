package com.docker.repository;

import com.docker.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour UserRepository avec @DataJpaTest
 * Tests rapides de la couche de persistance avec H2 in-memory
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testFindByUsername_ExistingUser_ReturnsUser() {
        // Given
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("password123");
        entityManager.persist(user);
        entityManager.flush();

        // When
        User found = userRepository.findByUsername("testuser");

        // Then
        assertNotNull(found);
        assertEquals("testuser", found.getUsername());
    }

    @Test
    void testFindByUsername_NonExistingUser_ReturnsNull() {
        // When
        User found = userRepository.findByUsername("nonexistent");

        // Then
        assertNull(found);
    }

    @Test
    void testFindByEmail_ExistingEmail_ReturnsUser() {
        // Given
        User user = new User();
        user.setUsername("john");
        user.setEmail("john@example.com");
        user.setPassword("password123");
        entityManager.persist(user);
        entityManager.flush();

        // When
        User found = userRepository.findByEmail("john@example.com");

        // Then
        assertNotNull(found);
        assertEquals("john@example.com", found.getEmail());
    }

    @Test
    void testSaveUser_Success() {
        // Given
        User user = new User();
        user.setUsername("newuser");
        user.setEmail("new@example.com");
        user.setPassword("password123");

        // When
        User saved = userRepository.save(user);

        // Then
        assertNotNull(saved.getId());
        assertEquals("newuser", saved.getUsername());
    }

    @Test
    void testDeleteUser_Success() {
        // Given
        User user = new User();
        user.setUsername("deleteMe");
        user.setEmail("delete@example.com");
        user.setPassword("password123");
        user = entityManager.persist(user);
        entityManager.flush();
        Long userId = user.getId();

        // When
        userRepository.deleteById(userId);

        // Then
        User deleted = entityManager.find(User.class, userId);
        assertNull(deleted);
    }
}
