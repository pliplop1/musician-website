package com.docker.service;

import com.docker.entity.Role;
import com.docker.entity.User;
import com.docker.repository.RoleRepository;
import com.docker.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set; // Import corrigé
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional
    public void saveUser(User user) { // Méthode renommée
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        Role userRole = roleRepository.findByName("ROLE_USER");
        if (userRole == null) {
            throw new RuntimeException("Error: Role 'ROLE_USER' is not found.");
        }
        
        user.setRoles(new HashSet<>(Collections.singletonList(userRole)));
        userRepository.save(user);
    }
    
    @Transactional
    // Signature de la méthode corrigée
    public void updateUserFromAdmin(Long userId, String username, String email, Set<Role> roles) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + userId));
        
        existingUser.setUsername(username);
        existingUser.setEmail(email);
        existingUser.setRoles(roles);
        
        userRepository.save(existingUser);
    }
    
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }
    
    public Optional<User> findUserById(Long id) {
        return userRepository.findById(id);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
    
    public List<User> searchUsers(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return findAllUsers();
        }
        String lowerCaseKeyword = keyword.toLowerCase();
        return userRepository.findAll().stream()
                .filter(user -> user.getUsername().toLowerCase().contains(lowerCaseKeyword) ||
                                 (user.getEmail() != null && user.getEmail().toLowerCase().contains(lowerCaseKeyword)))
                .collect(Collectors.toList());
    }
}

