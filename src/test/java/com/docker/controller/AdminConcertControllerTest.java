package com.docker.controller;

import com.docker.entity.Concert;
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

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests unitaires pour AdminConcertController
 * Teste la gestion admin des concerts (affichage, création, modification, suppression)
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdminConcertControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ConcertService concertService;

    @MockBean
    private CommentService commentService;

    @MockBean
    private SocialLinkService socialLinkService;

    @MockBean
    private LoginAttemptService loginAttemptService;

    private List<Concert> testConcerts;
    private Concert testConcert;

    @BeforeEach
    void setUp() {
        Concert concert1 = new Concert();
        concert1.setId(1L);
        concert1.setLocation("Salle Pleyel, Paris");
        concert1.setDate(LocalDate.now().plusDays(30));
        concert1.setDescription("Grand concert de musique classique");

        Concert concert2 = new Concert();
        concert2.setId(2L);
        concert2.setLocation("Olympia, Paris");
        concert2.setDate(LocalDate.now().plusDays(60));
        concert2.setDescription("Concert acoustique intimiste");

        testConcerts = Arrays.asList(concert1, concert2);
        testConcert = concert1;
    }

    // ========================================================================
    // TESTS AFFICHAGE PAGE GESTION CONCERTS
    // ========================================================================

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testShowConcertManagementPage_AsAdmin_ReturnsView() throws Exception {
        when(concertService.findAllConcerts()).thenReturn(testConcerts);

        mockMvc.perform(get("/admin/concerts"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/concert-management"))
                .andExpect(model().attributeExists("concerts"))
                .andExpect(model().attributeExists("concert"))
                .andExpect(model().attribute("concerts", testConcerts));

        verify(concertService, times(1)).findAllConcerts();
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testShowConcertManagementPage_AsUser_Returns403() throws Exception {
        mockMvc.perform(get("/admin/concerts"))
                .andExpect(status().isForbidden());

        verify(concertService, never()).findAllConcerts();
    }

    // ========================================================================
    // TESTS AJOUT CONCERT
    // ========================================================================

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testSaveConcert_AsAdmin_Success() throws Exception {
        when(concertService.saveConcert(any(Concert.class))).thenReturn(testConcert);

        mockMvc.perform(post("/admin/concerts/add")
                .param("location", "Zenith de Paris")
                .param("date", LocalDate.now().plusDays(15).toString())
                .param("description", "Concert exceptionnel")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/dashboard"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(concertService, times(1)).saveConcert(any(Concert.class));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testSaveConcert_AsUser_Returns403() throws Exception {
        mockMvc.perform(post("/admin/concerts/add")
                .param("location", "Zenith de Paris")
                .param("date", LocalDate.now().plusDays(15).toString())
                .param("description", "Concert exceptionnel")
                .with(csrf()))
                .andExpect(status().isForbidden());

        verify(concertService, never()).saveConcert(any());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testSaveConcert_ValidationErrors_ReturnsFormWithErrors() throws Exception {
        when(concertService.findAllConcerts()).thenReturn(testConcerts);

        // Location trop court (moins de 3 caractères) et date dans le passé
        mockMvc.perform(post("/admin/concerts/add")
                .param("location", "AB")  // Trop court
                .param("date", LocalDate.now().minusDays(10).toString())  // Date passée
                .param("description", "Description valide")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/concert-management"))
                .andExpect(model().attributeExists("concerts"))
                .andExpect(model().attributeHasFieldErrors("concert", "location", "date"));

        verify(concertService, never()).saveConcert(any());
        verify(concertService, times(1)).findAllConcerts();
    }

    // ========================================================================
    // TESTS SUPPRESSION CONCERT
    // ========================================================================

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testDeleteConcert_AsAdmin_Success() throws Exception {
        doNothing().when(concertService).deleteConcert(1L);

        mockMvc.perform(post("/admin/concerts/delete/1")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/dashboard"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(concertService, times(1)).deleteConcert(1L);
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testDeleteConcert_AsUser_Returns403() throws Exception {
        mockMvc.perform(post("/admin/concerts/delete/1")
                .with(csrf()))
                .andExpect(status().isForbidden());

        verify(concertService, never()).deleteConcert(any());
    }

    // ========================================================================
    // TESTS AFFICHAGE FORMULAIRE EDITION CONCERT
    // ========================================================================

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testShowEditConcertForm_AsAdmin_Success() throws Exception {
        when(concertService.findConcertById(1L)).thenReturn(Optional.of(testConcert));

        mockMvc.perform(get("/admin/concerts/edit/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/edit-concert"))
                .andExpect(model().attributeExists("concert"))
                .andExpect(model().attribute("concert", testConcert));

        verify(concertService, times(1)).findConcertById(1L);
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testShowEditConcertForm_AsUser_Returns403() throws Exception {
        mockMvc.perform(get("/admin/concerts/edit/1"))
                .andExpect(status().isForbidden());

        verify(concertService, never()).findConcertById(any());
    }

    // ========================================================================
    // TESTS MODIFICATION CONCERT
    // ========================================================================

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testUpdateConcert_AsAdmin_Success() throws Exception {
        when(concertService.findConcertById(1L)).thenReturn(Optional.of(testConcert));
        when(concertService.saveConcert(any(Concert.class))).thenReturn(testConcert);

        mockMvc.perform(post("/admin/concerts/update/1")
                .param("location", "Nouveau lieu de concert avec nom valide")
                .param("date", LocalDate.now().plusDays(45).toString())
                .param("description", "Description modifiée du concert")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/dashboard"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(concertService, times(1)).findConcertById(1L);
        verify(concertService, times(1)).saveConcert(any(Concert.class));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testUpdateConcert_AsUser_Returns403() throws Exception {
        mockMvc.perform(post("/admin/concerts/update/1")
                .param("location", "Nouveau lieu de concert avec nom valide")
                .param("date", LocalDate.now().plusDays(45).toString())
                .param("description", "Description modifiée du concert")
                .with(csrf()))
                .andExpect(status().isForbidden());

        verify(concertService, never()).findConcertById(any());
        verify(concertService, never()).saveConcert(any());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testUpdateConcert_ValidationErrors_ReturnsFormWithErrors() throws Exception {
        // Validation échoue avant l'appel au service
        mockMvc.perform(post("/admin/concerts/update/1")
                .param("location", "AB")  // Trop court
                .param("date", LocalDate.now().minusDays(5).toString())  // Date passée
                .param("description", "Description valide")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/edit-concert"))
                .andExpect(model().attributeHasFieldErrors("concert", "location", "date"));

        verify(concertService, never()).findConcertById(any());
        verify(concertService, never()).saveConcert(any());
    }
}
