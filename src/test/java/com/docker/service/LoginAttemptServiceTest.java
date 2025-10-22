package com.docker.service;

import com.docker.dto.SecurityStatsDTO;
import com.docker.dto.SuspiciousIpDTO;
import com.docker.entity.LoginAttempt;
import com.docker.repository.LoginAttemptRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires pour LoginAttemptService
 * Service critique de sécurité - protection anti-brute force
 */
@ExtendWith(MockitoExtension.class)
class LoginAttemptServiceTest {

    @Mock
    private LoginAttemptRepository loginAttemptRepository;

    @InjectMocks
    private LoginAttemptService loginAttemptService;

    private LoginAttempt successfulAttempt;
    private LoginAttempt failedAttempt;

    @BeforeEach
    void setUp() {
        successfulAttempt = new LoginAttempt("testuser", "192.168.1.1", true);
        successfulAttempt.setId(1L);
        successfulAttempt.setUserAgent("Mozilla/5.0");
        successfulAttempt.setAttemptTime(LocalDateTime.now());

        failedAttempt = new LoginAttempt("testuser", "192.168.1.1", false, "Mauvais mot de passe");
        failedAttempt.setId(2L);
        failedAttempt.setUserAgent("Mozilla/5.0");
        failedAttempt.setAttemptTime(LocalDateTime.now());
    }

    // ========================================
    // Tests pour recordSuccessfulLogin
    // ========================================

    @Test
    void testRecordSuccessfulLogin_ShouldResetFailedAttempts() {
        // Given
        String username = "testuser";
        String ipAddress = "192.168.1.1";
        String userAgent = "Mozilla/5.0";

        when(loginAttemptRepository.save(any(LoginAttempt.class))).thenReturn(successfulAttempt);

        // When
        loginAttemptService.recordSuccessfulLogin(username, ipAddress, userAgent);

        // Then
        verify(loginAttemptRepository, times(1)).deleteByUsernameAndSuccessFalse(username);
        verify(loginAttemptRepository, times(1)).save(any(LoginAttempt.class));
    }

    // ========================================
    // Tests pour recordFailedLogin
    // ========================================

    @Test
    void testRecordFailedLogin_ShouldSaveAttempt() {
        // Given
        String username = "testuser";
        String ipAddress = "192.168.1.1";
        String userAgent = "Mozilla/5.0";
        String failureReason = "Mauvais mot de passe";

        when(loginAttemptRepository.save(any(LoginAttempt.class))).thenReturn(failedAttempt);
        when(loginAttemptRepository.countFailedAttemptsByUsernameSince(anyString(), any(LocalDateTime.class)))
                .thenReturn(1L);

        // When
        loginAttemptService.recordFailedLogin(username, ipAddress, userAgent, failureReason);

        // Then
        verify(loginAttemptRepository, times(1)).save(any(LoginAttempt.class));
        verify(loginAttemptRepository, times(1)).countFailedAttemptsByUsernameSince(anyString(), any(LocalDateTime.class));
    }

    @Test
    void testRecordFailedLogin_ShouldLogWarningWhenMaxAttemptsReached() {
        // Given
        String username = "testuser";
        String ipAddress = "192.168.1.1";
        String userAgent = "Mozilla/5.0";
        String failureReason = "Mauvais mot de passe";

        when(loginAttemptRepository.save(any(LoginAttempt.class))).thenReturn(failedAttempt);
        when(loginAttemptRepository.countFailedAttemptsByUsernameSince(anyString(), any(LocalDateTime.class)))
                .thenReturn(5L); // MAX_ATTEMPTS_PER_USERNAME = 5

        // When
        loginAttemptService.recordFailedLogin(username, ipAddress, userAgent, failureReason);

        // Then
        verify(loginAttemptRepository, times(1)).save(any(LoginAttempt.class));
    }

    // ========================================
    // Tests pour isBlocked
    // ========================================

    @Test
    void testIsBlocked_ShouldReturnFalse_WhenLessThan5Attempts() {
        // Given
        String username = "testuser";
        when(loginAttemptRepository.countFailedAttemptsByUsernameSince(anyString(), any(LocalDateTime.class)))
                .thenReturn(3L);

        // When
        boolean blocked = loginAttemptService.isBlocked(username);

        // Then
        assertFalse(blocked);
        verify(loginAttemptRepository, times(1)).countFailedAttemptsByUsernameSince(eq(username), any(LocalDateTime.class));
    }

    @Test
    void testIsBlocked_ShouldReturnTrue_When5OrMoreAttempts() {
        // Given
        String username = "testuser";
        when(loginAttemptRepository.countFailedAttemptsByUsernameSince(anyString(), any(LocalDateTime.class)))
                .thenReturn(5L);

        // When
        boolean blocked = loginAttemptService.isBlocked(username);

        // Then
        assertTrue(blocked);
        verify(loginAttemptRepository, times(1)).countFailedAttemptsByUsernameSince(eq(username), any(LocalDateTime.class));
    }

