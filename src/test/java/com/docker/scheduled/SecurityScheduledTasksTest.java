package com.docker.scheduled;

import com.docker.service.LoginAttemptService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

/**
 * Tests pour les tâches planifiées de sécurité
 * Améliore la couverture du package scheduled de 36% à >50%
 */
@ExtendWith(MockitoExtension.class)
class SecurityScheduledTasksTest {

    @Mock
    private LoginAttemptService loginAttemptService;

    private SecurityScheduledTasks securityScheduledTasks;

    @BeforeEach
    void setUp() {
        securityScheduledTasks = new SecurityScheduledTasks(loginAttemptService);
    }

    @Test
    void testCleanupOldLoginAttempts_Success() {
        // Arrange
        doNothing().when(loginAttemptService).cleanupOldAttempts();

        // Act
        securityScheduledTasks.cleanupOldLoginAttempts();

        // Assert
        verify(loginAttemptService, times(1)).cleanupOldAttempts();
    }

    @Test
    void testCleanupOldLoginAttempts_HandlesException() {
        // Arrange
        doThrow(new RuntimeException("Database error")).when(loginAttemptService).cleanupOldAttempts();

        // Act - should not throw exception
        securityScheduledTasks.cleanupOldLoginAttempts();

        // Assert
        verify(loginAttemptService, times(1)).cleanupOldAttempts();
    }

    @Test
    void testCleanupOldLoginAttempts_MultipleExecutions() {
        // Arrange
        doNothing().when(loginAttemptService).cleanupOldAttempts();

        // Act - simulate multiple scheduled executions
        securityScheduledTasks.cleanupOldLoginAttempts();
        securityScheduledTasks.cleanupOldLoginAttempts();
        securityScheduledTasks.cleanupOldLoginAttempts();

        // Assert
        verify(loginAttemptService, times(3)).cleanupOldAttempts();
    }
}
