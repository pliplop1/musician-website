// /src/main/java/com/docker/repository/VideoRepository.java
package com.docker.repository;

import com.docker.entity.User;
import com.docker.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {

    // Récupérer toutes les vidéos likées par un utilisateur
    @Query("SELECT v FROM Video v JOIN v.likedByUsers u WHERE u = :user ORDER BY v.createdAt DESC")
    List<Video> findLikedByUser(@Param("user") User user);
}
