package com.docker.entity;

/**
 * Types de badges pour les critères de déblocage.
 */
public enum BadgeType {
    FAVORITE_CONCERTS,  // Basé sur le nombre de concerts favoris
    ACCOUNT_AGE,        // Basé sur l'ancienneté du compte
    MESSAGES_SENT,      // Basé sur le nombre de messages envoyés
    SPECIAL             // Badges spéciaux (admin, etc.)
}
