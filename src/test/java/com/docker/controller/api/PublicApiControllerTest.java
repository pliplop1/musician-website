package com.docker.controller.api;

import com.docker.entity.Biography;
import com.docker.entity.Concert;
import com.docker.service.BiographyService;
import com.docker.service.ConcertService;
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

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PublicApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean private BiographyService biographyService;
    @MockBean private ConcertService concertService;

    private Biography testBio;
    private Concert upcomingConcert, pastConcert;

    @BeforeEach
    void setUp() {
        testBio = new Biography();
        testBio.setContent("This is a test biography.");

        upcomingConcert = new Concert();
        upcomingConcert.setId(1L);
        upcomingConcert.setLocation("Future Venue");
        upcomingConcert.setDate(LocalDate.now().plusDays(10));

        pastConcert = new Concert();
        pastConcert.setId(2L);
        pastConcert.setLocation("Past Venue");
        pastConcert.setDate(LocalDate.now().minusDays(10));
    }

    @Test
    void getBiography_Success() throws Exception {
        when(biographyService.getBiography()).thenReturn(testBio);

        mockMvc.perform(get("/api/public/biography"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", is("This is a test biography.")));
    }

    @Test
    void getUpcomingConcerts_Success() throws Exception {
        when(concertService.findAllConcerts()).thenReturn(Arrays.asList(upcomingConcert, pastConcert));

        mockMvc.perform(get("/api/public/concerts/upcoming"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].location", is("Future Venue")));
    }

    @Test
    void getPastConcerts_Success() throws Exception {
        when(concertService.findAllConcerts()).thenReturn(Arrays.asList(upcomingConcert, pastConcert));

        mockMvc.perform(get("/api/public/concerts/past"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].location", is("Past Venue")));
    }

    @Test
    void getAuthStatus_Unauthenticated() throws Exception {
        mockMvc.perform(get("/api/public/auth/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authenticated", is(false)));
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void getAuthStatus_Authenticated() throws Exception {
        mockMvc.perform(get("/api/public/auth/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authenticated", is(true)))
                .andExpect(jsonPath("$.username", is("testuser")));
    }
}
