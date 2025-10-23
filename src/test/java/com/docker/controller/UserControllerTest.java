package com.docker.controller;

import com.docker.entity.LoginAttempt;
import com.docker.entity.User;
import com.docker.entity.UserBadge;
import com.docker.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests unitaires pour UserController
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private BadgeService badgeService;

    @MockBean
    private DataExportService dataExportService;

    @MockBean
    private LoginAttemptService loginAttemptService;

    // Mock des services utilisés par @ControllerAdvice
    @MockBean
    private CommentService commentService;

    @MockBean
    private SocialLinkService socialLinkService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedPassword");
        testUser.setCreatedAt(LocalDateTime.now().minusDays(30));
        testUser.setFavoriteConcerts(new HashSet<>());
    }

    // Tests pour /user/profile
    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testShowProfile_ReturnsProfileView() throws Exception {
        when(userService.findByUsername("testuser")).thenReturn(testUser);
        when(badgeService.getUserBadges(testUser)).thenReturn(new ArrayList<>());
        when(badgeService.countUserBadges(testUser)).thenReturn(0L);
        when(loginAttemptService.getLastSuccessfulLogin("testuser")).thenReturn(null);

        mockMvc.perform(get("/user/profile"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/profile"))
                .andExpect(model().attributeExists("username", "user", "daysSinceRegistration", "userBadges", "badgeCount"));

        verify(userService, atLeastOnce()).findByUsername("testuser");
        verify(badgeService, times(1)).getUserBadges(testUser);
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testShowProfile_WithBadges() throws Exception {
        List<UserBadge> badges = new ArrayList<>();

        when(userService.findByUsername("testuser")).thenReturn(testUser);
        when(badgeService.getUserBadges(testUser)).thenReturn(badges);
        when(badgeService.countUserBadges(testUser)).thenReturn(0L);
        when(loginAttemptService.getLastSuccessfulLogin("testuser")).thenReturn(null);

        mockMvc.perform(get("/user/profile"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/profile"));
    }

    // Tests pour /user/login-history
    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testShowLoginHistory_ReturnsView() throws Exception {
        List<LoginAttempt> loginHistory = new ArrayList<>();
        Page<LoginAttempt> loginHistoryPage = new PageImpl<>(loginHistory);

        when(userService.findByUsername("testuser")).thenReturn(testUser);
        when(loginAttemptService.getLoginHistoryPaginated(eq("testuser"), eq(30), any(Pageable.class)))
                .thenReturn(loginHistoryPage);

        mockMvc.perform(get("/user/login-history"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/login-history"))
                .andExpect(model().attributeExists("user", "loginHistoryPage", "currentPage", "totalPages"));

        verify(loginAttemptService, times(1)).getLoginHistoryPaginated(eq("testuser"), eq(30), any(Pageable.class));
    }

    // Tests pour /user/profile/edit
    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testShowEditProfileForm_ReturnsView() throws Exception {
        when(userService.findByUsername("testuser")).thenReturn(testUser);

        mockMvc.perform(get("/user/profile/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/edit-profile"))
                .andExpect(model().attributeExists("user"));
    }

    // Tests pour /user/profile/update
    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testUpdateProfile_WithValidData_RedirectsToProfile() throws Exception {
        when(userService.findByUsername("testuser")).thenReturn(testUser);
        when(userService.findByUsername("newusername")).thenReturn(testUser);
        doNothing().when(userService).updateUserProfile(anyString(), anyString(), anyString());

        mockMvc.perform(post("/user/profile/update")
                .param("username", "newusername")
                .param("email", "newemail@example.com")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/profile"))
                .andExpect(flash().attributeExists("successMessage"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testUpdateProfile_WithInvalidData_RedirectsWithError() throws Exception {
        doThrow(new IllegalArgumentException("Email déjà utilisé"))
                .when(userService).updateUserProfile(anyString(), anyString(), anyString());

        mockMvc.perform(post("/user/profile/update")
                .param("username", "testuser")
                .param("email", "duplicate@example.com")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/profile/edit"))
                .andExpect(flash().attributeExists("errorMessage"));
    }

    // Tests pour /user/profile/change-password
    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testShowChangePasswordForm_ReturnsView() throws Exception {
        mockMvc.perform(get("/user/profile/change-password"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/change-password"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testHandleChangePassword_WithValidData_RedirectsToLogin() throws Exception {
        doNothing().when(userService).changeUserPassword(anyString(), anyString(), anyString(), anyString());

        mockMvc.perform(post("/user/profile/change-password")
                .param("oldPassword", "oldpass")
                .param("newPassword", "newpass123")
                .param("confirmPassword", "newpass123")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"))
                .andExpect(flash().attributeExists("successMessage"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testHandleChangePassword_WithInvalidData_RedirectsWithError() throws Exception {
        doThrow(new IllegalArgumentException("Ancien mot de passe incorrect"))
                .when(userService).changeUserPassword(anyString(), anyString(), anyString(), anyString());

        mockMvc.perform(post("/user/profile/change-password")
                .param("oldPassword", "wrongpass")
                .param("newPassword", "newpass123")
                .param("confirmPassword", "newpass123")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/profile/change-password"))
                .andExpect(flash().attributeExists("errorMessage"));
    }

    // Tests pour /user/favorites/add/{concertId}
    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testAddFavorite_RedirectsBack() throws Exception {
        doNothing().when(userService).addFavoriteConcert("testuser", 1L);

        mockMvc.perform(post("/user/favorites/add/1")
                .header("Referer", "/concerts")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/concerts"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(userService, times(1)).addFavoriteConcert("testuser", 1L);
    }

    // Tests pour /user/favorites/remove/{concertId}
    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testRemoveFavorite_RedirectsBack() throws Exception {
        doNothing().when(userService).removeFavoriteConcert("testuser", 1L);

        mockMvc.perform(post("/user/favorites/remove/1")
                .header("Referer", "/user/profile")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/profile"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(userService, times(1)).removeFavoriteConcert("testuser", 1L);
    }

    // Tests pour /user/profile/details
    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testShowProfileDetailsForm_ReturnsView() throws Exception {
        when(userService.findByUsername("testuser")).thenReturn(testUser);

        mockMvc.perform(get("/user/profile/details"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/edit-profile-details"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testUpdateProfileDetails_WithValidData_RedirectsToProfile() throws Exception {
        doNothing().when(userService).updateProfileDetails(anyString(), anyString(), anyString(), anyString());

        mockMvc.perform(post("/user/profile/details")
                .param("firstName", "John")
                .param("lastName", "Doe")
                .param("bio", "Test bio")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/profile"))
                .andExpect(flash().attributeExists("successMessage"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testUpdateProfileDetails_WithError_RedirectsWithError() throws Exception {
        doThrow(new RuntimeException("Error")).when(userService)
                .updateProfileDetails(anyString(), anyString(), anyString(), anyString());

        mockMvc.perform(post("/user/profile/details")
                .param("firstName", "John")
                .param("lastName", "Doe")
                .param("bio", "Test bio")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/profile/details"))
                .andExpect(flash().attributeExists("errorMessage"));
    }

    // Tests pour /user/profile/upload-avatar
    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testUploadAvatar_WithValidFile_RedirectsToProfile() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "avatar.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "test image content".getBytes()
        );

        doNothing().when(userService).updateAvatar(anyString(), any());

        mockMvc.perform(multipart("/user/profile/upload-avatar")
                .file(file)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/profile"))
                .andExpect(flash().attributeExists("successMessage"));
    }

    // Tests pour /user/profile/delete-avatar
    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testDeleteAvatar_RedirectsToProfile() throws Exception {
        doNothing().when(userService).deleteAvatar(anyString());

        mockMvc.perform(post("/user/profile/delete-avatar")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/profile"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(userService, times(1)).deleteAvatar("testuser");
    }

    // Tests pour /user/export-data (RGPD)
    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testExportUserData_ReturnsJsonFile() throws Exception {
        String jsonData = "{\"username\":\"testuser\",\"email\":\"test@example.com\"}";
        String filename = "testuser_data_export.json";

        when(dataExportService.exportUserDataByUsername("testuser")).thenReturn(jsonData);
        when(dataExportService.generateExportFilename("testuser")).thenReturn(filename);

        mockMvc.perform(get("/user/export-data"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string(jsonData))
                .andExpect(header().string("Content-Disposition", "form-data; name=\"attachment\"; filename=\"" + filename + "\""));

        verify(dataExportService, times(1)).exportUserDataByUsername("testuser");
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testExportUserData_WithError_ReturnsError() throws Exception {
        when(dataExportService.exportUserDataByUsername("testuser"))
                .thenThrow(new RuntimeException("Export error"));

        mockMvc.perform(get("/user/export-data"))
                .andExpect(status().isInternalServerError());
    }

    // Tests pour /user/delete-account
    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testDeleteAccount_WithValidPassword_RedirectsToHome() throws Exception {
        when(userService.findByUsername("testuser")).thenReturn(testUser);
        when(userService.verifyPassword(testUser, "correctPassword")).thenReturn(true);
        doNothing().when(userService).deleteUserAccountCompletely(1L);

        mockMvc.perform(post("/user/delete-account")
                .param("confirmPassword", "correctPassword")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(userService, times(1)).deleteUserAccountCompletely(1L);
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testDeleteAccount_WithInvalidPassword_RedirectsWithError() throws Exception {
        when(userService.findByUsername("testuser")).thenReturn(testUser);
        when(userService.verifyPassword(testUser, "wrongPassword")).thenReturn(false);

        mockMvc.perform(post("/user/delete-account")
                .param("confirmPassword", "wrongPassword")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/delete-account"))
                .andExpect(flash().attributeExists("errorMessage"));

        verify(userService, never()).deleteUserAccountCompletely(anyLong());
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testDeleteAccount_WithError_RedirectsWithError() throws Exception {
        when(userService.findByUsername("testuser")).thenReturn(testUser);
        when(userService.verifyPassword(testUser, "correctPassword")).thenReturn(true);
        doThrow(new RuntimeException("Delete error")).when(userService).deleteUserAccountCompletely(1L);

        mockMvc.perform(post("/user/delete-account")
                .param("confirmPassword", "correctPassword")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/delete-account"))
                .andExpect(flash().attributeExists("errorMessage"));
    }
}
