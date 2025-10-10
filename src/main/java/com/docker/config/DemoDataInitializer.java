package com.docker.config;

import com.docker.entity.Biography;
import com.docker.entity.Concert;
import com.docker.entity.Photo;
import com.docker.entity.Track;
import com.docker.entity.TrackType;
import com.docker.repository.BiographyRepository;
import com.docker.repository.ConcertRepository;
import com.docker.repository.PhotoRepository;
import com.docker.repository.TrackRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * Initialisation des données de démonstration pour le Duo Black & White
 * S'exécute uniquement en profil 'dev'
 */
@Component
@Profile("dev")
public class DemoDataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DemoDataInitializer.class);

    private final BiographyRepository biographyRepository;
    private final ConcertRepository concertRepository;
    private final PhotoRepository photoRepository;
    private final TrackRepository trackRepository;

    public DemoDataInitializer(BiographyRepository biographyRepository,
                                ConcertRepository concertRepository,
                                PhotoRepository photoRepository,
                                TrackRepository trackRepository) {
        this.biographyRepository = biographyRepository;
        this.concertRepository = concertRepository;
        this.photoRepository = photoRepository;
        this.trackRepository = trackRepository;
    }

    @Override
    public void run(String... args) {
        logger.info("🎵 Initialisation des données de démonstration...");

        initBiography();
        initConcerts();
        initPhotos();
        initTracks();

        logger.info("✅ Données de démonstration initialisées avec succès");
    }

    private void initBiography() {
        if (biographyRepository.count() == 0 ||
            (biographyRepository.findById(1L).isPresent() &&
             biographyRepository.findById(1L).get().getContent().isEmpty())) {

            String bioContent = """
                # Duo Black & White - L'harmonie parfaite entre tradition et modernité

                Depuis plus de 10 ans, le **Duo Black & White** enchante le public avec son répertoire varié
                alliant chansons françaises intemporelles, standards internationaux et créations originales.

                ## L'Histoire du Duo

                Née d'une rencontre musicale en 2015 à Abbeville, l'alchimie entre **Marilyne Dumoulin**,
                chanteuse à la voix envoûtante, et **Philippe Prudhomme**, guitariste virtuose et chanteur
                talentueux, était évidente dès les premières notes.

                Leur nom "Black & White" symbolise la complémentarité parfaite de leurs univers musicaux :
                la puissance et la douceur, le classique et le contemporain, l'ombre et la lumière.

                ## Notre Répertoire

                Notre setlist est soigneusement élaborée pour créer une ambiance unique lors de vos événements :

                - **Chansons françaises** : Édith Piaf, Jacques Brel, Charles Aznavour, Françoise Hardy
                - **Variété internationale** : The Beatles, Simon & Garfunkel, Norah Jones, Adele
                - **Jazz et Soul** : Ella Fitzgerald, Nina Simone, Amy Winehouse
                - **Pop moderne** : Coldplay, Ed Sheeran, John Legend

                ## Notre Philosophie

                Chaque prestation est unique et adaptée à votre événement. Nous accordons une attention
                particulière à l'ambiance et aux émotions que nous souhaitons partager avec votre public.

                Notre approche acoustique privilégie l'authenticité et la proximité avec le public,
                créant ainsi des moments intimes et mémorables.

                ## Nos Prestations

                Le Duo Black & White se produit dans toute la France et en Belgique pour :

                - 💍 **Mariages** : de la cérémonie au vin d'honneur
                - 🎉 **Événements privés** : anniversaires, soirées d'entreprise
                - 🎵 **Concerts** : salles de spectacle, festivals, bars musicaux
                - 🍷 **Animations** : restaurants, cocktails, vernissages

                ## Distinctions

                - 🏆 Prix du Public - Festival de Musique d'Abbeville 2019
                - 🎖️ Coup de Coeur - Scènes Picardes 2020
                - ⭐ Plus de 200 prestations réalisées avec succès

                *"La musique est le langage universel de l'âme. Notre mission est de créer des souvenirs
                inoubliables à travers des mélodies qui touchent le cœur."* - Duo Black & White
                """;

            Biography bio = biographyRepository.findById(1L).orElse(new Biography());
            bio.setContent(bioContent);
            biographyRepository.save(bio);

            logger.info("✓ Biographie initialisée");
        }
    }

    private void initConcerts() {
        if (concertRepository.count() == 0) {
            // Concerts passés récents
            Concert past1 = new Concert();
            past1.setLocation("Le Grand Rex - Paris");
            past1.setDate(LocalDate.now().minusMonths(3));
            past1.setDescription("Concert acoustique exceptionnel en première partie de Carla Bruni");
            concertRepository.save(past1);

            Concert past2 = new Concert();
            past2.setLocation("Festival Jazz & Blues - Marciac");
            past2.setDate(LocalDate.now().minusMonths(5));
            past2.setDescription("Participation au festival prestigieux de jazz");
            concertRepository.save(past2);

            Concert past3 = new Concert();
            past3.setLocation("Mariage Château de Versailles");
            past3.setDate(LocalDate.now().minusMonths(1));
            past3.setDescription("Prestation privée pour un mariage d'exception");
            concertRepository.save(past3);

            // Concerts à venir
            Concert upcoming1 = new Concert();
            upcoming1.setLocation("Salle des Fêtes d'Abbeville");
            upcoming1.setDate(LocalDate.now().plusWeeks(2));
            upcoming1.setDescription("Concert de Noël - Soirée caritative au profit des enfants hospitalisés");
            concertRepository.save(upcoming1);

            Concert upcoming2 = new Concert();
            upcoming2.setLocation("Le Botanique - Bruxelles (Belgique)");
            upcoming2.setDate(LocalDate.now().plusMonths(1));
            upcoming2.setDescription("Tournée belge - Première date à Bruxelles");
            concertRepository.save(upcoming2);

            Concert upcoming3 = new Concert();
            upcoming3.setLocation("Festival des Traditions - Amiens");
            upcoming3.setDate(LocalDate.now().plusMonths(2));
            upcoming3.setDescription("Concert en plein air - Célébration de la musique picarde");
            concertRepository.save(upcoming3);

            Concert upcoming4 = new Concert();
            upcoming4.setLocation("Zénith de Lille");
            upcoming4.setDate(LocalDate.now().plusMonths(3));
            upcoming4.setDescription("Grande tournée régionale - Soirée acoustique");
            concertRepository.save(upcoming4);

            Concert upcoming5 = new Concert();
            upcoming5.setLocation("Festival d'Été - Dinant (Belgique)");
            upcoming5.setDate(LocalDate.now().plusMonths(5));
            upcoming5.setDescription("Festival international - Concert en bord de Meuse");
            concertRepository.save(upcoming5);

            logger.info("✓ {} concerts initialisés", concertRepository.count());
        }
    }

    private void initPhotos() {
        if (photoRepository.count() == 0) {
            // Photos de concert
            Photo photo1 = new Photo();
            photo1.setFilename("demo-concert-1.jpg");
            photo1.setDisplayOrder(1);
            photoRepository.save(photo1);

            Photo photo2 = new Photo();
            photo2.setFilename("demo-concert-2.jpg");
            photo2.setDisplayOrder(2);
            photoRepository.save(photo2);

            Photo photo3 = new Photo();
            photo3.setFilename("demo-concert-3.jpg");
            photo3.setDisplayOrder(3);
            photoRepository.save(photo3);

            // Photos du duo
            Photo photo4 = new Photo();
            photo4.setFilename("demo-duo-1.jpg");
            photo4.setDisplayOrder(4);
            photoRepository.save(photo4);

            Photo photo5 = new Photo();
            photo5.setFilename("demo-duo-2.jpg");
            photo5.setDisplayOrder(5);
            photoRepository.save(photo5);

            Photo photo6 = new Photo();
            photo6.setFilename("demo-instruments.jpg");
            photo6.setDisplayOrder(6);
            photoRepository.save(photo6);

            logger.info("✓ {} photos initialisées", photoRepository.count());
        }
    }

    private void initTracks() {
        if (trackRepository.count() == 0) {
            // Morceaux avec embed Spotify (fictifs)
            Track track1 = new Track();
            track1.setTitle("La Vie en Rose");
            track1.setTrackType(TrackType.EMBED);
            track1.setEmbedCode("https://open.spotify.com/embed/track/7FX7JIdCmPTUy0a5XT65Hd");
            trackRepository.save(track1);

            Track track2 = new Track();
            track2.setTitle("Ne Me Quitte Pas");
            track2.setTrackType(TrackType.EMBED);
            track2.setEmbedCode("https://open.spotify.com/embed/track/3LTCjy5b3EhSAEhIpx6QhL");
            trackRepository.save(track2);

            Track track3 = new Track();
            track3.setTitle("Autumn Leaves");
            track3.setTrackType(TrackType.EMBED);
            track3.setEmbedCode("https://open.spotify.com/embed/track/5Hd4oZIZqV3FNWc1Ei6bXY");
            trackRepository.save(track3);

            Track track4 = new Track();
            track4.setTitle("Summertime");
            track4.setTrackType(TrackType.EMBED);
            track4.setEmbedCode("https://open.spotify.com/embed/track/2yVTGBiYzk9g4Y8aHVKdKH");
            trackRepository.save(track4);

            Track track5 = new Track();
            track5.setTitle("Feeling Good");
            track5.setTrackType(TrackType.EMBED);
            track5.setEmbedCode("https://open.spotify.com/embed/track/7KXjTSCq5nL1LoYtL7XAwS");
            trackRepository.save(track5);

            logger.info("✓ {} morceaux initialisés", trackRepository.count());
        }
    }
}
