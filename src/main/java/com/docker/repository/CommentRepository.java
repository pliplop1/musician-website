package com.docker.repository;

import com.docker.entity.Comment;
import com.docker.entity.CommentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    // Trouver tous les commentaires approuvés pour un type et un targetId avec JOIN FETCH
    @Query("SELECT c FROM Comment c JOIN FETCH c.user WHERE c.type = :type AND c.targetId = :targetId AND c.approved = true ORDER BY c.createdAt DESC")
    List<Comment> findApprovedCommentsWithUser(@Param("type") CommentType type, @Param("targetId") Long targetId);

    // Trouver tous les commentaires (approuvés ou non) pour un type et un targetId
    List<Comment> findByTypeAndTargetIdOrderByCreatedAtDesc(CommentType type, Long targetId);

    // Trouver tous les commentaires en attente de modération avec JOIN FETCH
    @Query("SELECT c FROM Comment c JOIN FETCH c.user WHERE c.approved = false ORDER BY c.createdAt DESC")
    List<Comment> findPendingCommentsWithUser();

    // Trouver tous les commentaires avec JOIN FETCH
    @Query("SELECT c FROM Comment c JOIN FETCH c.user ORDER BY c.createdAt DESC")
    List<Comment> findAllCommentsWithUser();

    // Trouver tous les commentaires d'un utilisateur
    List<Comment> findByUserIdOrderByCreatedAtDesc(Long userId);

    // Compter les commentaires approuvés pour un target
    Long countByTypeAndTargetIdAndApprovedTrue(CommentType type, Long targetId);

    // Compter les commentaires en attente d'approbation
    long countByApprovedFalse();
}
