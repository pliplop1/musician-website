package com.docker.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Représente un lien vers un réseau social.
 */
@Entity
@Getter
@Setter
@Table(name = "social_links")
public class SocialLink {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String name; // Ex: "Facebook", "Instagram", "YouTube"

    @Column(length = 500)
    private String url; // URL du réseau social

    @Column(nullable = false, length = 100)
    private String icon; // Classe CSS de l'icône (ex: "fab fa-facebook")

    @Column(nullable = false)
    private Boolean enabled = true; // Le lien est-il activé ?

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder = 0; // Ordre d'affichage

    public SocialLink() {
    }

    public SocialLink(String name, String icon, Integer displayOrder) {
        this.name = name;
        this.icon = icon;
        this.displayOrder = displayOrder;
        this.url = "";
        this.enabled = false;
    }

    public SocialLink(String name, String url, String icon, Integer displayOrder) {
        this.name = name;
        this.url = url;
        this.icon = icon;
        this.displayOrder = displayOrder;
        this.enabled = true;
    }
}
