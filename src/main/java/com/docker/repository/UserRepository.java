package com.docker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.docker.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    User findByEmail(String email);
}

