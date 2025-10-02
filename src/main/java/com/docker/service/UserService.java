package com.docker.service;

import com.docker.entity.Concert;
import com.docker.entity.Role;
import com.docker.entity.User;
import com.docker.repository.ConcertRepository;
import com.docker.repository.RoleRepository;
import com.docker.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ConcertRepository concertRepository;
    private final PasswordEncoder passwordEncoder;

    private static final String PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";

    public UserService(UserRepository userRepository, RoleRepository roleRepository, ConcertRepository concertRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.concertRepository = concertRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional
    public void saveUser(User user) {
        if (!user.getPassword().matches(PASSWORD_PATTERN)) {
             throw new IllegalArgumentException("Le mot de passe ne respecte pas les critères de sécurité.");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        Role userRole = roleRepository.findByName("ROLE_USER");
        if (userRole == null) {
            throw new RuntimeException("Error: Role 'ROLE_USER' is not found.");
        }
        
        user.setRoles(new HashSet<>(Collections.singletonList(userRole)));
        userRepository.save(user);
    }
    
    @Transactional
    public void updateUserFromAdmin(Long userId, String username, String email, Set<Role> roles) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + userId));
        
        existingUser.setUsername(username);
        existingUser.setEmail(email);
        existingUser.setRoles(roles);
        
        userRepository.save(existingUser);
    }
    
    @Transactional
    public void updateUserProfile(String currentUsername, String newUsername, String newEmail) {
        User currentUser = userRepository.findByUsername(currentUsername);
        if (currentUser == null) {
            throw new IllegalArgumentException("Utilisateur non trouvé.");
        }

        User userByNewUsername = userRepository.findByUsername(newUsername);
        if (userByNewUsername != null && !userByNewUsername.getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("Ce nom d'utilisateur est déjà pris.");
        }

        User userByNewEmail = userRepository.findByEmail(newEmail);
        if (userByNewEmail != null && !userByNewEmail.getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("Cet email est déjà utilisé.");
        }

        currentUser.setUsername(newUsername);
        currentUser.setEmail(newEmail);
        userRepository.save(currentUser);
    }

    @Transactional
    public void changeUserPassword(String username, String oldPassword, String newPassword, String confirmPassword) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new IllegalArgumentException("Utilisateur non trouvé.");
        }

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("L'ancien mot de passe est incorrect.");
        }

        if (!newPassword.equals(confirmPassword)) {
            throw new IllegalArgumentException("Le nouveau mot de passe et sa confirmation ne correspondent pas.");
        }

        if (!newPassword.matches(PASSWORD_PATTERN)) {
            String errorMessage = "Le mot de passe doit contenir au moins :\n"
                                + "- 8 caractères\n"
                                + "- Une lettre majuscule\n"
                                + "- Une lettre minuscule\n"
                                + "- Un chiffre\n"
                                + "- Un caractère spécial (@, $, !, %, *, ?, &)";
            throw new IllegalArgumentException(errorMessage);
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
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
    
    public Page<User> searchUsers(String keyword, Pageable pageable) {
        if (keyword != null && !keyword.isEmpty()) {
            return userRepository.searchUsers(keyword, pageable);
        }
        return userRepository.findAll(pageable);
    }
    
    public long countUsers() {
        return userRepository.count();
    }

    @Transactional
    public void addFavoriteConcert(String username, Long concertId) {
        User user = userRepository.findByUsername(username);
        Concert concert = concertRepository.findById(concertId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid concert Id:" + concertId));
        user.getFavoriteConcerts().add(concert);
        userRepository.save(user);
    }

    @Transactional
    public void removeFavoriteConcert(String username, Long concertId) {
        User user = userRepository.findByUsername(username);
        Concert concert = concertRepository.findById(concertId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid concert Id:" + concertId));
        user.getFavoriteConcerts().remove(concert);
        userRepository.save(user);
    }
}