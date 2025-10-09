package com.docker.service;

import com.docker.repository.CommentRepository;
import com.docker.repository.MessageRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class NotificationService {

    private final MessageRepository messageRepository;
    private final CommentRepository commentRepository;

    public NotificationService(MessageRepository messageRepository, CommentRepository commentRepository) {
        this.messageRepository = messageRepository;
        this.commentRepository = commentRepository;
    }

    /**
     * Récupère le nombre total d'éléments en attente de modération
     */
    public int getTotalPendingCount() {
        return getUnreadMessagesCount() + getPendingCommentsCount();
    }

    /**
     * Compte les messages non lus
     */
    public int getUnreadMessagesCount() {
        return (int) messageRepository.countByReadFalse();
    }

    /**
     * Compte les commentaires en attente d'approbation
     */
    public int getPendingCommentsCount() {
        return (int) commentRepository.countByApprovedFalse();
    }

    /**
     * Récupère toutes les statistiques de notifications
     */
    public Map<String, Integer> getNotificationStats() {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("unreadMessages", getUnreadMessagesCount());
        stats.put("pendingComments", getPendingCommentsCount());
        stats.put("total", getTotalPendingCount());
        return stats;
    }
}
