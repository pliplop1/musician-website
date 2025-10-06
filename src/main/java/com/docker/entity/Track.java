// /src/main/java/com/docker/entity/Track.java
package com.docker.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Track {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Enumerated(EnumType.STRING) // Stocke le type sous forme de texte (EMBED ou UPLOADED_FILE)
    private TrackType trackType;

    @Column(columnDefinition = "TEXT") // Pour stocker le code <iframe>
    private String embedCode;

    private String filename; // Pour stocker le nom du fichier audio si uploadé
    
    // AJOUT : Champ pour stocker les données du fichier en Base64
    @Transient // Ne sera pas sauvegardé en base de données
    private String base64Data;
}