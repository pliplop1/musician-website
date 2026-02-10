# 🎯 RÉPONSES AUX QUESTIONS PIÈGES DU JURY

**Document préparé suite aux retours d'une professionnelle**

---

## ❓ QUESTION 1 : "La recherche dans la galerie ne fonctionne pas. Pouvez-vous l'implémenter ?"

### ✅ STRATÉGIE DE RÉPONSE

**Soyez honnête mais montrez que vous savez comment faire :**

> "Actuellement, la fonctionnalité de recherche dans la galerie n'est pas implémentée. J'ai priorisé les fonctionnalités essentielles du MVP dans le temps imparti. Cependant, si je devais l'implémenter, voici mon approche technique."

---

### 🔧 IMPLÉMENTATION TECHNIQUE (à expliquer oralement)

#### **Étape 1 : Backend - Repository**

> "Côté backend, j'ajouterais une méthode de recherche dans `PhotoRepository` :"

```java
@Repository
public interface PhotoRepository extends JpaRepository<Photo, Long> {

    /**
     * Recherche de photos par mot-clé dans titre, description ou catégorie
     * @param keyword Le mot-clé à rechercher
     * @return Liste des photos correspondantes
     */
    @Query("SELECT p FROM Photo p WHERE " +
           "LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.category) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Photo> searchPhotos(@Param("keyword") String keyword);

    // Alternative avec méthode Spring Data JPA dérivée
    List<Photo> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
        String title, String description
    );
}
```

#### **Étape 2 : Backend - Service**

```java
@Service
public class PhotoService {

    @Autowired
    private PhotoRepository photoRepository;

    /**
     * Recherche de photos par mot-clé
     */
    public List<PhotoDTO> searchPhotos(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllPhotos(); // Retourne toutes les photos si recherche vide
        }

        List<Photo> photos = photoRepository.searchPhotos(keyword.trim());

        return photos.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
}
```

#### **Étape 3 : Backend - Controller REST**

```java
@RestController
@RequestMapping("/api/photos")
public class PhotoApiController {

    @Autowired
    private PhotoService photoService;

    /**
     * Endpoint de recherche de photos
     * GET /api/photos/search?q=concert
     */
    @GetMapping("/search")
    public ResponseEntity<List<PhotoDTO>> searchPhotos(
            @RequestParam(required = false) String q) {

        List<PhotoDTO> results = photoService.searchPhotos(q);
        return ResponseEntity.ok(results);
    }
}
```

#### **Étape 4 : Frontend - Vue.js Component**

```vue
<template>
  <div class="gallery-search">
    <!-- Barre de recherche -->
    <div class="search-bar">
      <input
        v-model="searchQuery"
        @input="debouncedSearch"
        type="text"
        placeholder="Rechercher une photo..."
        class="search-input"
      />
      <i class="fas fa-search"></i>
    </div>

    <!-- Résultats -->
    <div v-if="searchQuery" class="search-results">
      <p>{{ filteredPhotos.length }} résultat(s) pour "{{ searchQuery }}"</p>
    </div>

    <!-- Galerie -->
    <div class="photo-grid">
      <div v-for="photo in filteredPhotos" :key="photo.id" class="photo-item">
        <img :src="photo.url" :alt="photo.title" />
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';
import { debounce } from 'lodash'; // Pour éviter trop de requêtes

export default {
  name: 'PhotoGallery',
  data() {
    return {
      searchQuery: '',
      allPhotos: [],
      filteredPhotos: []
    }
  },
  mounted() {
    this.loadPhotos();
  },
  created() {
    // Debounce pour attendre que l'utilisateur finisse de taper
    this.debouncedSearch = debounce(this.searchPhotos, 300);
  },
  methods: {
    async loadPhotos() {
      try {
        const response = await axios.get('/api/photos');
        this.allPhotos = response.data;
        this.filteredPhotos = this.allPhotos;
      } catch (error) {
        console.error('Erreur chargement photos:', error);
      }
    },

    async searchPhotos() {
      if (this.searchQuery.length === 0) {
        // Si recherche vide, afficher toutes les photos
        this.filteredPhotos = this.allPhotos;
        return;
      }

      if (this.searchQuery.length < 2) {
        // Attendre au moins 2 caractères
        return;
      }

      try {
        const response = await axios.get(`/api/photos/search?q=${this.searchQuery}`);
        this.filteredPhotos = response.data;
      } catch (error) {
        console.error('Erreur recherche:', error);
      }
    }
  }
}
</script>
```

