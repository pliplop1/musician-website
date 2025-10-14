
package com.docker.controller;

import com.docker.entity.Role;
import com.docker.entity.User;
import com.docker.repository.RoleRepository;
import com.docker.service.LoginAttemptService;
import com.docker.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdminUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private RoleRepository roleRepository;

    @MockBean
    private LoginAttemptService loginAttemptService;

    private User testUser;
    private Role userRole;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");

        userRole = new Role();
        userRole.setId(1L);
        userRole.setName("ROLE_USER");
    }

    // ========================================================================
    // TESTS AFFICHAGE LISTE UTILISATEURS
    // ========================================================================

    @Test
    @WithMockUser(roles = "ADMIN")
    void testShowUserManagement_AsAdmin_ReturnsViewWithUsers() throws Exception {
        Page<User> userPage = new PageImpl<>(Collections.singletonList(testUser));
        when(userService.searchUsers(any(), any(Pageable.class))).thenReturn(userPage);

        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/user-management"))
                .andExpect(model().attributeExists("userPage"))
                .andExpect(model().attribute("userPage", userPage));

        verify(userService, times(1)).searchUsers(any(), any(Pageable.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    void testShowUserManagement_AsUser_Returns403() throws Exception {
        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isForbidden());
    }

    // ========================================================================
    // TESTS AFFICHAGE FORMULAIRE D'ÉDITION
    // ========================================================================

    @Test
    @WithMockUser(roles = "ADMIN")
    void testShowUserEditForm_AsAdmin_ReturnsEditView() throws Exception {
        when(userService.findUserById(1L)).thenReturn(Optional.of(testUser));
        when(roleRepository.findAll()).thenReturn(Collections.singletonList(userRole));

        mockMvc.perform(get("/admin/users/edit/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/edit-user"))
                .andExpect(model().attribute("user", testUser))
                .andExpect(model().attributeExists("allRoles"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testShowUserEditForm_InvalidId_RedirectsWithError() throws Exception {
        when(userService.findUserById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/admin/users/edit/999"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/users"))
                .andExpect(flash().attributeExists("errorMessage"));
    }

    // ========================================================================
    // TESTS MISE À JOUR UTILISATEUR
    // ========================================================================

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateUser_AsAdmin_Success() throws Exception {
        when(roleRepository.findById(anyLong())).thenReturn(Optional.of(userRole));
        doNothing().when(userService).updateUserFromAdmin(anyLong(), anyString(), anyString(), any());

        mockMvc.perform(post("/admin/users/update").with(csrf())
                .flashAttr("user", testUser)
                .param("roleId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/users"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(userService, times(1)).updateUserFromAdmin(anyLong(), anyString(), anyString(), any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateUser_NoRole_ReturnsError() throws Exception {
        mockMvc.perform(post("/admin/users/update").with(csrf())
                .flashAttr("user", testUser))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/users/edit/1"))
                .andExpect(flash().attributeExists("errorMessage"));

        verify(userService, never()).updateUserFromAdmin(anyLong(), anyString(), anyString(), any());
    }

    // ========================================================================
    // TESTS SUPPRESSION UTILISATEUR
    // ========================================================================

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteUser_AsAdmin_Success() throws Exception {
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(post("/admin/users/delete/1").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/users"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(userService, times(1)).deleteUser(1L);
    }

    // ========================================================================
    // TESTS SUPPRESSION EN MASSE
    // ========================================================================

    @Test
    @WithMockUser(roles = "ADMIN")
    void testBulkDeleteUsers_AsAdmin_Success() throws Exception {
        doNothing().when(userService).deleteUser(anyLong());

        mockMvc.perform(post("/admin/users/bulk-delete").with(csrf())
                .param("userIds", "1", "2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/users"))
                .andExpect(flash().attribute("successMessage", "2 utilisateur(s) supprimé(s) avec succès !"));

        verify(userService, times(2)).deleteUser(anyLong());
    }
}
