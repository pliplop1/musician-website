package com.docker.service;

import com.docker.repository.CommentRepository;
import com.docker.repository.MessageRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private NotificationService notificationService;

    @Test
    void testGetUnreadMessagesCount_ReturnsCount() {
        when(messageRepository.countByReadFalse()).thenReturn(5L);

        int count = notificationService.getUnreadMessagesCount();

        assertEquals(5, count);
        verify(messageRepository, times(1)).countByReadFalse();
    }

    @Test
    void testGetPendingCommentsCount_ReturnsCount() {
        when(commentRepository.countByApprovedFalse()).thenReturn(3L);

        int count = notificationService.getPendingCommentsCount();

        assertEquals(3, count);
        verify(commentRepository, times(1)).countByApprovedFalse();
    }

    @Test
    void testGetTotalPendingCount_ReturnsSumOfUnreadAndPending() {
        when(messageRepository.countByReadFalse()).thenReturn(5L);
        when(commentRepository.countByApprovedFalse()).thenReturn(3L);

        int total = notificationService.getTotalPendingCount();

        assertEquals(8, total);
        verify(messageRepository, times(1)).countByReadFalse();
        verify(commentRepository, times(1)).countByApprovedFalse();
    }

    @Test
    void testGetNotificationStats_ReturnsAllStats() {
        when(messageRepository.countByReadFalse()).thenReturn(5L);
        when(commentRepository.countByApprovedFalse()).thenReturn(3L);

        Map<String, Integer> stats = notificationService.getNotificationStats();

        assertNotNull(stats);
        assertEquals(3, stats.size());
        assertEquals(5, stats.get("unreadMessages"));
        assertEquals(3, stats.get("pendingComments"));
        assertEquals(8, stats.get("total"));
        verify(messageRepository, times(2)).countByReadFalse(); // Called twice: once for unread, once for total
        verify(commentRepository, times(2)).countByApprovedFalse(); // Called twice: once for pending, once for total
    }

    @Test
    void testGetNotificationStats_WithZeroCounts() {
        when(messageRepository.countByReadFalse()).thenReturn(0L);
        when(commentRepository.countByApprovedFalse()).thenReturn(0L);

        Map<String, Integer> stats = notificationService.getNotificationStats();

        assertNotNull(stats);
        assertEquals(0, stats.get("unreadMessages"));
        assertEquals(0, stats.get("pendingComments"));
        assertEquals(0, stats.get("total"));
    }
}
