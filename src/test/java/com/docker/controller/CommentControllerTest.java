package com.docker.controller;

import com.docker.entity.CommentType;
import com.docker.entity.User;
import com.docker.service.CommentService;
import com.docker.service.LoginAttemptService;
import com.docker.service.SocialLinkService;
import com.docker.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests unitaires pour CommentController
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentService commentService;

    @MockBean
    private UserService userService;

    // Mock des services utilisés par @ControllerAdvice
    @MockBean
    private SocialLinkService socialLinkService;

    @MockBean
    private LoginAttemptService loginAttemptService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testAddConcertComment_WithValidContent_ReturnsSuccess() throws Exception {
        when(userService.findByUsername("testuser")).thenReturn(testUser);

        mockMvc.perform(post("/comments/concert/1")
                .param("content", "Great concert!")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").exists());

        verify(commentService, times(1)).createComment(eq("Great concert!"), eq(testUser), eq(CommentType.CONCERT), eq(1L));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testAddConcertComment_WithEmptyContent_ReturnsBadRequest() throws Exception {
        when(userService.findByUsername("testuser")).thenReturn(testUser);

        mockMvc.perform(post("/comments/concert/1")
                .param("content", "")
                .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(false));

        verify(commentService, never()).createComment(anyString(), any(User.class), any(CommentType.class), anyLong());
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testAddConcertComment_WithTooLongContent_ReturnsBadRequest() throws Exception {
        when(userService.findByUsername("testuser")).thenReturn(testUser);
        String longContent = "a".repeat(1001); // Plus de 1000 caractères

        mockMvc.perform(post("/comments/concert/1")
                .param("content", longContent)
                .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(false));

        verify(commentService, never()).createComment(anyString(), any(User.class), any(CommentType.class), anyLong());
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testAddArticleComment_WithValidContent_ReturnsSuccess() throws Exception {
        when(userService.findByUsername("testuser")).thenReturn(testUser);

        mockMvc.perform(post("/comments/article/1")
                .param("content", "Nice article!")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true));

        verify(commentService, times(1)).createComment(eq("Nice article!"), eq(testUser), eq(CommentType.ARTICLE), eq(1L));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testAddVideoComment_WithValidContent_ReturnsSuccess() throws Exception {
        when(userService.findByUsername("testuser")).thenReturn(testUser);

        mockMvc.perform(post("/comments/video/1")
                .param("content", "Amazing video!")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true));

        verify(commentService, times(1)).createComment(eq("Amazing video!"), eq(testUser), eq(CommentType.VIDEO), eq(1L));
    }
}
