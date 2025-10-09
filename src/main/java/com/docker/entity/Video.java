// /src/main/java/com/docker/entity/Video.java
package com.docker.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Video {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Enumerated(EnumType.STRING) // Stocke le type sous forme de texte (EMBED ou UPLOADED_FILE)
    private VideoType videoType;

    @Column(columnDefinition = "TEXT") // Pour stocker le code <iframe>
    private String embedCode;

    private String filename; // Pour stocker le nom du fichier vidéo si uploadé

}