    // ========================================
    // Tests pour isIpBlocked
    // ========================================

    @Test
    void testIsIpBlocked_ShouldReturnFalse_WhenLessThan10Attempts() {
        // Given
        String ipAddress = "192.168.1.1";
        when(loginAttemptRepository.countFailedAttemptsByIpSince(anyString(), any(LocalDateTime.class)))
                .thenReturn(7L);

        // When
        boolean blocked = loginAttemptService.isIpBlocked(ipAddress);

        // Then
        assertFalse(blocked);
        verify(loginAttemptRepository, times(1)).countFailedAttemptsByIpSince(eq(ipAddress), any(LocalDateTime.class));
    }

    @Test
    void testIsIpBlocked_ShouldReturnTrue_When10OrMoreAttempts() {
        // Given
        String ipAddress = "192.168.1.1";
        when(loginAttemptRepository.countFailedAttemptsByIpSince(anyString(), any(LocalDateTime.class)))
                .thenReturn(10L);

        // When
        boolean blocked = loginAttemptService.isIpBlocked(ipAddress);

        // Then
        assertTrue(blocked);
        verify(loginAttemptRepository, times(1)).countFailedAttemptsByIpSince(eq(ipAddress), any(LocalDateTime.class));
    }

    // ========================================
    // Tests pour getFailedAttemptsByUsername
    // ========================================

    @Test
    void testGetFailedAttemptsByUsername_ShouldReturnCount() {
        // Given
        String username = "testuser";
        when(loginAttemptRepository.countFailedAttemptsByUsernameSince(anyString(), any(LocalDateTime.class)))
                .thenReturn(3L);

        // When
        long count = loginAttemptService.getFailedAttemptsByUsername(username);

        // Then
        assertEquals(3L, count);
        verify(loginAttemptRepository, times(1)).countFailedAttemptsByUsernameSince(eq(username), any(LocalDateTime.class));
    }

    // ========================================
    // Tests pour getRemainingAttempts
    // ========================================

    @Test
    void testGetRemainingAttempts_ShouldReturn2_When3Failed() {
        // Given
        String username = "testuser";
        when(loginAttemptRepository.countFailedAttemptsByUsernameSince(anyString(), any(LocalDateTime.class)))
                .thenReturn(3L);

        // When
        int remaining = loginAttemptService.getRemainingAttempts(username);

        // Then
        assertEquals(2, remaining); // 5 - 3 = 2
    }

    @Test
    void testGetRemainingAttempts_ShouldReturn0_WhenMaxReached() {
        // Given
        String username = "testuser";
        when(loginAttemptRepository.countFailedAttemptsByUsernameSince(anyString(), any(LocalDateTime.class)))
                .thenReturn(5L);

        // When
        int remaining = loginAttemptService.getRemainingAttempts(username);

        // Then
        assertEquals(0, remaining);
    }

    @Test
    void testGetRemainingAttempts_ShouldReturn0_WhenExceeded() {
        // Given
        String username = "testuser";
        when(loginAttemptRepository.countFailedAttemptsByUsernameSince(anyString(), any(LocalDateTime.class)))
                .thenReturn(7L);

        // When
        int remaining = loginAttemptService.getRemainingAttempts(username);

        // Then
        assertEquals(0, remaining); // Math.max(0, 5 - 7) = 0
    }

    // ========================================
    // Tests pour getLastSuccessfulLogin
    // ========================================

    @Test
    void testGetLastSuccessfulLogin_ShouldReturnAttempt() {
        // Given
        String username = "testuser";
        when(loginAttemptRepository.findFirstByUsernameAndSuccessTrueOrderByAttemptTimeDesc(username))
                .thenReturn(successfulAttempt);

        // When
        LoginAttempt result = loginAttemptService.getLastSuccessfulLogin(username);

        // Then
        assertNotNull(result);
        assertEquals(successfulAttempt.getId(), result.getId());
        assertTrue(result.isSuccess());
    }

    @Test
    void testGetLastSuccessfulLogin_ShouldReturnNull_WhenNoSuccessfulLogin() {
        // Given
        String username = "newuser";
        when(loginAttemptRepository.findFirstByUsernameAndSuccessTrueOrderByAttemptTimeDesc(username))
                .thenReturn(null);

        // When
        LoginAttempt result = loginAttemptService.getLastSuccessfulLogin(username);

        // Then
        assertNull(result);
    }

    // ========================================
    // Tests pour getLoginHistory
    // ========================================

