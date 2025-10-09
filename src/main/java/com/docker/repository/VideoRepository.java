// /src/main/java/com/docker/repository/VideoRepository.java
package com.docker.repository;

import com.docker.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {
}
