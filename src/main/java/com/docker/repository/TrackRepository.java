// /src/main/java/com/docker/repository/TrackRepository.java
package com.docker.repository;

import com.docker.entity.Track;
import com.docker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrackRepository extends JpaRepository<Track, Long> {

    // Récupérer tous les morceaux likés par un utilisateur
    @Query("SELECT t FROM Track t JOIN t.likedByUsers u WHERE u = :user ORDER BY t.createdAt DESC")
    List<Track> findLikedByUser(@Param("user") User user);
}