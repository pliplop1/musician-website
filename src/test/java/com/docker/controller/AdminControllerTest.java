
package com.docker.controller;

import com.docker.entity.Article;
import com.docker.entity.Biography;
import com.docker.entity.Concert;
import com.docker.entity.Message;
import com.docker.entity.Photo;
import com.docker.entity.Track;
import com.docker.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean private UserService userService;
    @MockBean private ConcertService concertService;
    @MockBean private ArticleService articleService;
    @MockBean private MessageService messageService;
    @MockBean private BiographyService biographyService;
    @MockBean private PhotoService photoService;
    @MockBean private TrackService trackService;
    @MockBean private LoginAttemptService loginAttemptService; // Common mock

    @Test
    @WithMockUser(roles = "ADMIN")
    void testShowDashboard_AsAdmin_ReturnsDashboardWithData() throws Exception {
        // Arrange: Mock all service calls with realistic data for the template
        Biography bio = new Biography();
        bio.setContent("Test Bio");

        Photo photo = new Photo();
        photo.setFilename("test.jpg");

        Concert concert = new Concert();
        concert.setId(1L);
        concert.setLocation("Test Venue");
        concert.setDate(java.time.LocalDate.now());

        Article article = new Article();
        article.setId(1L);
        article.setTitre("Test Article");

        Track track = new Track();
        track.setTitle("Test Track");
        track.setTrackType(com.docker.entity.TrackType.EMBED);

        Message message = new Message();
        message.setName("Test User");
        message.setContent("Test message content");

        when(userService.countUsers()).thenReturn(10L);
        when(concertService.countConcerts()).thenReturn(5L);
        when(articleService.countArticles()).thenReturn(3L);
        when(messageService.countMessages()).thenReturn(20L);
        when(biographyService.getBiography()).thenReturn(bio);
        when(photoService.getAllPhotos()).thenReturn(Collections.singletonList(photo));
        when(concertService.findAllConcerts()).thenReturn(Collections.singletonList(concert));
        when(messageService.findAllMessages()).thenReturn(Collections.singletonList(message));
        when(articleService.findAllArticles()).thenReturn(Collections.singletonList(article));
        when(trackService.getAllTracks()).thenReturn(Collections.singletonList(track));

        // Act & Assert
        mockMvc.perform(get("/admin/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/dashboard"))
                .andExpect(model().attribute("userCount", 10L))
                .andExpect(model().attribute("concertCount", 5L))
                .andExpect(model().attribute("articleCount", 3L))
                .andExpect(model().attribute("messageCount", 20L))
                .andExpect(model().attributeExists("biography", "photos", "concerts", "messages", "articles", "tracks", "concert", "article"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void testShowDashboard_AsUser_Returns403() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/admin/dashboard"))
                .andExpect(status().isForbidden());

        // Verify that no service methods were called
        verifyNoInteractions(userService, concertService, articleService, messageService, biographyService, photoService, trackService);
    }
}
