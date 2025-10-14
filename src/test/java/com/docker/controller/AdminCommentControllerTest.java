
package com.docker.controller;

import com.docker.entity.Comment;
import com.docker.entity.User;
import com.docker.service.CommentService;
import com.docker.service.LoginAttemptService;
import com.docker.service.SocialLinkService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdminCommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentService commentService;

    @MockBean
    private SocialLinkService socialLinkService;
    @MockBean
    private LoginAttemptService loginAttemptService;

    private Comment pendingComment;
    private Comment approvedComment;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        pendingComment = new Comment();
        pendingComment.setId(1L);
        pendingComment.setUser(testUser);
        pendingComment.setContent("This is a pending comment.");
        pendingComment.setApproved(false);

        approvedComment = new Comment();
        approvedComment.setId(2L);
        approvedComment.setUser(testUser);
        approvedComment.setContent("This is an approved comment.");
        approvedComment.setApproved(true);
    }

    // ========================================================================
    // TESTS AFFICHAGE PAGE GESTION COMMENTAIRES
    // ========================================================================

    @Test
    @WithMockUser(roles = "ADMIN")
    void testShowCommentManagementPage_AsAdmin_ReturnsView() throws Exception {
        when(commentService.getPendingComments()).thenReturn(Collections.singletonList(pendingComment));
        when(commentService.getAllComments()).thenReturn(Arrays.asList(pendingComment, approvedComment));

        mockMvc.perform(get("/admin/comments"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/comments"))
                .andExpect(model().attributeExists("pendingComments", "allComments"))
                .andExpect(model().attribute("pendingComments", Collections.singletonList(pendingComment)))
                .andExpect(model().attribute("allComments", Arrays.asList(pendingComment, approvedComment)));

        verify(commentService, times(1)).getPendingComments();
        verify(commentService, times(1)).getAllComments();
    }

    @Test
    @WithMockUser(roles = "USER")
    void testShowCommentManagementPage_AsUser_Returns403() throws Exception {
        mockMvc.perform(get("/admin/comments"))
                .andExpect(status().isForbidden());

        verify(commentService, never()).getAllComments();
    }

    // ========================================================================
    // TESTS APPROBATION COMMENTAIRE
    // ========================================================================

    @Test
    @WithMockUser(roles = "ADMIN")
    void testApproveComment_AsAdmin_Success() throws Exception {
        doNothing().when(commentService).approveComment(1L);

        mockMvc.perform(post("/admin/comments/approve/1").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/comments"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(commentService, times(1)).approveComment(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testApproveComment_NonExistent_ReturnsError() throws Exception {
        doThrow(new IllegalArgumentException("Comment not found")).when(commentService).approveComment(999L);

        mockMvc.perform(post("/admin/comments/approve/999").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/comments"))
                .andExpect(flash().attributeExists("errorMessage"));

        verify(commentService, times(1)).approveComment(999L);
    }

    // ========================================================================
    // TESTS SUPPRESSION COMMENTAIRE
    // ========================================================================

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteComment_AsAdmin_Success() throws Exception {
        doNothing().when(commentService).deleteComment(1L);

        mockMvc.perform(post("/admin/comments/delete/1").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/comments"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(commentService, times(1)).deleteComment(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteComment_NonExistent_ReturnsError() throws Exception {
        doThrow(new IllegalArgumentException("Comment not found")).when(commentService).deleteComment(999L);

        mockMvc.perform(post("/admin/comments/delete/999").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/comments"))
                .andExpect(flash().attributeExists("errorMessage"));

        verify(commentService, times(1)).deleteComment(999L);
    }

    // ========================================================================
    // TESTS ACTIONS EN MASSE
    // ========================================================================

    @Test
    @WithMockUser(roles = "ADMIN")
    void testBulkApproveComments_AsAdmin_Success() throws Exception {
        mockMvc.perform(post("/admin/comments/bulk-approve").with(csrf())
                .param("commentIds", "1", "2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/comments"))
                .andExpect(flash().attribute("successMessage", "2 commentaire(s) approuvé(s) avec succès !"));

        verify(commentService, times(1)).approveComment(1L);
        verify(commentService, times(1)).approveComment(2L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testBulkDeleteComments_AsAdmin_Success() throws Exception {
        mockMvc.perform(post("/admin/comments/bulk-delete").with(csrf())
                .param("commentIds", "1", "2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/comments"))
                .andExpect(flash().attribute("successMessage", "2 commentaire(s) supprimé(s) avec succès !"));

        verify(commentService, times(1)).deleteComment(1L);
        verify(commentService, times(1)).deleteComment(2L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testBulkActions_NoIds_ReturnsError() throws Exception {
        mockMvc.perform(post("/admin/comments/bulk-approve").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/comments"))
                .andExpect(flash().attribute("errorMessage", "Aucun commentaire sélectionné"));

        mockMvc.perform(post("/admin/comments/bulk-delete").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/comments"))
                .andExpect(flash().attribute("errorMessage", "Aucun commentaire sélectionné"));

        verify(commentService, never()).approveComment(anyLong());
        verify(commentService, never()).deleteComment(anyLong());
    }
}
