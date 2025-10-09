package com.docker.entity;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import org.springframework.format.annotation.DateTimeFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

    @NotBlank(message = "Le lieu est obligatoire")
    @Size(min = 3, max = 200, message = "Le lieu doit contenir entre 3 et 200 caractères")
    private String location;

    @NotNull(message = "La date est obligatoire")
    @FutureOrPresent(message = "La date doit être aujourd'hui ou dans le futur")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    @Size(max = 1000, message = "La description ne peut pas dépasser 1000 caractères")
    @Column(length = 1000)
    private String description;
    
    @ManyToMany(mappedBy = "favoriteConcerts")
    private Set<User> interestedUsers = new HashSet<>();

    // Les getters et setters manuels peuvent être supprimés, Lombok s'en charge.
    public Concert() {
    }
}