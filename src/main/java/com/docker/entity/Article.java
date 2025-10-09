package com.docker.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le titre est obligatoire")
    @Size(min = 5, max = 200, message = "Le titre doit contenir entre 5 et 200 caractères")
    private String titre;

    @NotBlank(message = "Le contenu est obligatoire")
    @Size(min = 20, max = 10000, message = "Le contenu doit contenir entre 20 et 10000 caractères")
    @Column(columnDefinition = "TEXT") // Permet d'écrire un contenu plus long qu'un simple String
    private String contenu;

    private LocalDateTime datePublication;

    // Constructeur par défaut
    public Article() {
    }
}