package com.docker.entity;

import java.util.HashSet;
import java.util.Set;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter // Génère tous les getters
@Setter // Génère tous les setters
@EqualsAndHashCode(exclude = "favoriteConcerts") // <-- LA CORRECTION : On exclut la liste pour casser la boucle
@ToString(exclude = "favoriteConcerts")          // <-- C'est aussi une bonne pratique pour l'affichage
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String email;
    private String password;

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
    
    // Les getters et setters manuels peuvent être supprimés, Lombok s'en charge.
    public User() {
    }

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }
}