#### **Étape 5 : Tests unitaires**

```java
@SpringBootTest
class PhotoServiceSearchTest {

    @Autowired
    private PhotoService photoService;

    @Autowired
    private PhotoRepository photoRepository;

    @Test
    @DisplayName("Recherche de photos par titre")
    void testSearchPhotosByTitle() {
        // Arrange
        Photo photo1 = new Photo();
        photo1.setTitle("Concert à Paris");
        photo1.setDescription("Belle soirée");
        photoRepository.save(photo1);

        Photo photo2 = new Photo();
        photo2.setTitle("Concert à Lyon");
        photo2.setDescription("Super ambiance");
        photoRepository.save(photo2);

        // Act
        List<PhotoDTO> results = photoService.searchPhotos("Paris");

        // Assert
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getTitle()).contains("Paris");
    }

    @Test
    @DisplayName("Recherche insensible à la casse")
    void testSearchCaseInsensitive() {
        Photo photo = new Photo();
        photo.setTitle("CONCERT");
        photoRepository.save(photo);

        List<PhotoDTO> results = photoService.searchPhotos("concert");

        assertThat(results).isNotEmpty();
    }

    @Test
    @DisplayName("Recherche vide retourne toutes les photos")
    void testSearchEmptyReturnsAll() {
        photoRepository.save(new Photo());
        photoRepository.save(new Photo());

        List<PhotoDTO> results = photoService.searchPhotos("");

        assertThat(results).hasSize(2);
    }
}
```

---

### 🎯 POURQUOI VOUS NE L'AVEZ PAS FAIT

**Ce que vous devez dire au jury :**

> "J'ai fait des choix de priorisation dans mon développement :"

1. **Priorité 1 - Fonctionnalités critiques** :
   - Authentification sécurisée
   - CRUD complet (concerts, photos, tracks)
   - Interface admin fonctionnelle
   - Formulaire de contact avec email
   - Tests (545 tests, 99,8% de succès)
   - CI/CD automatisé

2. **Priorité 2 - Qualité du code** :
   - Architecture propre (3-tier)
   - Sécurité (Spring Security, BCrypt, CSRF)
   - Documentation (JavaDoc, README)
   - Performance (Lighthouse 95/100)

3. **Priorité 3 - Fonctionnalités "nice to have"** :
   - Recherche dans la galerie ← **ICI**
   - Filtres avancés
   - Système de favoris

> "La recherche est une amélioration importante, mais pas critique pour le MVP. Dans un contexte Agile, cette user story serait dans le backlog pour le sprint suivant."

---

## ❓ QUESTION 2 : "Vous n'avez pas de backup automatique. Que se passe-t-il si vos conteneurs plantent ?"

### ✅ STRATÉGIE DE RÉPONSE

**Expliquez ce qui est déjà en place, puis ce qui manque :**

> "Excellente question de sécurité. J'ai deux niveaux de protection actuellement, mais vous avez raison qu'un système de backup automatique serait nécessaire en production."

---

### 🛡️ CE QUI EST DÉJÀ EN PLACE

#### **1. Volumes Docker persistants**

> "Les données sont persistées dans des volumes Docker, même si les conteneurs sont supprimés :"

