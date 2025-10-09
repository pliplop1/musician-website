package com.docker.service;

import com.docker.entity.Comment;
import com.docker.entity.CommentType;
import com.docker.entity.User;
import com.docker.repository.CommentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CommentService {

    private final CommentRepository commentRepository;

    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    /**
     * Récupère tous les commentaires approuvés pour un contenu spécifique
     */
    public List<Comment> getApprovedComments(CommentType type, Long targetId) {
        return commentRepository.findApprovedCommentsWithUser(type, targetId);
    }

    /**
     * Récupère tous les commentaires (approuvés ou non) pour un contenu spécifique
     */
    public List<Comment> getAllCommentsForTarget(CommentType type, Long targetId) {
        return commentRepository.findByTypeAndTargetIdOrderByCreatedAtDesc(type, targetId);
    }

    /**
     * Récupère tous les commentaires en attente de modération
     */
    public List<Comment> getPendingComments() {
        return commentRepository.findPendingCommentsWithUser();
    }

    /**
     * Récupère tous les commentaires d'un utilisateur
     */
    public List<Comment> getUserComments(Long userId) {
        return commentRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    /**
     * Compte le nombre de commentaires approuvés pour un contenu
     */
    public Long countApprovedComments(CommentType type, Long targetId) {
        return commentRepository.countByTypeAndTargetIdAndApprovedTrue(type, targetId);
    }

    /**
     * Crée un nouveau commentaire
     */
    @Transactional
    public Comment createComment(String content, User user, CommentType type, Long targetId) {
        Comment comment = new Comment(content, user, type, targetId);
        // Par défaut, les commentaires ne sont pas approuvés (modération)
        comment.setApproved(false);
        return commentRepository.save(comment);
    }

    /**
     * Approuve un commentaire
     */
    @Transactional
    public void approveComment(Long commentId) {
        Optional<Comment> commentOpt = commentRepository.findById(commentId);
        if (commentOpt.isPresent()) {
            Comment comment = commentOpt.get();
            comment.setApproved(true);
            commentRepository.save(comment);
        }
    }

    /**
     * Supprime un commentaire
     */
    @Transactional
    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
    }

    /**
     * Récupère un commentaire par son ID
     */
    public Optional<Comment> findById(Long id) {
        return commentRepository.findById(id);
    }

    /**
     * Récupère tous les commentaires
     */
    public List<Comment> getAllComments() {
        return commentRepository.findAllCommentsWithUser();
    }
}
