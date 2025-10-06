// /src/main/java/com/docker/entity/Biography.java

package com.docker.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Biography {

    @Id
    private Long id; // On utilisera un ID fixe (ex: 1) car il n'y a qu'une seule biographie

    @Column(columnDefinition = "TEXT")
    private String content;
}