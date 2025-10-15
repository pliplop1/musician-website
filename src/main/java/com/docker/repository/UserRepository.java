package com.docker.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.docker.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    User findByEmail(String email);

    @Query("SELECT u FROM User u WHERE " +
           "LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<User> searchUsers(String keyword, Pageable pageable);
    
    Page<User> findAll(Pageable pageable);

    // Nettoyage explicite des tables de jointure par user_id (défensif)
    @Modifying
    @Query(value = "DELETE FROM user_roles WHERE user_id = :userId", nativeQuery = true)
    void deleteRolesByUserId(@Param("userId") Long userId);

    @Modifying
    @Query(value = "DELETE FROM user_favorite_concerts WHERE user_id = :userId", nativeQuery = true)
    void deleteFavoriteConcertsByUserId(@Param("userId") Long userId);

    // Cleanup by concert_id to allow deleting a concert referenced in favorites
    @Modifying
    @Query(value = "DELETE FROM user_favorite_concerts WHERE concert_id = :concertId", nativeQuery = true)
    void deleteFavoriteConcertsByConcertId(@Param("concertId") Long concertId);
}
