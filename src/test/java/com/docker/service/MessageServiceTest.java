package com.docker.service;

import com.docker.entity.Message;
import com.docker.repository.MessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

    @Mock
    private MessageRepository messageRepository;

    @InjectMocks
    private MessageService messageService;

    private Message testMessage1;
    private Message testMessage2;

    @BeforeEach
    void setUp() {
        testMessage1 = new Message();
        testMessage1.setId(1L);
        testMessage1.setName("John Doe");
        testMessage1.setEmail("john@example.com");
        testMessage1.setContent("Test message 1");
        testMessage1.setRead(false);

        testMessage2 = new Message();
        testMessage2.setId(2L);
        testMessage2.setName("Jane Doe");
        testMessage2.setEmail("jane@example.com");
        testMessage2.setContent("Test message 2");
        testMessage2.setRead(true);
    }

    @Test
    void testSaveMessage_SetsTimestampAndReadStatus() {
        Message newMessage = new Message();
        newMessage.setName("Test User");
        newMessage.setEmail("test@example.com");
        newMessage.setContent("Test content");

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        when(messageRepository.save(any(Message.class))).thenReturn(newMessage);

        Message result = messageService.saveMessage(newMessage);

        verify(messageRepository, times(1)).save(messageCaptor.capture());
        Message capturedMessage = messageCaptor.getValue();

        assertNotNull(capturedMessage.getTimestamp());
        assertFalse(capturedMessage.isRead());
        assertEquals("Test User", capturedMessage.getName());
    }

    @Test
    void testFindAllMessages_ReturnsAllMessages() {
        List<Message> messages = Arrays.asList(testMessage1, testMessage2);
        when(messageRepository.findAll()).thenReturn(messages);

        List<Message> result = messageService.findAllMessages();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(messageRepository, times(1)).findAll();
    }

    @Test
    void testDeleteMessage_DeletesMessage() {
        doNothing().when(messageRepository).deleteById(1L);

        messageService.deleteMessage(1L);

        verify(messageRepository, times(1)).deleteById(1L);
    }

    @Test
    void testCountMessages_ReturnsCount() {
        when(messageRepository.count()).thenReturn(5L);

        long count = messageService.countMessages();

        assertEquals(5L, count);
        verify(messageRepository, times(1)).count();
    }

    @Test
    void testMarkAllAsRead_MarksUnreadMessagesAsRead() {
        testMessage1.setRead(false);
        testMessage2.setRead(false);
        List<Message> messages = Arrays.asList(testMessage1, testMessage2);

        when(messageRepository.findAll()).thenReturn(messages);
        when(messageRepository.saveAll(anyList())).thenReturn(messages);

        messageService.markAllAsRead();

        assertTrue(testMessage1.isRead());
        assertTrue(testMessage2.isRead());
        verify(messageRepository, times(1)).findAll();
        verify(messageRepository, times(1)).saveAll(messages);
    }

    @Test
    void testMarkAllAsRead_DoesNotModifyAlreadyReadMessages() {
        testMessage1.setRead(true);
        testMessage2.setRead(true);
        List<Message> messages = Arrays.asList(testMessage1, testMessage2);

        when(messageRepository.findAll()).thenReturn(messages);
        when(messageRepository.saveAll(anyList())).thenReturn(messages);

        messageService.markAllAsRead();

        assertTrue(testMessage1.isRead());
        assertTrue(testMessage2.isRead());
        verify(messageRepository, times(1)).findAll();
        verify(messageRepository, times(1)).saveAll(messages);
    }

    @Test
    void testToggleReadStatus_TogglesFromUnreadToRead() {
        testMessage1.setRead(false);
        when(messageRepository.findById(1L)).thenReturn(Optional.of(testMessage1));
        when(messageRepository.save(any(Message.class))).thenReturn(testMessage1);

        messageService.toggleReadStatus(1L);

        assertTrue(testMessage1.isRead());
        verify(messageRepository, times(1)).findById(1L);
        verify(messageRepository, times(1)).save(testMessage1);
    }

    @Test
    void testToggleReadStatus_TogglesFromReadToUnread() {
        testMessage1.setRead(true);
        when(messageRepository.findById(1L)).thenReturn(Optional.of(testMessage1));
        when(messageRepository.save(any(Message.class))).thenReturn(testMessage1);

        messageService.toggleReadStatus(1L);

        assertFalse(testMessage1.isRead());
        verify(messageRepository, times(1)).findById(1L);
        verify(messageRepository, times(1)).save(testMessage1);
    }

    @Test
    void testToggleReadStatus_ThrowsExceptionWhenMessageNotFound() {
        when(messageRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            messageService.toggleReadStatus(999L);
        });

        verify(messageRepository, times(1)).findById(999L);
        verify(messageRepository, never()).save(any(Message.class));
    }
}
