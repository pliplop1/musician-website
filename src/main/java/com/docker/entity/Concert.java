package com.docker.entity;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import lombok.Data;

@Entity
@Data
public class Concert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String location;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;
    
    private String description;
    
    @ManyToMany(mappedBy = "favoriteConcerts")
    private Set<User> interestedUsers = new HashSet<>();

    // Constructeurs
    public Concert() {
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<User> getInterestedUsers() {
        return interestedUsers;
    }

    public void setInterestedUsers(Set<User> interestedUsers) {
        this.interestedUsers = interestedUsers;
    }
}

