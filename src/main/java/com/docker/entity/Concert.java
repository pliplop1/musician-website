package com.docker.entity;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import org.springframework.format.annotation.DateTimeFormat;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter // Génère tous les getters
@Setter // Génère tous les setters
@EqualsAndHashCode(exclude = "interestedUsers") // <-- LA CORRECTION : On exclut la liste pour casser la boucle
@ToString(exclude = "interestedUsers")          // <-- C'est aussi une bonne pratique pour l'affichage
public class Concert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String location;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;
    
    private String description;
    
    @ManyToMany(mappedBy = "favoriteConcerts")
    private Set<User> interestedUsers = new HashSet<>();

    // Les getters et setters manuels peuvent être supprimés, Lombok s'en charge.
    public Concert() {
    }
}