package com.docker.repository;

import com.docker.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Ce repository gère les opérations de base de données pour l'entité Message.
 * En étendant JpaRepository, Spring Data JPA fournit automatiquement
 * des méthodes comme save(), findById(), findAll(), delete(), etc.
 */
@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    // Compter les messages non lus
    long countByReadFalse();
}
