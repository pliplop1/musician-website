package com.docker.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Représente un badge que les utilisateurs peuvent débloquer.
 * Système de gamification pour encourager l'engagement.
 */
@Entity
@Getter
@Setter
@Table(name = "badges")
public class Badge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 100)
    private String code; // Code unique pour identifier le badge (ex: "FIRST_FAVORITE")

    @Column(nullable = false, length = 100)
    private String name; // Nom du badge (ex: "Mélomane")

    @Column(length = 500)
    private String description; // Description du badge

    @Column(length = 50)
    private String icon; // Emoji ou classe d'icône (ex: "🎵")

    @Column(nullable = false)
    private Integer requiredCount; // Nombre requis pour débloquer (ex: 1, 5, 10)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BadgeType type; // Type de badge pour le critère de déblocage

    @Column(nullable = false)
    private Integer level = 1; // Niveau du badge (1 = bronze, 2 = argent, 3 = or)

    public Badge() {
    }

    public Badge(String code, String name, String description, String icon, BadgeType type, Integer requiredCount, Integer level) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.type = type;
        this.requiredCount = requiredCount;
        this.level = level;
    }
}