```yaml
# docker-compose.yml
volumes:
  db-data:
    driver: local
    name: musician-db-data
  app-uploads:
    driver: local
    name: musician-uploads
```

**Démonstration :**

```bash
# Même si je supprime les conteneurs
docker-compose down

# Les volumes restent
docker volume ls | grep musician

# musician-db-data    ← Toujours là !
# musician-uploads    ← Toujours là !

# Quand je redémarre
docker-compose up -d

# Les données sont restaurées automatiquement
```

#### **2. Health checks automatiques**

> "J'ai configuré des health checks qui redémarrent automatiquement les conteneurs défaillants :"

```yaml
# docker-compose.yml
healthcheck:
  test: ["CMD", "healthcheck.sh", "--connect", "--innodb_initialized"]
  interval: 10s
  timeout: 5s
  retries: 5
```

```yaml
# Pour l'application
healthcheck:
  test: ["CMD", "wget", "--quiet", "--tries=1", "--spider", "http://localhost:8080/actuator/health"]
  interval: 30s
  timeout: 10s
  retries: 3
  start_period: 60s
```

#### **3. Politique de redémarrage**

```yaml
restart: unless-stopped
```

> "Si le conteneur plante, Docker le redémarre automatiquement."

---

### 🚨 CE QUI MANQUE (et comment l'implémenter)

#### **Solution 1 : Backup automatique quotidien avec script Bash**

> "En production, je mettrais en place un script de backup automatique :"

**Fichier : `scripts/backup.sh`**

```bash
#!/bin/bash
#
# Backup automatique de la base de données et des uploads
#

set -e  # Arrêter si erreur

# Configuration
BACKUP_DIR="/backups"
DATE=$(date +%Y%m%d_%H%M%S)
RETENTION_DAYS=7

# Créer le dossier si n'existe pas
mkdir -p $BACKUP_DIR

echo "🔄 Démarrage du backup - $DATE"

# 1. Backup de la base de données MariaDB
echo "📦 Backup de la base de données..."
docker exec musician-db mysqldump \
  --user=root \
  --password=$DB_ROOT_PASSWORD \
  --single-transaction \
  --quick \
  --lock-tables=false \
  musician_db > $BACKUP_DIR/db_backup_$DATE.sql

echo "✅ Base de données sauvegardée : db_backup_$DATE.sql"

# 2. Backup des volumes (uploads)
echo "📦 Backup des fichiers uploadés..."
docker run --rm \
  -v musician-uploads:/data:ro \
  -v $BACKUP_DIR:/backup \
  alpine tar czf /backup/uploads_$DATE.tar.gz /data

echo "✅ Uploads sauvegardés : uploads_$DATE.tar.gz"

# 3. Compression du backup SQL
gzip $BACKUP_DIR/db_backup_$DATE.sql
echo "✅ Backup compressé"

# 4. Nettoyage des vieux backups (garder seulement les 7 derniers jours)
echo "🧹 Nettoyage des anciens backups (> $RETENTION_DAYS jours)..."
find $BACKUP_DIR -name "db_backup_*.sql.gz" -mtime +$RETENTION_DAYS -delete
find $BACKUP_DIR -name "uploads_*.tar.gz" -mtime +$RETENTION_DAYS -delete

echo "✅ Backup terminé avec succès !"

# 5. (Optionnel) Upload vers S3 / Azure Blob
# aws s3 cp $BACKUP_DIR/db_backup_$DATE.sql.gz s3://my-backups/
```

**Planification avec cron :**

```bash
# Éditer crontab
crontab -e

# Ajouter cette ligne (backup tous les jours à 2h du matin)
0 2 * * * /path/to/backup.sh >> /var/log/backup.log 2>&1
```

#### **Solution 2 : Conteneur de backup automatique**

> "Ou j'utiliserais un conteneur dédié au backup :"

**Ajout dans `docker-compose.yml` :**

