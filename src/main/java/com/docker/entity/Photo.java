// /src/main/java/com/docker/entity/Photo.java

package com.docker.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Photo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String filename; // Le nom du fichier image (ex: concert_paris.jpg)

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder = 0; // Ordre d'affichage
}