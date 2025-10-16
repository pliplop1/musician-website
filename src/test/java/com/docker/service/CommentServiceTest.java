package com.docker.service;

import com.docker.entity.Comment;
import com.docker.entity.CommentType;
import com.docker.entity.User;
import com.docker.repository.CommentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentService commentService;

    private User testUser;
    private Comment testComment;
    private Comment testComment2;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        testComment = new Comment();
        testComment.setId(1L);
        testComment.setContent("Test comment 1");
        testComment.setUser(testUser);
        testComment.setApproved(true);
        testComment.setType(CommentType.ARTICLE);
        testComment.setTargetId(1L);

        testComment2 = new Comment();
        testComment2.setId(2L);
        testComment2.setContent("Test comment 2");
        testComment2.setUser(testUser);
        testComment2.setApproved(false);
        testComment2.setType(CommentType.VIDEO);
        testComment2.setTargetId(2L);
    }

    @Test
    void testGetApprovedComments_ReturnsApprovedComments() {
        List<Comment> approvedComments = Arrays.asList(testComment);
        when(commentRepository.findApprovedCommentsWithUser(CommentType.ARTICLE, 1L))
                .thenReturn(approvedComments);

        List<Comment> result = commentService.getApprovedComments(CommentType.ARTICLE, 1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).getApproved());
        verify(commentRepository, times(1)).findApprovedCommentsWithUser(CommentType.ARTICLE, 1L);
    }

    @Test
    void testGetAllCommentsForTarget_ReturnsAllComments() {
        List<Comment> allComments = Arrays.asList(testComment, testComment2);
        when(commentRepository.findByTypeAndTargetIdOrderByCreatedAtDesc(CommentType.ARTICLE, 1L))
                .thenReturn(allComments);

        List<Comment> result = commentService.getAllCommentsForTarget(CommentType.ARTICLE, 1L);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(commentRepository, times(1)).findByTypeAndTargetIdOrderByCreatedAtDesc(CommentType.ARTICLE, 1L);
    }

    @Test
    void testGetPendingComments_ReturnsPendingComments() {
        testComment2.setApproved(false);
        List<Comment> pendingComments = Arrays.asList(testComment2);
        when(commentRepository.findPendingCommentsWithUser()).thenReturn(pendingComments);

        List<Comment> result = commentService.getPendingComments();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertFalse(result.get(0).getApproved());
        verify(commentRepository, times(1)).findPendingCommentsWithUser();
    }

    @Test
    void testGetUserComments_ReturnsUserComments() {
        List<Comment> userComments = Arrays.asList(testComment, testComment2);
        when(commentRepository.findByUserIdOrderByCreatedAtDesc(1L)).thenReturn(userComments);

        List<Comment> result = commentService.getUserComments(1L);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(commentRepository, times(1)).findByUserIdOrderByCreatedAtDesc(1L);
    }

    @Test
    void testCountApprovedComments_ReturnsCount() {
        when(commentRepository.countByTypeAndTargetIdAndApprovedTrue(CommentType.ARTICLE, 1L))
                .thenReturn(5L);

        Long count = commentService.countApprovedComments(CommentType.ARTICLE, 1L);

        assertEquals(5L, count);
        verify(commentRepository, times(1)).countByTypeAndTargetIdAndApprovedTrue(CommentType.ARTICLE, 1L);
    }

    @Test
    void testCreateComment_SavesCommentAsUnapproved() {
        Comment newComment = new Comment("New comment", testUser, CommentType.ARTICLE, 1L);
        newComment.setApproved(false);
        when(commentRepository.save(any(Comment.class))).thenReturn(newComment);

        Comment result = commentService.createComment("New comment", testUser, CommentType.ARTICLE, 1L);

        assertNotNull(result);
        assertFalse(result.getApproved());
        assertEquals("New comment", result.getContent());
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void testApproveComment_WhenCommentExists_ApprovesIt() {
        testComment.setApproved(false);
        when(commentRepository.findById(1L)).thenReturn(Optional.of(testComment));
        when(commentRepository.save(any(Comment.class))).thenReturn(testComment);

        commentService.approveComment(1L);

        assertTrue(testComment.getApproved());
        verify(commentRepository, times(1)).findById(1L);
        verify(commentRepository, times(1)).save(testComment);
    }

    @Test
    void testApproveComment_WhenCommentNotFound_DoesNothing() {
        when(commentRepository.findById(999L)).thenReturn(Optional.empty());

        commentService.approveComment(999L);

        verify(commentRepository, times(1)).findById(999L);
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void testDeleteComment_DeletesComment() {
        doNothing().when(commentRepository).deleteById(1L);

        commentService.deleteComment(1L);

        verify(commentRepository, times(1)).deleteById(1L);
    }

    @Test
    void testFindById_WhenCommentExists_ReturnsComment() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(testComment));

        Optional<Comment> result = commentService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(testComment.getId(), result.get().getId());
        verify(commentRepository, times(1)).findById(1L);
    }

    @Test
    void testFindById_WhenCommentNotFound_ReturnsEmpty() {
        when(commentRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Comment> result = commentService.findById(999L);

        assertFalse(result.isPresent());
        verify(commentRepository, times(1)).findById(999L);
    }

    @Test
    void testGetAllComments_ReturnsAllComments() {
        List<Comment> allComments = Arrays.asList(testComment, testComment2);
        when(commentRepository.findAllCommentsWithUser()).thenReturn(allComments);

        List<Comment> result = commentService.getAllComments();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(commentRepository, times(1)).findAllCommentsWithUser();
    }
}
