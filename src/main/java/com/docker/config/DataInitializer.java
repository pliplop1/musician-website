// /src/main/java/com/docker/config/DataInitializer.java

package com.docker.config;

import com.docker.entity.Role;
import com.docker.entity.User;
import com.docker.repository.RoleRepository;
import com.docker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional; // <-- AJOUTEZ CET IMPORT

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Component
@Profile("dev")
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.initial-password}")
    private String adminInitialPassword;

    public DataInitializer(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional // <-- AJOUTEZ CETTE ANNOTATION
    public void run(String... args) throws Exception {
        // --- 1. Création des Rôles ---
        Role userRole = createRoleIfNotFound("ROLE_USER");
        Role adminRole = createRoleIfNotFound("ROLE_ADMIN");

        // --- 2. Création de l'Administrateur ---
        createUserIfNotFound("admin", "admin@test.com", adminInitialPassword, Set.of(adminRole));

        // --- 3. CRÉATION DE VOS UTILISATEURS DE TEST ---
        // Réinitialise le mot de passe de testuser à chaque démarrage
        resetUserPassword("testuser", "testeur@email.com", "Password123!", Set.of(userRole));
        createUserIfNotFound("tutu", "tutu@test.com", "Password123!", Set.of(userRole));
        createUserIfNotFound("amiens", "amiens@amiens.com", "Password123!", Set.of(userRole));
        createUserIfNotFound("tatatutu", "tatatutu@email.com", "Password123!", Set.of(userRole));
    }

    // Pas besoin de changer les méthodes utilitaires
    private Role createRoleIfNotFound(String name) {
        Role role = roleRepository.findByName(name);
        if (role == null) {
            role = new Role(name);
            roleRepository.save(role);
        }
        return role;
    }

    private void createUserIfNotFound(String username, String email, String password, Set<Role> roles) {
        if (userRepository.findByUsername(username) == null) {
            User user = new User();
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password));
            user.setRoles(roles);
            userRepository.save(user);
        }
    }

    private void resetUserPassword(String username, String email, String password, Set<Role> roles) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            user = new User();
            user.setUsername(username);
            user.setEmail(email);
            user.setRoles(roles);
        }
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
        System.out.println("🔑 Mot de passe réinitialisé pour l'utilisateur: " + username);
    }
}