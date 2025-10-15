package com.docker.service;

import com.docker.entity.Role;
import com.docker.entity.User;
import com.docker.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class UserDetailsServiceImplTest {

    @Test
    void loadUserByUsername_returnsSpringUserWithRoles() {
        UserRepository repo = Mockito.mock(UserRepository.class);
        User user = new User();
        user.setUsername("john");
        user.setPassword("encoded");
        Role role = new Role();
        role.setName("ROLE_USER");
        user.setRoles(Collections.singleton(role));
        Mockito.when(repo.findByUsername("john")).thenReturn(user);

        UserDetailsServiceImpl svc = new UserDetailsServiceImpl(repo);
        UserDetails details = svc.loadUserByUsername("john");

        assertEquals("john", details.getUsername());
        assertEquals("encoded", details.getPassword());
        assertTrue(details.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
    }

    @Test
    void loadUserByUsername_unknownUser_throws() {
        UserRepository repo = Mockito.mock(UserRepository.class);
        Mockito.when(repo.findByUsername("nope")).thenReturn(null);
        UserDetailsServiceImpl svc = new UserDetailsServiceImpl(repo);
        assertThrows(UsernameNotFoundException.class, () -> svc.loadUserByUsername("nope"));
    }
}

