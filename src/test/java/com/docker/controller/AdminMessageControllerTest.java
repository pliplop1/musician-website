package com.docker.controller;

import com.docker.entity.Message;
import com.docker.service.*;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests unitaires pour AdminMessageController
 * Teste la gestion admin des messages (affichage, suppression, marquage lu/non lu)
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdminMessageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MessageService messageService;

    @MockBean
    private CommentService commentService;

    @MockBean
    private SocialLinkService socialLinkService;

    @MockBean
    private LoginAttemptService loginAttemptService;

    private List<Message> testMessages;

    @BeforeEach
    void setUp() {
        Message message1 = new Message();
        message1.setId(1L);
        message1.setName("John Doe");
        message1.setEmail("john@example.com");
        message1.setContent("Test message 1");
        message1.setRead(false);

        Message message2 = new Message();
        message2.setId(2L);
        message2.setName("Jane Smith");
        message2.setEmail("jane@example.com");
        message2.setContent("Test message 2");
        message2.setRead(true);

        testMessages = Arrays.asList(message1, message2);
    }

    // ========================================================================
    // TESTS AFFICHAGE PAGE GESTION MESSAGES
    // ========================================================================

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testShowMessageManagementPage_AsAdmin_ReturnsView() throws Exception {
        when(messageService.findAllMessages()).thenReturn(testMessages);

        mockMvc.perform(get("/admin/messages"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/message-management"))
                .andExpect(model().attributeExists("messages"))
                .andExpect(model().attribute("messages", testMessages));

        verify(messageService, times(1)).findAllMessages();
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testShowMessageManagementPage_AsUser_Returns403() throws Exception {
        mockMvc.perform(get("/admin/messages"))
                .andExpect(status().isForbidden());

        verify(messageService, never()).findAllMessages();
    }

    // ========================================================================
    // TESTS SUPPRESSION MESSAGE
    // ========================================================================

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testDeleteMessage_AsAdmin_Success() throws Exception {
        doNothing().when(messageService).deleteMessage(1L);

        mockMvc.perform(post("/admin/messages/delete/1")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/messages"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(messageService, times(1)).deleteMessage(1L);
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testDeleteMessage_AsUser_Returns403() throws Exception {
        mockMvc.perform(post("/admin/messages/delete/1")
                .with(csrf()))
                .andExpect(status().isForbidden());

        verify(messageService, never()).deleteMessage(anyLong());
    }

    // ========================================================================
    // TESTS SUPPRESSION EN MASSE
    // ========================================================================

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testBulkDeleteMessages_AsAdmin_Success() throws Exception {
        doNothing().when(messageService).deleteMessage(anyLong());

        mockMvc.perform(post("/admin/messages/bulk-delete")
                .param("messageIds", "1", "2", "3")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/messages"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(messageService, times(3)).deleteMessage(anyLong());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testBulkDeleteMessages_AsUser_Returns403() throws Exception {
        mockMvc.perform(post("/admin/messages/bulk-delete")
                .param("messageIds", "1", "2")
                .with(csrf()))
                .andExpect(status().isForbidden());

        verify(messageService, never()).deleteMessage(anyLong());
    }

    // ========================================================================
    // TESTS BASCULER STATUT LU/NON LU
    // ========================================================================

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testToggleReadStatus_AsAdmin_Success() throws Exception {
        doNothing().when(messageService).toggleReadStatus(1L);

        mockMvc.perform(post("/admin/messages/toggle-read/1")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/messages"));

        verify(messageService, times(1)).toggleReadStatus(1L);
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testToggleReadStatus_AsUser_Returns403() throws Exception {
        mockMvc.perform(post("/admin/messages/toggle-read/1")
                .with(csrf()))
                .andExpect(status().isForbidden());

        verify(messageService, never()).toggleReadStatus(anyLong());
    }

    // ========================================================================
    // TESTS MARQUER TOUS COMME LUS
    // ========================================================================

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testMarkAllMessagesAsRead_AsAdmin_Success() throws Exception {
        doNothing().when(messageService).markAllAsRead();

        mockMvc.perform(post("/admin/messages/mark-all-read")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/messages"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(messageService, times(1)).markAllAsRead();
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testMarkAllMessagesAsRead_AsUser_Returns403() throws Exception {
        mockMvc.perform(post("/admin/messages/mark-all-read")
                .with(csrf()))
                .andExpect(status().isForbidden());

        verify(messageService, never()).markAllAsRead();
    }
}