    @Test
    void testGetLoginHistory_ShouldReturnList() {
        // Given
        String username = "testuser";
        int days = 30;
        List<LoginAttempt> expected = Arrays.asList(successfulAttempt, failedAttempt);

        when(loginAttemptRepository.findByUsernameAndAttemptTimeGreaterThanEqualOrderByAttemptTimeDesc(
                eq(username), any(LocalDateTime.class)))
                .thenReturn(expected);

        // When
        List<LoginAttempt> result = loginAttemptService.getLoginHistory(username, days);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(loginAttemptRepository, times(1)).findByUsernameAndAttemptTimeGreaterThanEqualOrderByAttemptTimeDesc(
                eq(username), any(LocalDateTime.class));
    }

    // ========================================
    // Tests pour getLoginHistoryPaginated
    // ========================================

    @Test
    void testGetLoginHistoryPaginated_ShouldReturnPage() {
        // Given
        String username = "testuser";
        int days = 30;
        Pageable pageable = PageRequest.of(0, 10);
        List<LoginAttempt> attempts = Arrays.asList(successfulAttempt, failedAttempt);
        Page<LoginAttempt> expected = new PageImpl<>(attempts, pageable, attempts.size());

        when(loginAttemptRepository.findByUsernameAndAttemptTimeGreaterThanEqual(
                eq(username), any(LocalDateTime.class), eq(pageable)))
                .thenReturn(expected);

        // When
        Page<LoginAttempt> result = loginAttemptService.getLoginHistoryPaginated(username, days, pageable);

        // Then
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals(0, result.getNumber());
        verify(loginAttemptRepository, times(1)).findByUsernameAndAttemptTimeGreaterThanEqual(
                eq(username), any(LocalDateTime.class), eq(pageable));
    }

    // ========================================
    // Tests pour cleanupOldAttempts
    // ========================================

    @Test
    void testCleanupOldAttempts_ShouldDeleteOldRecords() {
        // When
        loginAttemptService.cleanupOldAttempts();

        // Then
        verify(loginAttemptRepository, times(1)).deleteByAttemptTimeBefore(any(LocalDateTime.class));
    }

    // ========================================
    // Tests pour getBlockMessage
    // ========================================

    @Test
    void testGetBlockMessage_ShouldReturnBlockedMessage_WhenBlocked() {
        // Given
        String username = "testuser";
        when(loginAttemptRepository.countFailedAttemptsByUsernameSince(anyString(), any(LocalDateTime.class)))
                .thenReturn(5L);

        // When
        String message = loginAttemptService.getBlockMessage(username);

        // Then
        assertNotNull(message);
        assertTrue(message.contains("temporairement bloqué"));
        assertTrue(message.contains("5 tentatives"));
        assertTrue(message.contains("15 minutes"));
    }

    @Test
    void testGetBlockMessage_ShouldReturnRemainingMessage_WhenNotBlocked() {
        // Given
        String username = "testuser";
        when(loginAttemptRepository.countFailedAttemptsByUsernameSince(anyString(), any(LocalDateTime.class)))
                .thenReturn(3L);

        // When
        String message = loginAttemptService.getBlockMessage(username);

        // Then
        assertNotNull(message);
        assertTrue(message.contains("Il vous reste 2 tentative(s)"));
    }

    // ========================================
    // Tests pour getSecurityStats
    // ========================================

    @Test
    void testGetSecurityStats_ShouldReturnCompleteStats() {
        // Given
        List<LoginAttempt> recentAttempts = Arrays.asList(successfulAttempt, failedAttempt);
        List<LoginAttempt> last24hAttempts = new ArrayList<>(recentAttempts);

        when(loginAttemptRepository.count()).thenReturn(100L);
        when(loginAttemptRepository.countBySuccess(true)).thenReturn(80L);
        when(loginAttemptRepository.countBySuccess(false)).thenReturn(20L);
        when(loginAttemptRepository.findTop50ByOrderByAttemptTimeDesc()).thenReturn(recentAttempts);
        when(loginAttemptRepository.findByAttemptTimeGreaterThanEqualOrderByAttemptTimeDesc(any(LocalDateTime.class)))
                .thenReturn(last24hAttempts);
        when(loginAttemptRepository.findDistinctUsernamesBySuccessFalseAndAttemptTimeGreaterThanEqual(any(LocalDateTime.class)))
                .thenReturn(Arrays.asList("testuser"));
        when(loginAttemptRepository.countFailedAttemptsByUsernameSince(anyString(), any(LocalDateTime.class)))
                .thenReturn(2L);

        // When
        SecurityStatsDTO stats = loginAttemptService.getSecurityStats();

        // Then
        assertNotNull(stats);
        assertEquals(100L, stats.getTotalAttempts());
        assertEquals(80L, stats.getSuccessfulAttempts());
        assertEquals(20L, stats.getFailedAttempts());
        assertNotNull(stats.getRecentAttempts());
        assertEquals(2, stats.getRecentAttempts().size());
    }

