package com.docker.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@EqualsAndHashCode(exclude = {"favoriteConcerts", "badges"})
@ToString(exclude = {"favoriteConcerts", "badges"})
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String username;
    private String email;
    private String password;

    // NOUVEAUX CHAMPS AVEC ANNOTATIONS
    @Column(length = 50)
    @Size(max = 50, message = "Le prénom ne peut pas dépasser 50 caractères")
    private String firstName;      // Prénom
    
    @Column(length = 50)
    @Size(max = 50, message = "Le nom ne peut pas dépasser 50 caractères")
    private String lastName;       // Nom de famille
    
    @Column(length = 500)
    @Size(max = 500, message = "La biographie ne peut pas dépasser 500 caractères")
    private String bio;            // Biographie courte
    
    private String avatarFilename; // Nom du fichier avatar
    
    private LocalDateTime createdAt; // Date d'inscription

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "user_favorite_concerts",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "concert_id")
    )
    private Set<Concert> favoriteConcerts = new HashSet<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<UserBadge> badges = new HashSet<>();

    // Définit automatiquement la date de création
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    public User() {
    }

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }
    
    // Méthode utilitaire pour obtenir le nom complet
    public String getFullName() {
        if (firstName != null && lastName != null) {
            return firstName + " " + lastName;
        } else if (firstName != null) {
            return firstName;
        } else if (lastName != null) {
            return lastName;
        }
        return username;
    }
    
    // Méthode pour obtenir les initiales (pour l'avatar par défaut)
    public String getInitials() {
        StringBuilder initials = new StringBuilder();
        if (firstName != null && !firstName.isEmpty()) {
            initials.append(firstName.charAt(0));
        }
        if (lastName != null && !lastName.isEmpty()) {
            initials.append(lastName.charAt(0));
        }
        if (initials.length() == 0) {
            initials.append(username.charAt(0));
        }
        return initials.toString().toUpperCase();
    }
}