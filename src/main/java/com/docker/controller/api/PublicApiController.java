package com.docker.controller.api;

import com.docker.dto.*;
import com.docker.entity.*;
import com.docker.exception.ResourceNotFoundException;
import com.docker.service.*;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
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
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:8106"})
public class PublicApiController {

    private final BiographyService biographyService;
    private final ConcertService concertService;
    private final PhotoService photoService;
    private final TrackService trackService;
    private final VideoService videoService;
    private final HomepageSettingsService homepageSettingsService;
    private final UserService userService;

    public PublicApiController(BiographyService biographyService, ConcertService concertService,
                               PhotoService photoService, TrackService trackService, VideoService videoService,
                               HomepageSettingsService homepageSettingsService, UserService userService) {
        this.biographyService = biographyService;
        this.concertService = concertService;
        this.photoService = photoService;
        this.trackService = trackService;
        this.videoService = videoService;
        this.homepageSettingsService = homepageSettingsService;
        this.userService = userService;
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
            .map(this::mapToTrackDTO)
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
            .map(this::mapToPhotoDTO)
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

    /**
     * Vérifier l'état d'authentification de l'utilisateur
     * Utilisé par le frontend Vue.js pour afficher les boutons Login/Profil/Admin
     */
    @Operation(
        summary = "Vérifier l'état d'authentification",
        description = "Retourne l'état de connexion de l'utilisateur, son nom d'utilisateur et ses rôles (USER, ADMIN)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "État d'authentification récupéré avec succès",
            content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/auth/status")
    public ResponseEntity<Map<String, Object>> getAuthStatus(Authentication authentication) {
        Map<String, Object> authStatus = new HashMap<>();

        if (authentication != null && authentication.isAuthenticated()
            && !authentication.getPrincipal().equals("anonymousUser")) {

            // Utilisateur connecté
            authStatus.put("authenticated", true);
            authStatus.put("username", authentication.getName());

            // Vérifier si l'utilisateur est admin
            boolean isAdmin = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_ADMIN"));

            authStatus.put("isAdmin", isAdmin);

            // Liste des rôles
            List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(role -> role.replace("ROLE_", ""))
                .collect(Collectors.toList());

            authStatus.put("roles", roles);
        } else {
            // Utilisateur non connecté
            authStatus.put("authenticated", false);
            authStatus.put("username", null);
            authStatus.put("isAdmin", false);
            authStatus.put("roles", new ArrayList<>());
        }

        return ResponseEntity.ok(authStatus);
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

    // ========================================================================
    // NOUVEAUX ENDPOINTS POUR GÉRER LA PAGE D'ACCUEIL VUE.JS
    // ========================================================================

    /**
     * Récupérer les paramètres de la page d'accueil
     */
    @Operation(
        summary = "Récupérer les paramètres de la page d'accueil",
        description = "Retourne les paramètres généraux de la page d'accueil (titres, messages, état inscription)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Paramètres récupérés avec succès",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = HomepageSettingsDTO.class)))
    })
    @GetMapping("/homepage-settings")
    public ResponseEntity<HomepageSettingsDTO> getHomepageSettings() {
        HomepageSettings settings = homepageSettingsService.getSettings();

        HomepageSettingsDTO dto = new HomepageSettingsDTO(
            settings.getHeroTitle(),
            settings.getHeroSubtitle(),
            settings.getHeroBackgroundVideoUrl(),
            settings.getWelcomeMessage(),
            settings.getRegistrationEnabled(),
            settings.getRegistrationMessage(),
            settings.getAutoRotationEnabledVideos(),
            settings.getAutoRotationEnabledTracks(),
            settings.getAutoRotationEnabledGallery()
        );

        return ResponseEntity.ok(dto);
    }

    /**
     * Récupérer les vidéos featured (1-3)
     */
    @Operation(
        summary = "Récupérer les vidéos featured",
        description = "Retourne les vidéos mises en avant sur la page d'accueil (maximum 3)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Vidéos récupérées avec succès",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = VideoDTO.class)))
    })
    @GetMapping("/featured/videos")
    public ResponseEntity<List<VideoDTO>> getFeaturedVideos() {
        HomepageSettings settings = homepageSettingsService.getSettings();

        List<VideoDTO> videos = settings.getFeaturedVideos().stream()
            .map(this::mapToVideoDTO)
            .collect(Collectors.toList());

        return ResponseEntity.ok(videos);
    }

    /**
     * Récupérer toutes les vidéos (pour la galerie complète)
     */
    @Operation(
        summary = "Récupérer toutes les vidéos",
        description = "Retourne toutes les vidéos (YouTube embeds et fichiers uploadés) pour la galerie vidéo complète"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Vidéos récupérées avec succès",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = VideoDTO.class)))
    })
    @GetMapping("/videos")
    public ResponseEntity<List<VideoDTO>> getAllVideos() {
        List<Video> videos = videoService.getAllVideos();

        List<VideoDTO> videoDTOs = videos.stream()
            .map(this::mapToVideoDTO)
            .collect(Collectors.toList());

        return ResponseEntity.ok(videoDTOs);
    }

    /**
     * Récupérer une vidéo par ID
     */
    @Operation(
        summary = "Récupérer une vidéo par ID",
        description = "Retourne une vidéo spécifique avec ses détails (compteur de likes inclus)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Vidéo récupérée avec succès",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = VideoDTO.class))),
        @ApiResponse(responseCode = "404", description = "Vidéo introuvable", content = @Content)
    })
    @GetMapping("/videos/{id}")
    public ResponseEntity<VideoDTO> getVideoById(@PathVariable Long id) {
        Video video = videoService.findById(id);

        if (video == null) {
            return ResponseEntity.notFound().build();
        }

        VideoDTO videoDTO = mapToVideoDTO(video);
        return ResponseEntity.ok(videoDTO);
    }

    /**
     * Récupérer les tracks featured (1-3)
     */
    @Operation(
        summary = "Récupérer les sons featured",
        description = "Retourne les morceaux/sons mis en avant sur la page d'accueil (maximum 3)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tracks récupérés avec succès",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TrackDTO.class)))
    })
    @GetMapping("/featured/tracks")
    public ResponseEntity<List<TrackDTO>> getFeaturedTracks() {
        HomepageSettings settings = homepageSettingsService.getSettings();

        List<TrackDTO> tracks = settings.getFeaturedTracks().stream()
            .map(this::mapToTrackDTO)
            .collect(Collectors.toList());

        return ResponseEntity.ok(tracks);
    }

    /**
     * Récupérer tous les tracks (pour la galerie complète)
     */
    @Operation(
        summary = "Récupérer tous les morceaux",
        description = "Retourne tous les morceaux (Spotify embeds et fichiers uploadés) pour la galerie musicale complète"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Morceaux récupérés avec succès",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TrackDTO.class)))
    })
    @GetMapping("/tracks")
    public ResponseEntity<List<TrackDTO>> getAllTracks() {
        List<Track> tracks = trackService.getAllTracks();

        List<TrackDTO> trackDTOs = tracks.stream()
            .map(this::mapToTrackDTO)
            .collect(Collectors.toList());

        return ResponseEntity.ok(trackDTOs);
    }

    /**
     * Récupérer un track par ID
     */
    @Operation(
        summary = "Récupérer un morceau par ID",
        description = "Retourne un morceau spécifique avec ses détails (compteur de likes inclus)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Morceau récupéré avec succès",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TrackDTO.class))),
        @ApiResponse(responseCode = "404", description = "Morceau introuvable", content = @Content)
    })
    @GetMapping("/tracks/{id}")
    public ResponseEntity<TrackDTO> getTrackById(@PathVariable Long id) {
        Track track = trackService.findById(id);

        if (track == null) {
            return ResponseEntity.notFound().build();
        }

        TrackDTO trackDTO = mapToTrackDTO(track);
        return ResponseEntity.ok(trackDTO);
    }

    /**
     * Récupérer les photos featured (1-3)
     */
    @Operation(
        summary = "Récupérer les photos featured",
        description = "Retourne les photos mises en avant sur la page d'accueil (maximum 3)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Photos récupérées avec succès",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PhotoDTO.class)))
    })
    @GetMapping("/featured/photos")
    public ResponseEntity<List<PhotoDTO>> getFeaturedPhotos() {
        HomepageSettings settings = homepageSettingsService.getSettings();

        List<PhotoDTO> photos = settings.getFeaturedPhotos().stream()
            .map(this::mapToPhotoDTO)
            .collect(Collectors.toList());

        return ResponseEntity.ok(photos);
    }

    /**
     * Récupérer toutes les photos (pour la galerie complète)
     */
    @Operation(
        summary = "Récupérer toutes les photos",
        description = "Retourne toutes les photos pour la galerie photos complète"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Photos récupérées avec succès",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PhotoDTO.class)))
    })
    @GetMapping("/photos")
    public ResponseEntity<List<PhotoDTO>> getAllPhotos() {
        List<Photo> photos = photoService.getAllPhotos();

        List<PhotoDTO> photoDTOs = photos.stream()
            .map(this::mapToPhotoDTO)
            .collect(Collectors.toList());

        return ResponseEntity.ok(photoDTOs);
    }

    /**
     * Récupérer une photo par ID
     */
    @Operation(
        summary = "Récupérer une photo par ID",
        description = "Retourne une photo spécifique avec ses détails (compteur de likes inclus)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Photo récupérée avec succès",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PhotoDTO.class))),
        @ApiResponse(responseCode = "404", description = "Photo introuvable", content = @Content)
    })
    @GetMapping("/photos/{id}")
    public ResponseEntity<PhotoDTO> getPhotoById(@PathVariable Long id) {
        Photo photo = photoService.findById(id);

        if (photo == null) {
            return ResponseEntity.notFound().build();
        }

        PhotoDTO photoDTO = mapToPhotoDTO(photo);
        return ResponseEntity.ok(photoDTO);
    }

    /**
     * Inscription d'un utilisateur depuis Vue.js
     */
    @Operation(
        summary = "Inscrire un nouvel utilisateur",
        description = "Permet l'inscription d'un utilisateur depuis le frontend Vue.js"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Inscription réussie", content = @Content),
        @ApiResponse(responseCode = "400", description = "Données invalides ou utilisateur déjà existant", content = @Content),
        @ApiResponse(responseCode = "403", description = "Inscription désactivée", content = @Content)
    })
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerUser(@Valid @RequestBody UserRegistrationDTO registrationDTO) {
        try {
            // Vérifier si l'inscription est activée
            HomepageSettings settings = homepageSettingsService.getSettings();
            if (!settings.getRegistrationEnabled()) {
                return ResponseEntity.status(403)
                    .body(Map.of("message", "L'inscription est actuellement désactivée"));
            }

            // Vérifier username unique
            if (userService.findByUsername(registrationDTO.username()) != null) {
                return ResponseEntity.status(400)
                    .body(Map.of("message", "Ce nom d'utilisateur existe déjà"));
            }

            // Vérifier email unique
            if (userService.findByEmail(registrationDTO.email()) != null) {
                return ResponseEntity.status(400)
                    .body(Map.of("message", "Cet email est déjà utilisé"));
            }

            // Créer un nouvel utilisateur
            User user = new User();
            user.setUsername(registrationDTO.username());
            user.setEmail(registrationDTO.email());
            user.setPassword(registrationDTO.password());

            userService.saveUser(user);

            return ResponseEntity.status(201)
                .body(Map.of(
                    "message", "Inscription réussie !",
                    "username", user.getUsername(),
                    "redirectUrl", "/login"
                ));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400)
                .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("message", "Erreur serveur lors de l'inscription"));
        }
    }

    // ========================================================================
    // MÉTHODES HELPER POUR ÉVITER LA DUPLICATION DE CODE (DRY PRINCIPLE)
    // ========================================================================

    /**
     * Convertit une Track en TrackDTO avec gestion des URLs
     * Méthode helper pour éviter la duplication de code dans getFeaturedTracks, getAllTracks, et getDiscography
     *
     * @param track L'entité Track à convertir
     * @return TrackDTO avec les URLs correctement construites
     */
    private TrackDTO mapToTrackDTO(Track track) {
        String audioUrl = null;
        String spotifyUrl = null;

        if (TrackType.EMBED.equals(track.getTrackType())) {
            spotifyUrl = track.getEmbedCode();
        } else if (TrackType.UPLOADED_FILE.equals(track.getTrackType()) && track.getFilename() != null) {
            audioUrl = "/uploaded-music/" + track.getFilename();
        }

        return new TrackDTO(
            track.getId(),
            track.getTitle(),
            null, // trackNumber
            null, // duration
            audioUrl,
            spotifyUrl,
            track.getLikeCount() != null ? track.getLikeCount() : 0,
            track.getPlayCount() != null ? track.getPlayCount().intValue() : 0
        );
    }

    /**
     * Convertit une Photo en PhotoDTO avec construction des URLs
     * Méthode helper pour éviter la duplication de code dans getFeaturedPhotos, getAllPhotos, et getGallery
     *
     * @param photo L'entité Photo à convertir
     * @return PhotoDTO avec les URLs correctement construites
     */
    private PhotoDTO mapToPhotoDTO(Photo photo) {
        return new PhotoDTO(
            photo.getId(),
            "/uploaded-photos/" + photo.getFilename(),
            "/uploaded-photos/" + photo.getFilename(), // thumbnail = same for now
            null, // caption
            "concert", // category par défaut
            photo.getDisplayOrder(),
            photo.getLikeCount() != null ? photo.getLikeCount() : 0,
            photo.getViewCount() != null ? photo.getViewCount().intValue() : 0
        );
    }

    /**
     * Convertit une Video en VideoDTO avec gestion des URLs
     * Méthode helper pour éviter la duplication de code dans getFeaturedVideos et getAllVideos
     *
     * @param video L'entité Video à convertir
     * @return VideoDTO avec les URLs correctement construites
     */
    private VideoDTO mapToVideoDTO(Video video) {
        return new VideoDTO(
            video.getId(),
            video.getTitle(),
            video.getVideoType().toString(),
            video.getEmbedCode(),
            video.getFilename() != null ? "/uploaded-videos/" + video.getFilename() : null,
            video.getLikeCount() != null ? video.getLikeCount() : 0,
            video.getViewCount() != null ? video.getViewCount().intValue() : 0
        );
    }
}
