package com.docker.controller;

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
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests unitaires pour RegistrationController
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RegistrationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    // Mock des services utilisés par @ControllerAdvice
    @MockBean
    private CommentService commentService;

    @MockBean
    private SocialLinkService socialLinkService;

    @MockBean
    private LoginAttemptService loginAttemptService;

    @BeforeEach
    void setUp() {
        reset(userService);
    }

    @Test
    @WithAnonymousUser
    void testShowRegistrationForm_ReturnsRegisterView() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    @WithAnonymousUser
    void testShowRegistrationForm_UserAttributeIsPresent() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("user", org.hamcrest.Matchers.instanceOf(User.class)));
    }

    @Test
    @WithAnonymousUser
    void testRegisterUser_WithValidData_RedirectsToLogin() throws Exception {
        when(userService.findByUsername("newuser")).thenReturn(null);
        when(userService.findByEmail("newuser@example.com")).thenReturn(null);

        mockMvc.perform(post("/register")
                .param("username", "newuser")
                .param("email", "newuser@example.com")
                .param("password", "password123")
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(userService, times(1)).saveUser(any(User.class));
    }

    @Test
    @WithAnonymousUser
    void testRegisterUser_WithExistingUsername_ReturnsError() throws Exception {
        User existingUser = new User();
        existingUser.setUsername("existinguser");
        when(userService.findByUsername("existinguser")).thenReturn(existingUser);

        mockMvc.perform(post("/register")
                .param("username", "existinguser")
                .param("email", "newemail@example.com")
                .param("password", "password123")
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("errorMessage"));

        verify(userService, never()).saveUser(any(User.class));
    }

    @Test
    @WithAnonymousUser
    void testRegisterUser_WithExistingEmail_ReturnsError() throws Exception {
        User existingUser = new User();
        existingUser.setEmail("existing@example.com");
        when(userService.findByUsername("newuser")).thenReturn(null);
        when(userService.findByEmail("existing@example.com")).thenReturn(existingUser);

        mockMvc.perform(post("/register")
                .param("username", "newuser")
                .param("email", "existing@example.com")
                .param("password", "password123")
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("errorMessage"));

        verify(userService, never()).saveUser(any(User.class));
    }

    @Test
    @WithAnonymousUser
    void testRegisterUser_WithValidationError_ReturnsRegisterView() throws Exception {
        // Test avec un username trop court (moins de 3 caractères)
        mockMvc.perform(post("/register")
                .param("username", "ab")
                .param("email", "test@example.com")
                .param("password", "password123")
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("register"));

        verify(userService, never()).saveUser(any(User.class));
    }

    @Test
    @WithAnonymousUser
    void testRegisterUser_WithInvalidEmail_ReturnsRegisterView() throws Exception {
        mockMvc.perform(post("/register")
                .param("username", "validuser")
                .param("email", "invalidemail")
                .param("password", "password123")
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("register"));

        verify(userService, never()).saveUser(any(User.class));
    }

    @Test
    @WithAnonymousUser
    void testRegisterUser_WithShortPassword_ReturnsRegisterView() throws Exception {
        mockMvc.perform(post("/register")
                .param("username", "validuser")
                .param("email", "valid@example.com")
                .param("password", "12345")
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("register"));

        verify(userService, never()).saveUser(any(User.class));
    }

    @Test
    @WithAnonymousUser
    void testRegisterUser_WithEmptyUsername_ReturnsRegisterView() throws Exception {
        mockMvc.perform(post("/register")
                .param("username", "")
                .param("email", "test@example.com")
                .param("password", "password123")
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("register"));

        verify(userService, never()).saveUser(any(User.class));
    }

    @Test
    @WithAnonymousUser
    void testRegisterUser_ServiceThrowsException_ReturnsError() throws Exception {
        when(userService.findByUsername("newuser")).thenReturn(null);
        when(userService.findByEmail("test@example.com")).thenReturn(null);
        doThrow(new IllegalArgumentException("Le mot de passe est trop faible"))
                .when(userService).saveUser(any(User.class));

        mockMvc.perform(post("/register")
                .param("username", "newuser")
                .param("email", "test@example.com")
                .param("password", "password123")
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("errorMessage"));
    }

    @Test
    @WithAnonymousUser
    void testRegisterUser_WithoutCsrfToken_ReturnsForbidden() throws Exception {
        mockMvc.perform(post("/register")
                .param("username", "newuser")
                .param("email", "test@example.com")
                .param("password", "password123"))
                .andExpect(status().isForbidden());
    }
}
