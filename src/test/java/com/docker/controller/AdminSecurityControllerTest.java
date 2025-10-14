
package com.docker.controller;

import com.docker.dto.SecurityStatsDTO;
import com.docker.service.LoginAttemptService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdminSecurityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LoginAttemptService loginAttemptService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void testShowSecurityDashboard_AsAdmin_Success() throws Exception {
        SecurityStatsDTO stats = new SecurityStatsDTO(100L, 80L, 20L, 5L, Collections.emptyList(), Collections.emptyList(), Collections.emptyMap());
        when(loginAttemptService.getSecurityStats()).thenReturn(stats);

        mockMvc.perform(get("/admin/security"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/security-dashboard"))
                .andExpect(model().attribute("stats", stats));
    }

    @Test
    @WithMockUser(roles = "USER")
    void testShowSecurityDashboard_AsUser_Returns403() throws Exception {
        mockMvc.perform(get("/admin/security"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testShowSecurityDashboard_ServiceThrowsException_RedirectsToDashboard() throws Exception {
        when(loginAttemptService.getSecurityStats()).thenThrow(new RuntimeException("Service unavailable"));

        mockMvc.perform(get("/admin/security"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/dashboard"))
                .andExpect(flash().attributeExists("errorMessage"));
    }
}