```yaml
services:
  # ... autres services ...

  backup:
    image: offen/docker-volume-backup:v2
    container_name: musician-backup
    restart: unless-stopped
    volumes:
      - db-data:/backup/db-data:ro
      - app-uploads:/backup/uploads:ro
      - ./backups:/archive
    environment:
      BACKUP_CRON_EXPRESSION: "0 2 * * *"      # Tous les jours à 2h
      BACKUP_RETENTION_DAYS: "7"                # Garder 7 jours
      BACKUP_FILENAME: "backup-%Y%m%d-%H%M%S.tar.gz"
      BACKUP_PRUNING_PREFIX: "backup-"
      BACKUP_ARCHIVE: "/archive"
    networks:
      - musician-network
```

#### **Solution 3 : Réplication MariaDB (haute disponibilité)**

> "Pour une solution vraiment robuste, je mettrais en place une réplication master-slave :"

```yaml
services:
  database-master:
    image: mariadb:10.11
    environment:
      MYSQL_ROOT_PASSWORD: ${DB_ROOT_PASSWORD}
      # Configuration master
    command: --server-id=1 --log-bin=mysql-bin --binlog-format=ROW

  database-slave:
    image: mariadb:10.11
    depends_on:
      - database-master
    environment:
      MYSQL_ROOT_PASSWORD: ${DB_ROOT_PASSWORD}
      # Configuration slave
    command: --server-id=2 --relay-log=mysql-relay-bin --log-bin=mysql-bin
```

#### **Solution 4 : Stockage cloud des backups**

> "Les backups locaux ne suffisent pas. Je les enverrais vers le cloud :"

**Avec AWS S3 :**

```bash
# Après le backup local
aws s3 cp $BACKUP_DIR/db_backup_$DATE.sql.gz \
  s3://musician-backups/db/ \
  --storage-class GLACIER  # Stockage à long terme économique
```

**Avec Azure Blob Storage :**

```bash
az storage blob upload \
  --account-name musicianbackups \
  --container-name database-backups \
  --file $BACKUP_DIR/db_backup_$DATE.sql.gz \
  --name db_backup_$DATE.sql.gz
```

---

### 🔄 PROCÉDURE DE RESTAURATION

> "Et bien sûr, un backup sans procédure de restauration testée ne sert à rien. Voici comment je restaurerais :"

**Restauration de la base de données :**

```bash
#!/bin/bash
# restore.sh

BACKUP_FILE=$1

if [ -z "$BACKUP_FILE" ]; then
  echo "Usage: ./restore.sh backup_file.sql.gz"
  exit 1
fi

# Décompresser
gunzip -c $BACKUP_FILE > /tmp/restore.sql

# Restaurer
docker exec -i musician-db mysql \
  --user=root \
  --password=$DB_ROOT_PASSWORD \
  musician_db < /tmp/restore.sql

echo "✅ Base de données restaurée avec succès"
```

**Restauration des uploads :**

```bash
# Extraire l'archive
docker run --rm \
  -v musician-uploads:/data \
  -v ./backups:/backup \
  alpine tar xzf /backup/uploads_20250101_020000.tar.gz -C /
```

---

### 📊 MONITORING ET ALERTES

> "En plus des backups, je mettrais en place du monitoring :"

**Avec Prometheus + Grafana :**

```yaml
services:
  prometheus:
    image: prom/prometheus
    volumes:
      - ./monitoring/prometheus.yml:/etc/prometheus/prometheus.yml
    ports:
      - "9090:9090"

  grafana:
    image: grafana/grafana
    ports:
      - "3000:3000"
    environment:
      GF_SECURITY_ADMIN_PASSWORD: admin
```

**Alertes par email si conteneur down :**

```yaml
# prometheus/alert.rules
groups:
  - name: docker_alerts
    rules:
      - alert: ContainerDown
        expr: up{job="docker"} == 0
        for: 1m
        annotations:
          summary: "Container {{ $labels.instance }} is down"
```

---

