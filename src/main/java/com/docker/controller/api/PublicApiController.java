package com.docker.controller.api;

import com.docker.dto.*;
import com.docker.entity.*;
import com.docker.exception.ResourceNotFoundException;
import com.docker.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * API REST publique pour le frontend Vue.js
 * Tous ces endpoints sont accessibles sans authentification
 */
@Tag(name = "API Publique", description = "Endpoints publics accessibles sans authentification pour le frontend Vue.js")
@RestController
@RequestMapping("/api/public")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:8080"})
public class PublicApiController {

    private final BiographyService biographyService;
    private final ConcertService concertService;
    private final PhotoService photoService;
    private final TrackService trackService;

    public PublicApiController(BiographyService biographyService, ConcertService concertService,
                               PhotoService photoService, TrackService trackService) {
        this.biographyService = biographyService;
        this.concertService = concertService;
        this.photoService = photoService;
        this.trackService = trackService;
    }

    /**
     * Données pour la Hero Section
     */
    @Operation(
        summary = "Récupérer les données de la section Hero",
        description = "Retourne les données pour la section principale de la page d'accueil (Hero), incluant le titre, la dernière sortie musicale et la vidéo de fond"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Données récupérées avec succès",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = HeroDataDTO.class)))
    })
    @GetMapping("/hero")
    public ResponseEntity<HeroDataDTO> getHeroData() {
        // Pour l'instant données en dur, à adapter selon ta DB
        HeroDataDTO.LatestReleaseDTO latestRelease = new HeroDataDTO.LatestReleaseDTO(
            "Notre Dernier Album",
            "/uploaded-photos/cover-album.jpg",
            "https://open.spotify.com/album/...",
            "https://music.apple.com/..."
        );

        HeroDataDTO heroData = new HeroDataDTO(
            "DUO BLACK & WHITE",
            "La musique qui vous transporte • France & Belgique",
            "/videos/hero-concert.mp4",
            "/images/poster.jpg",
            latestRelease
        );

        return ResponseEntity.ok(heroData);
    }

    /**
     * Biographie complète
     */
    @Operation(
        summary = "Récupérer la biographie du duo",
        description = "Retourne la biographie complète du Duo Black & White avec la timeline des événements marquants et les photos associées"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Biographie récupérée avec succès",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = BiographyDTO.class))),
        @ApiResponse(responseCode = "404", description = "Biographie introuvable ou vide", content = @Content)
    })
    @GetMapping("/biography")
    public ResponseEntity<BiographyDTO> getBiography() {
        Biography bio = biographyService.getBiography();

        if (bio == null || bio.getContent() == null || bio.getContent().isEmpty()) {
            throw new ResourceNotFoundException("Biography not found or empty");
        }

        // Pour l'instant, timeline vide - à implémenter selon tes besoins
        List<BiographyDTO.TimelineEventDTO> timeline = List.of(
            new BiographyDTO.TimelineEventDTO(2015, "Débuts", "Premiers pas dans la musique", null),
            new BiographyDTO.TimelineEventDTO(2018, "Premier Album", "Sortie du premier album", null),
            new BiographyDTO.TimelineEventDTO(2024, "Aujourd'hui", "Tournée nationale", null)
        );

        BiographyDTO bioDTO = new BiographyDTO(
            bio.getContent(),
            timeline,
            new ArrayList<>() // Photos à ajouter selon tes besoins
        );

        return ResponseEntity.ok(bioDTO);
    }

    /**
     * Discographie complète (tous les albums et morceaux)
     */
    @Operation(
        summary = "Récupérer la discographie complète",
        description = "Retourne tous les albums du duo avec leurs morceaux, incluant les liens Spotify et les fichiers audio uploadés"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Discographie récupérée avec succès",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = AlbumDTO.class)))
    })
    @GetMapping("/discography")
    public ResponseEntity<List<AlbumDTO>> getDiscography() {
        List<Track> tracks = trackService.getAllTracks();

        // Grouper les morceaux par album (simplified - à adapter)
        // Pour l'instant, on retourne un album fictif avec tous les tracks
        List<TrackDTO> trackDTOs = tracks.stream()
            .map(track -> {
                String audioUrl = null;
                String spotifyUrl = null;

                // Construire l'URL selon le type de track
                if ("EMBED".equals(track.getTrackType())) {
                    // Pour les embeds, utiliser le embedCode comme spotifyUrl
                    spotifyUrl = track.getEmbedCode();
                } else if ("UPLOADED_FILE".equals(track.getTrackType())) {
                    // Pour les fichiers uploadés, construire l'URL
                    audioUrl = "/uploaded-tracks/" + track.getFilename();
                }

                return new TrackDTO(
                    track.getId(),
                    track.getTitle(),
                    null, // trackNumber - à ajouter dans l'entité si besoin
                    null, // duration - à calculer ou ajouter dans l'entité
                    audioUrl,
                    spotifyUrl
                );
            })
            .collect(Collectors.toList());

        AlbumDTO album = new AlbumDTO(
            1L,
            "Collection",
            "/images/default-album.jpg",
            2024,
            "Collection de morceaux",
            trackDTOs,
            null,
            null
        );

        return ResponseEntity.ok(List.of(album));
    }

    /**
     * Galerie photos complète
     */
    @Operation(
        summary = "Récupérer la galerie photos",
        description = "Retourne toutes les photos du duo avec leurs métadonnées (catégories, légendes, ordre d'affichage)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Galerie récupérée avec succès",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PhotoDTO.class)))
    })
    @GetMapping("/gallery")
    public ResponseEntity<List<PhotoDTO>> getGallery() {
        List<Photo> photos = photoService.getAllPhotos();

        List<PhotoDTO> photoDTOs = photos.stream()
            .map(photo -> new PhotoDTO(
                photo.getId(),
                "/uploaded-photos/" + photo.getFilename(),
                "/uploaded-photos/" + photo.getFilename(), // thumbnail = same for now
                null, // caption - à ajouter dans l'entité Photo si besoin
                "concert", // category par défaut - à ajouter dans l'entité si besoin
                photo.getDisplayOrder()
            ))
            .collect(Collectors.toList());

        return ResponseEntity.ok(photoDTOs);
    }

    /**
     * Prochains concerts (à venir uniquement)
     */
    @Operation(
        summary = "Récupérer les concerts à venir",
        description = "Retourne tous les concerts programmés dans le futur, triés par date croissante avec le nombre de jours restants"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des concerts à venir récupérée avec succès",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ConcertDTO.class)))
    })
    @GetMapping("/concerts/upcoming")
    public ResponseEntity<List<ConcertDTO>> getUpcomingConcerts() {
        java.time.LocalDate today = java.time.LocalDate.now();
        List<Concert> concerts = concertService.findAllConcerts().stream()
            .filter(concert -> concert.getDate().isAfter(today))
            .sorted((c1, c2) -> c1.getDate().compareTo(c2.getDate()))
            .collect(Collectors.toList());

        List<ConcertDTO> concertDTOs = concerts.stream()
            .map(this::mapToConcertDTO)
            .collect(Collectors.toList());

        return ResponseEntity.ok(concertDTOs);
    }

    /**
     * Concerts passés
     */
    @Operation(
        summary = "Récupérer les concerts passés",
        description = "Retourne tous les concerts déjà réalisés, triés par date décroissante (du plus récent au plus ancien)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des concerts passés récupérée avec succès",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ConcertDTO.class)))
    })
    @GetMapping("/concerts/past")
    public ResponseEntity<List<ConcertDTO>> getPastConcerts() {
        java.time.LocalDate today = java.time.LocalDate.now();
        List<Concert> concerts = concertService.findAllConcerts().stream()
            .filter(concert -> concert.getDate().isBefore(today))
            .sorted((c1, c2) -> c2.getDate().compareTo(c1.getDate())) // DESC
            .collect(Collectors.toList());

        List<ConcertDTO> concertDTOs = concerts.stream()
            .map(this::mapToConcertDTO)
            .collect(Collectors.toList());

        return ResponseEntity.ok(concertDTOs);
    }

    /**
     * Données stats pour le dashboard (optionnel)
     */
    @Operation(
        summary = "Récupérer les statistiques du site",
        description = "Retourne des statistiques globales : nombre total de concerts, photos, morceaux, et concerts à venir"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Statistiques récupérées avec succès",
            content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        java.time.LocalDate today = java.time.LocalDate.now();
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalConcerts", concertService.findAllConcerts().size());
        stats.put("totalPhotos", photoService.getAllPhotos().size());
        stats.put("totalTracks", trackService.getAllTracks().size());

        long upcomingConcerts = concertService.findAllConcerts().stream()
            .filter(concert -> concert.getDate().isAfter(today))
            .count();
        stats.put("upcomingConcerts", upcomingConcerts);

        return ResponseEntity.ok(stats);
    }

    // Helper method pour mapper Concert → ConcertDTO
    private ConcertDTO mapToConcertDTO(Concert concert) {
        java.time.LocalDate today = java.time.LocalDate.now();
        boolean isPast = concert.getDate().isBefore(today);
        Long daysUntil = null;

        if (!isPast) {
            daysUntil = ChronoUnit.DAYS.between(today, concert.getDate());
        }

        // Convertir LocalDate en LocalDateTime (début de journée)
        LocalDateTime dateTime = concert.getDate().atStartOfDay();

        return new ConcertDTO(
            concert.getId(),
            concert.getLocation(),
            concert.getLocation(), // venue = location pour l'instant
            dateTime,
            concert.getDescription(),
            null, // ticketUrl à ajouter dans l'entité si besoin
            null, // photoUrl à ajouter dans l'entité si besoin
            isPast,
            daysUntil
        );
    }
}
