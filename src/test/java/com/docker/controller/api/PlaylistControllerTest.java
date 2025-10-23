package com.docker.controller.api;

import com.docker.entity.Playlist;
import com.docker.service.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests unitaires pour PlaylistController
 * Teste les endpoints publics et admin de gestion des playlists
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PlaylistControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PlaylistService playlistService;

    @MockBean
    private CommentService commentService;

    @MockBean
    private SocialLinkService socialLinkService;

    @MockBean
    private LoginAttemptService loginAttemptService;

    private Playlist testPlaylist;
    private List<Playlist> testPlaylists;

    @BeforeEach
    void setUp() {
        testPlaylist = new Playlist();
        testPlaylist.setId(1L);
        testPlaylist.setTitle("Best Hits 2024");
        testPlaylist.setDescription("The best hits of 2024");
        testPlaylist.setIsPublic(true);
        testPlaylist.setIsFeatured(false);
        testPlaylist.setCreatedAt(LocalDateTime.now());

        Playlist featuredPlaylist = new Playlist();
        featuredPlaylist.setId(2L);
        featuredPlaylist.setTitle("Featured Mix");
        featuredPlaylist.setDescription("Our featured selection");
        featuredPlaylist.setIsPublic(true);
        featuredPlaylist.setIsFeatured(true);

        testPlaylists = Arrays.asList(testPlaylist, featuredPlaylist);
    }

    // ========================================================================
    // TESTS ENDPOINTS PUBLICS
    // ========================================================================

    @Test
    @WithAnonymousUser
    void testGetPublicPlaylists_ReturnsPlaylistList() throws Exception {
        when(playlistService.getPublicPlaylists()).thenReturn(testPlaylists);

        mockMvc.perform(get("/api/playlists")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("Best Hits 2024"))
                .andExpect(jsonPath("$[1].title").value("Featured Mix"));

        verify(playlistService, times(1)).getPublicPlaylists();
    }

    @Test
    @WithAnonymousUser
    void testGetFeaturedPlaylists_ReturnsFeaturedOnly() throws Exception {
        List<Playlist> featured = Arrays.asList(testPlaylists.get(1));
        when(playlistService.getFeaturedPlaylists()).thenReturn(featured);

        mockMvc.perform(get("/api/playlists/featured")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("Featured Mix"))
                .andExpect(jsonPath("$[0].isFeatured").value(true));

        verify(playlistService, times(1)).getFeaturedPlaylists();
    }

    @Test
    @WithAnonymousUser
    void testGetPlaylistById_ExistingPlaylist_ReturnsPlaylist() throws Exception {
        when(playlistService.findById(1L)).thenReturn(testPlaylist);

        mockMvc.perform(get("/api/playlists/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Best Hits 2024"))
                .andExpect(jsonPath("$.description").value("The best hits of 2024"));

        verify(playlistService, times(1)).findById(1L);
    }

    @Test
    @WithAnonymousUser
    void testGetPlaylistById_NonExistingPlaylist_Returns404() throws Exception {
        when(playlistService.findById(999L)).thenReturn(null);

        mockMvc.perform(get("/api/playlists/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(playlistService, times(1)).findById(999L);
    }

    // ========================================================================
    // TESTS ENDPOINTS ADMIN - CREATE
    // ========================================================================

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testCreatePlaylist_AsAdmin_ReturnsCreatedPlaylist() throws Exception {
        when(playlistService.save(any(Playlist.class))).thenReturn(testPlaylist);

        mockMvc.perform(post("/api/playlists")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testPlaylist)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value("Best Hits 2024"));

        verify(playlistService, times(1)).save(any(Playlist.class));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testCreatePlaylist_AsUser_Returns403() throws Exception {
        mockMvc.perform(post("/api/playlists")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testPlaylist)))
                .andExpect(status().isForbidden());

        verify(playlistService, never()).save(any());
    }

    @Test
    @WithAnonymousUser
    void testCreatePlaylist_NotAuthenticated_Returns401() throws Exception {
        mockMvc.perform(post("/api/playlists")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testPlaylist)))
                .andExpect(status().isUnauthorized());

        verify(playlistService, never()).save(any());
    }

    // ========================================================================
    // TESTS ENDPOINTS ADMIN - UPDATE
    // ========================================================================

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testUpdatePlaylist_AsAdmin_ReturnsUpdatedPlaylist() throws Exception {
        when(playlistService.findById(1L)).thenReturn(testPlaylist);
        when(playlistService.save(any(Playlist.class))).thenReturn(testPlaylist);

        mockMvc.perform(put("/api/playlists/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testPlaylist)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value("Best Hits 2024"));

        verify(playlistService, times(1)).findById(1L);
        verify(playlistService, times(1)).save(any(Playlist.class));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testUpdatePlaylist_NonExisting_Returns404() throws Exception {
        when(playlistService.findById(999L)).thenReturn(null);

        mockMvc.perform(put("/api/playlists/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testPlaylist)))
                .andExpect(status().isNotFound());

        verify(playlistService, times(1)).findById(999L);
        verify(playlistService, never()).save(any());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testUpdatePlaylist_AsUser_Returns403() throws Exception {
        mockMvc.perform(put("/api/playlists/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testPlaylist)))
                .andExpect(status().isForbidden());

        verify(playlistService, never()).findById(any());
    }

    // ========================================================================
    // TESTS ENDPOINTS ADMIN - ADD/REMOVE TRACKS
    // ========================================================================

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testAddTrackToPlaylist_AsAdmin_ReturnsSuccess() throws Exception {
        doNothing().when(playlistService).addTrackToPlaylist(1L, 10L);

        mockMvc.perform(post("/api/playlists/1/tracks/10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Musique ajoutée à la playlist"));

        verify(playlistService, times(1)).addTrackToPlaylist(1L, 10L);
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testAddTrackToPlaylist_AsUser_Returns403() throws Exception {
        mockMvc.perform(post("/api/playlists/1/tracks/10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        verify(playlistService, never()).addTrackToPlaylist(anyLong(), anyLong());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testRemoveTrackFromPlaylist_AsAdmin_ReturnsSuccess() throws Exception {
        doNothing().when(playlistService).removeTrackFromPlaylist(1L, 10L);

        mockMvc.perform(delete("/api/playlists/1/tracks/10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Musique retirée de la playlist"));

        verify(playlistService, times(1)).removeTrackFromPlaylist(1L, 10L);
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testRemoveTrackFromPlaylist_AsUser_Returns403() throws Exception {
        mockMvc.perform(delete("/api/playlists/1/tracks/10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        verify(playlistService, never()).removeTrackFromPlaylist(anyLong(), anyLong());
    }

    // ========================================================================
    // TESTS ENDPOINTS ADMIN - DELETE
    // ========================================================================

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testDeletePlaylist_AsAdmin_ReturnsSuccess() throws Exception {
        doNothing().when(playlistService).deletePlaylist(1L);

        mockMvc.perform(delete("/api/playlists/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Playlist supprimée"));

        verify(playlistService, times(1)).deletePlaylist(1L);
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testDeletePlaylist_AsUser_Returns403() throws Exception {
        mockMvc.perform(delete("/api/playlists/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        verify(playlistService, never()).deletePlaylist(anyLong());
    }

    @Test
    @WithAnonymousUser
    void testDeletePlaylist_NotAuthenticated_Returns401() throws Exception {
        mockMvc.perform(delete("/api/playlists/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());

        verify(playlistService, never()).deletePlaylist(anyLong());
    }
}