### 🎯 CONCLUSION

**Ce que vous devez dire au jury :**

> "Pour résumer :"

✅ **Actuellement en place :**
- Volumes Docker persistants
- Health checks avec redémarrage automatique
- Politique de restart

❌ **Manque (production) :**
- Backup automatique quotidien
- Stockage des backups hors serveur
- Réplication de la base de données
- Monitoring avec alertes

> "C'est un excellent point que vous soulevez. En environnement de développement, les volumes Docker suffisent. Mais en production, j'implémenterais une des solutions que je viens de présenter, probablement le conteneur de backup automatique avec upload S3, car c'est un bon équilibre entre simplicité et robustesse."

---

## 💡 AUTRES QUESTIONS PIÈGES POSSIBLES

### Q : "Pourquoi pas de tests E2E ?"

**Réponse :**

> "J'ai des tests E2E avec Playwright dans mon pipeline CI/CD. Regardez le fichier `.github/workflows/ci.yml`. Ils testent les parcours utilisateur complets : inscription, connexion, création de concert, etc."

### Q : "Votre application est-elle scalable ?"

**Réponse :**

> "L'architecture 3-tier que j'ai choisie facilite la scalabilité horizontale. Je pourrais ajouter plusieurs instances de l'application derrière un load balancer (Nginx ou Traefik), car elle est stateless. La session est gérée par Spring Security avec un cookie, donc compatible avec du load balancing. Pour scaler encore plus, je migrerais les sessions vers Redis."

### Q : "Que se passe-t-il si la base de données devient trop grosse ?"

**Réponse :**

> "Plusieurs stratégies possibles :"
> 1. **Indexation** : J'ai déjà des index sur les clés étrangères, mais j'ajouterais des index sur les colonnes fréquemment recherchées (date des concerts, catégorie des photos)
> 2. **Partitionnement** : Partitionner la table concerts par année
> 3. **Archivage** : Déplacer les vieux concerts vers une table d'archive
> 4. **Réplication read-only** : Base de données secondaire pour les lectures

### Q : "Votre code est-il sécurisé ?"

**Réponse :**

> "J'ai mis en place plusieurs couches de sécurité :"
> - Spring Security (authentification + autorisation)
> - Mots de passe BCrypt (jamais en clair)
> - Protection CSRF
> - Protection XSS (Thymeleaf échappe le HTML)
> - Validation des entrées (Bean Validation)
> - HTTPS en production
> - Headers de sécurité (CSP, X-Frame-Options)
> - Rate limiting sur le login (max 5 tentatives)
> - Scan de sécurité Trivy dans le CI/CD

---

## 📋 CHECKLIST AVANT L'ORAL

**Relire ce document et être prêt à :**

- [ ] Expliquer comment implémenter une recherche (backend + frontend + tests)
- [ ] Expliquer la stratégie de backup (volumes + scripts + cloud)
- [ ] Démontrer que les volumes persistent après `docker-compose down`
- [ ] Montrer les health checks dans docker-compose.yml
- [ ] Parler de scalabilité, monitoring, alertes

**Phrases clés à retenir :**

1. "C'est une excellente remarque. Voici comment je l'implémenterais..."
2. "J'ai fait des choix de priorisation pour respecter le temps imparti..."
3. "En production, j'ajouterais..."
4. "Dans un contexte Agile, cette fonctionnalité serait dans le backlog..."

---

## 🚀 VOUS ÊTES PRÊT !

Avec ce document, vous pouvez transformer une faiblesse apparente en démonstration de vos compétences techniques.

**Le jury ne cherche pas un projet parfait. Il cherche quelqu'un qui :**
✅ Sait identifier les problèmes
✅ Sait proposer des solutions techniques
✅ Comprend les priorités (MVP vs nice-to-have)
✅ Connaît les bonnes pratiques (backup, monitoring, sécurité)

**BON COURAGE ! 💪**
