// /src/main/java/com/docker/repository/PhotoRepository.java

package com.docker.repository;

import com.docker.entity.Photo;
import com.docker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, Long> {

    List<Photo> findAllByOrderByDisplayOrderAsc();

    // Récupérer toutes les photos likées par un utilisateur
    @Query("SELECT p FROM Photo p JOIN p.likedByUsers u WHERE u = :user ORDER BY p.createdAt DESC")
    List<Photo> findLikedByUser(@Param("user") User user);
}