    // ========================================
    // Tests pour countCurrentlyBlockedAccounts
    // ========================================

    @Test
    void testCountCurrentlyBlockedAccounts_ShouldReturnCount() {
        // Given
        List<String> usernames = Arrays.asList("user1", "user2", "user3");
        when(loginAttemptRepository.findDistinctUsernamesBySuccessFalseAndAttemptTimeGreaterThanEqual(any(LocalDateTime.class)))
                .thenReturn(usernames);
        when(loginAttemptRepository.countFailedAttemptsByUsernameSince(eq("user1"), any(LocalDateTime.class)))
                .thenReturn(5L); // Bloqué
        when(loginAttemptRepository.countFailedAttemptsByUsernameSince(eq("user2"), any(LocalDateTime.class)))
                .thenReturn(3L); // Non bloqué
        when(loginAttemptRepository.countFailedAttemptsByUsernameSince(eq("user3"), any(LocalDateTime.class)))
                .thenReturn(6L); // Bloqué

        // When
        long count = loginAttemptService.countCurrentlyBlockedAccounts();

        // Then
        assertEquals(2L, count); // user1 et user3 sont bloqués
    }

    // ========================================
    // Tests pour getSuspiciousIps
    // ========================================

    @Test
    void testGetSuspiciousIps_ShouldReturnListOfSuspiciousIps() {
        // Given
        LoginAttempt attempt1 = new LoginAttempt("user1", "192.168.1.100", false, "Bad password");
        attempt1.setAttemptTime(LocalDateTime.now());
        LoginAttempt attempt2 = new LoginAttempt("user2", "192.168.1.100", false, "Bad password");
        attempt2.setAttemptTime(LocalDateTime.now().minusMinutes(5));
        LoginAttempt attempt3 = new LoginAttempt("user3", "192.168.1.100", false, "Bad password");
        attempt3.setAttemptTime(LocalDateTime.now().minusMinutes(10));
        LoginAttempt attempt4 = new LoginAttempt("user4", "192.168.1.100", true);
        attempt4.setAttemptTime(LocalDateTime.now().minusMinutes(15));

        List<LoginAttempt> attempts = Arrays.asList(attempt1, attempt2, attempt3, attempt4);

        when(loginAttemptRepository.findByAttemptTimeGreaterThanEqualOrderByAttemptTimeDesc(any(LocalDateTime.class)))
                .thenReturn(attempts);

        // When
        List<SuspiciousIpDTO> suspiciousIps = loginAttemptService.getSuspiciousIps();

        // Then
        assertNotNull(suspiciousIps);
        assertEquals(1, suspiciousIps.size());
        SuspiciousIpDTO dto = suspiciousIps.get(0);
        assertEquals("192.168.1.100", dto.getIpAddress());
        assertEquals(3L, dto.getFailedAttempts()); // 3 échecs
        assertEquals(1L, dto.getSuccessfulAttempts()); // 1 succès
    }

    @Test
    void testGetSuspiciousIps_ShouldFilterIpsWithLessThan3Failures() {
        // Given
        LoginAttempt attempt1 = new LoginAttempt("user1", "192.168.1.100", false, "Bad password");
        attempt1.setAttemptTime(LocalDateTime.now());
        LoginAttempt attempt2 = new LoginAttempt("user2", "192.168.1.100", false, "Bad password");
        attempt2.setAttemptTime(LocalDateTime.now().minusMinutes(5));

        List<LoginAttempt> attempts = Arrays.asList(attempt1, attempt2);

        when(loginAttemptRepository.findByAttemptTimeGreaterThanEqualOrderByAttemptTimeDesc(any(LocalDateTime.class)))
                .thenReturn(attempts);

        // When
        List<SuspiciousIpDTO> suspiciousIps = loginAttemptService.getSuspiciousIps();

        // Then
        assertTrue(suspiciousIps.isEmpty()); // Moins de 3 échecs, donc pas suspecte
    }

    // ========================================
    // Tests pour getDailyStats
    // ========================================

    @Test
    void testGetDailyStats_ShouldReturnStatsForRequestedDays() {
        // Given
        int days = 7;
        List<LoginAttempt> attempts = Arrays.asList(successfulAttempt, failedAttempt);

        when(loginAttemptRepository.findByAttemptTimeGreaterThanEqualOrderByAttemptTimeDesc(any(LocalDateTime.class)))
                .thenReturn(attempts);

        // When
        var dailyStats = loginAttemptService.getDailyStats(days);

        // Then
        assertNotNull(dailyStats);
        assertEquals(days, dailyStats.size()); // Une entrée pour chaque jour
    }
}
