package com.docker.service;

import com.docker.entity.Role;
import com.docker.entity.User;
import com.docker.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class DataExportServiceTest {

    @Test
    void exportUserData_returnsJsonWithAccountAndSecurity() throws Exception {
        UserRepository repo = Mockito.mock(UserRepository.class);
        User user = new User("john","john@example.com","hash");
        user.setId(1L);
        Role role = new Role();
        role.setName("ROLE_USER");
        user.getRoles().add(role);
        Mockito.when(repo.findById(1L)).thenReturn(Optional.of(user));

        DataExportService svc = new DataExportService(repo);
        String json = svc.exportUserData(1L);
        assertTrue(json.contains("\"account\""));
        assertTrue(json.contains("\"security\""));
        assertTrue(json.contains("\"roles\""));
        assertTrue(json.contains("john@example.com"));
    }

    @Test
    void exportUserData_unknownUser_throws() {
        UserRepository repo = Mockito.mock(UserRepository.class);
        Mockito.when(repo.findById(99L)).thenReturn(Optional.empty());
        DataExportService svc = new DataExportService(repo);
        assertThrows(IllegalArgumentException.class, () -> svc.exportUserData(99L));
    }
}

