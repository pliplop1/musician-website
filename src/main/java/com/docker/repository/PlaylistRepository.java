package com.docker.repository;

import com.docker.entity.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, Long> {
    List<Playlist> findByIsPublicTrue();
    List<Playlist> findByIsFeaturedTrue();
    List<Playlist> findByCreatedById(Long userId);
}
