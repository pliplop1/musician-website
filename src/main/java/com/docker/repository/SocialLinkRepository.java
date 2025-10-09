package com.docker.repository;

import com.docker.entity.SocialLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SocialLinkRepository extends JpaRepository<SocialLink, Long> {

    List<SocialLink> findByEnabledTrueOrderByDisplayOrderAsc();

    Optional<SocialLink> findByName(String name);

    List<SocialLink> findAllByOrderByDisplayOrderAsc();
}
