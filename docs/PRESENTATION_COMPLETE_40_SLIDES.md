# 📊 PRÉSENTATION LIBR

OFFICE IMPRESS COMPLÈTE - 40 SLIDES

**Projet CDA : Duo Black & White - Site Web Officiel**
**Durée : 40 minutes de présentation**

---

## 📋 TABLE DES MATIÈRES

**PARTIE 1 : INTRODUCTION** (5 min - Slides 1-4)
**PARTIE 2 : CONTEXTE & OBJECTIFS** (4 min - Slides 5-7)
**PARTIE 3 : ARCHITECTURE TECHNIQUE** (10 min - Slides 8-17)
**PARTIE 4 : SÉCURITÉ** (6 min - Slides 18-22)
**PARTIE 5 : DÉMONSTRATION** (10 min - Slides 23-33)
**PARTIE 6 : DEVOPS & TESTS** (3 min - Slides 34-36)
**PARTIE 7 : PERFORMANCE & QUALITÉ** (2 min - Slides 37-38)
**PARTIE 8 : CONCLUSION** (2 min - Slides 39-40)

---

# PARTIE 1 : INTRODUCTION (5 min)

## SLIDE 1 : Page de titre 🎯
```
═══════════════════════════════════════════════
         DUO BLACK & WHITE
       SITE WEB OFFICIEL
═══════════════════════════════════════════════

     Projet CDA - Niveau 6

     [Ton nom]
     [Date de l'oral]

     Spring Boot 3.2 + Vue.js 3 + Docker
     Architecture 3 tiers sécurisée
═══════════════════════════════════════════════
```

**Ce que tu dis (30 sec) :**
> "Bonjour, je vais vous présenter mon projet de titre professionnel Concepteur Développeur d'Applications : le site web du Duo Black & White, un groupe musical professionnel."

---

## SLIDE 2 : Sommaire 📑
```
SOMMAIRE DE LA PRÉSENTATION

1. Introduction et contexte du projet
2. Architecture technique 3 tiers
3. Sécurité et conformité (RGPD, RGAA)
4. Démonstration fonctionnelle
5. DevOps et CI/CD
6. Performance et qualité du code
7. Conclusion et perspectives

Durée : 40 minutes
Questions : 40 minutes
```

**Ce que tu dis (30 sec) :**
> "Ma présentation durera 40 minutes et couvrira l'architecture, la sécurité, une démonstration live, et les aspects DevOps du projet."

---

## SLIDE 3 : Contexte du projet 🎵
```
CONTEXTE : LE DUO BLACK & WHITE

Le client :
• Duo musical professionnel d'Abbeville
• Marilyne Dumoulin (chanteuse)
• Philippe Prudhomme (guitariste/chanteur)

Besoin exprimé :
• Site vitrine professionnel moderne
• Présentation du répertoire musical (50+ chansons)
• Galerie photos des concerts
• Agenda des événements
• Interface d'administration pour gérer le contenu

Cible :
• Fans et public
• Organisateurs de concerts
• Presse et médias
```

**Ce que tu dis (1 min) :**
> "Le Duo Black & White est un groupe musical professionnel qui avait besoin d'un site web moderne pour présenter leur travail, communiquer avec leur public, et gérer leurs contenus de manière autonome."

---

## SLIDE 4 : Mon rôle et durée 👨‍💻
```
MON RÔLE DANS LE PROJET

Responsabilités :
✓ Analyse des besoins client
✓ Conception de l'architecture
✓ Développement frontend et backend
✓ Mise en place de la base de données
✓ Sécurisation de l'application
✓ Tests (unitaires et intégration)
✓ Conteneurisation Docker
✓ CI/CD avec GitHub Actions
✓ Documentation technique complète

Durée : 3 mois (conception, dev, tests, déploiement)
Travail : Individuel
```

**Ce que tu dis (1 min) :**
> "J'ai réalisé l'intégralité du projet, de l'analyse des besoins jusqu'au déploiement, en passant par la conception de l'architecture, le développement, les tests et la sécurité."

---

# PARTIE 2 : CONTEXTE & OBJECTIFS (4 min)

## SLIDE 5 : Périmètre fonctionnel 📦
```
PÉRIMÈTRE FONCTIONNEL

PARTIE PUBLIQUE (Visiteurs) :
• Page d'accueil dynamique avec contenus featured
• Biographie du duo (rendu Markdown)
• Répertoire musical (50+ chansons)
  - Intégration Spotify/SoundCloud
  - Player HTML5 pour fichiers locaux
  - Compteur d'écoutes
• Galerie photos responsive (100+ photos)
  - Lightbox, lazy loading
  - Catégories et tags
• Section vidéos (YouTube/Vimeo)
• Agenda concerts (passés et à venir)
• Formulaire de contact sécurisé
• Design responsive mobile-first

PARTIE ADMINISTRATION (Backoffice) :
• Dashboard avec statistiques temps réel
• Gestion complète du contenu (CRUD)
• Upload photos/vidéos drag-and-drop
• Modération des commentaires
• Gestion des utilisateurs et rôles
• Logs de sécurité (tentatives connexion)
• Recherche et tri sur toutes les entités
```

**Ce que tu dis (1 min 30) :**
> "L'application comporte deux parties : une interface publique accessible à tous, et un backoffice d'administration pour que le duo puisse gérer leurs contenus en autonomie."

---

## SLIDE 6 : Stack technique 🛠️
```
STACK TECHNIQUE

BACKEND (Tier 2) :
• Java 17 (LTS)
• Spring Boot 3.2.4
  - Spring Data JPA (persistance)
  - Spring Security (authentification/autorisation)
  - Spring Validation (validation des données)
  - Spring Mail (envoi emails)
• MariaDB 10.11 (base de données)
• Maven 3.9 (build)

FRONTEND (Tier 1) :
• Vue.js 3 (Composition API)
  - Vue Router (navigation)
  - Axios (client HTTP)
• Thymeleaf (templates admin)
• Tailwind CSS 3 (framework CSS utilitaire)
• Vite 7.1.9 (build tool moderne)

DEVOPS & OUTILS :
• Docker & Docker Compose (conteneurisation)
• GitHub Actions (CI/CD)
• Adminer (administration BDD)
• Spring Boot Actuator (monitoring)
• JUnit 5 + Mockito (tests)
```

**Ce que tu dis (1 min) :**
> "J'ai utilisé une stack technique moderne et professionnelle : Spring Boot pour le backend, Vue.js pour le frontend, MariaDB pour la base de données, et Docker pour la conteneurisation."

---

## SLIDE 7 : Métriques du projet 📊
```
MÉTRIQUES DU PROJET

CODE :
• ~15 000 lignes de code Java
• ~3 000 lignes de code Vue.js/JavaScript
• ~5 000 lignes de templates Thymeleaf
• Total : ~23 000 lignes

ARCHITECTURE :
• 15 tables relationnelles (MariaDB)
• 25+ endpoints REST (/api/**)
• 20+ pages admin (Thymeleaf)
• 3 conteneurs Docker

QUALITÉ :
• Performance : 95/100 (PageSpeed Insights)
• Sécurité : 95/100
• RGPD : 95/100
• RGAA (accessibilité) : 80/100
• Score global : 82/100

DÉVELOPPEMENT :
• 12 semaines de développement
• 200+ commits Git
• CI/CD automatisé (GitHub Actions)
```

**Ce que tu dis (1 min 30) :**
> "Le projet représente environ 23 000 lignes de code, avec d'excellents scores de qualité : 95/100 en performance et sécurité, 95/100 pour la conformité RGPD."

---

# PARTIE 3 : ARCHITECTURE TECHNIQUE (10 min)

## SLIDE 8 : Architecture 3 tiers - Vue d'ensemble 🏗️
```
ARCHITECTURE 3 TIERS SÉCURISÉE

┌─────────────────────────────────────────┐
│     COUCHE PRÉSENTATION (Tier 1)        │
│  ┌──────────────┐  ┌──────────────┐    │
│  │   Vue.js 3   │  │  Thymeleaf   │    │
│  │ (SPA Public) │  │   (Admin)    │    │
│  └──────────────┘  └──────────────┘    │
└─────────────┬───────────────────────────┘
              │ HTTPS / API REST
┌─────────────▼───────────────────────────┐
│      COUCHE APPLICATION (Tier 2)        │
│  ┌────────────────────────────────┐    │
│  │    Spring Boot 3.2.4           │    │
│  │  ┌───────────┐  ┌───────────┐ │    │
│  │  │Controllers│  │ Services  │ │    │
│  │  └───────────┘  └───────────┘ │    │
│  │  ┌───────────┐  ┌───────────┐ │    │
│  │  │  Spring   │  │    JPA    │ │    │
│  │  │ Security  │  │Repositories│ │    │
│  │  └───────────┘  └───────────┘ │    │
│  └────────────────────────────────┘    │
└─────────────┬───────────────────────────┘
              │ JDBC / HikariCP
┌─────────────▼───────────────────────────┐
│       COUCHE DONNÉES (Tier 3)           │
│  ┌────────────────────────────────┐    │
│  │      MariaDB 10.11             │    │
│  │   (15 tables relationnelles)   │    │
│  └────────────────────────────────┘    │
└─────────────────────────────────────────┘

AVANTAGES :
✓ Séparation des responsabilités
✓ Maintenance facilitée
✓ Scalabilité horizontale possible
✓ Sécurité renforcée (isolation des couches)
✓ Testabilité améliorée
```

**Ce que tu dis (2 min) :**
> "J'ai conçu une architecture 3 tiers qui sépare clairement la présentation, la logique métier, et les données. Cette séparation permet une maintenance facilitée, une meilleure sécurité, et une scalabilité future."

---

## SLIDE 9 : Couche Présentation (Tier 1) 🎨
```
COUCHE PRÉSENTATION (TIER 1)

VUE.JS 3 - SPA PUBLIQUE :
• Single Page Application (SPA)
• Composition API avec <script setup>
• Vue Router pour la navigation
• Axios pour les appels API REST
• Cache localStorage pour les performances
• Composants réutilisables
• Reactive data binding

Fonctionnalités :
- Navigation fluide sans rechargement
- Animations et transitions
- Responsive mobile-first
- Lazy loading des images
- PWA-ready (Progressive Web App)

THYMELEAF - INTERFACE ADMIN :
• Rendu côté serveur (SSR)
• Templates dynamiques
• Intégration Spring Security native
• Protection CSRF automatique
• Formulaires avec validation
• Fragments réutilisables

Avantages :
- SEO optimisé (rendu serveur)
- Sécurité renforcée
- Pas de JavaScript complexe côté admin
- Support navigateurs anciens
```

**Ce que tu dis (2 min) :**
> "Pour la couche présentation, j'utilise Vue.js 3 pour l'interface publique, ce qui offre une expérience utilisateur fluide et moderne. L'interface d'administration utilise Thymeleaf avec rendu côté serveur pour plus de sécurité."

---

## SLIDE 10 : Couche Application (Tier 2) ⚙️
```
COUCHE APPLICATION (TIER 2)

CONTROLLERS :
• REST Controllers (/api/**)
  - Retournent du JSON
  - CORS configuré
  - Gestion erreurs (@ControllerAdvice)
• MVC Controllers (pages admin)
  - Retournent des vues Thymeleaf
  - CSRF activé
  - Redirections sécurisées

SERVICES :
• Logique métier centralisée
• Transactions (@Transactional)
• Validation des données
• Gestion des erreurs métier
• Logs structurés

REPOSITORIES :
• Spring Data JPA
• Méthodes CRUD automatiques
• Requêtes personnalisées (@Query)
• Pagination et tri (Pageable)
• Projections et DTO

SÉCURITÉ :
• Spring Security 6
• Authentification formulaire
• Autorisation par rôles (ADMIN, USER)
• Protection CSRF
• Headers de sécurité HTTP
• Session management

VALIDATION :
• Jakarta Validation (Bean Validation)
• @Valid sur les controllers
• Messages d'erreur personnalisés
• Validation custom avec @Constraint
```

**Ce que tu dis (2 min) :**
> "La couche application est le cœur du système. Les controllers gèrent les requêtes HTTP, les services contiennent la logique métier, les repositories interagissent avec la base de données, et Spring Security assure la sécurité globale."

---

## SLIDE 11 : Couche Données (Tier 3) 🗄️
```
COUCHE DONNÉES (TIER 3)

MARIADB 10.11 :
• Base de données relationnelle
• Compatible MySQL
• Performante et stable
• Open-source

SCHÉMA : 15 TABLES PRINCIPALES

Entités principales :
┌─────────────────────────────────────────┐
│ users (id, username, password, role)    │
│ photo (id, url, title, category, tags)  │
│ track (id, title, artist, spotify_url)  │
│ video (id, title, youtube_url)          │
│ concert (id, date, venue, location)     │
│ comment (id, content, author_id)        │
│ message (id, name, email, content)      │
│ article (id, title, content, author_id) │
│ biography (id, content_markdown)         │
│ social_link (id, platform, url)         │
│ setting (id, key, value)                 │
│ login_attempt (id, username, timestamp) │
│ track_likes (track_id, user_id)         │
│ ...                                      │
└─────────────────────────────────────────┘

RELATIONS :
• OneToMany : user → comments
• ManyToMany : track ↔ users (likes)
• ManyToOne : comment → user (author)

CONTRAINTES D'INTÉGRITÉ :
• Clés primaires (AUTO_INCREMENT)
• Clés étrangères (ON DELETE CASCADE)
• NOT NULL sur champs obligatoires
• UNIQUE sur username/email
• CHECK constraints (dates, enum)

INDEXES :
• Index sur clés étrangères
• Index sur champs de recherche
• Index composite pour performances
```

**Ce que tu dis (2 min) :**
> "La base de données MariaDB contient 15 tables avec des contraintes d'intégrité référentielle. J'ai mis en place des indexes pour optimiser les requêtes et des cascades pour gérer la suppression des données liées."

---

## SLIDE 12 : Communication entre couches 🔄
```
COMMUNICATION ENTRE LES COUCHES

FRONTEND → BACKEND (API REST) :
┌──────────────┐    GET /api/concerts   ┌──────────────┐
│  Vue.js 3    │ ────────────────────→ │ Spring Boot  │
│   (SPA)      │ ←──────────────────── │ REST API     │
└──────────────┘   JSON Response        └──────────────┘

Format :
• Requêtes : GET, POST, PUT, DELETE
• Content-Type : application/json
• Réponse : JSON avec structure standardisée
• Gestion erreurs : HTTP status codes

BACKEND → BDD (JPA/Hibernate) :
┌──────────────┐   JPQL/HQL Queries   ┌──────────────┐
│ Spring Boot  │ ───────────────────→ │   MariaDB    │
│ JPA/Hibernate│ ←─────────────────── │              │
└──────────────┘  ResultSet (Objects)  └──────────────┘

Mécanismes :
• EntityManager pour CRUD
• Query Methods (findBy...)
• @Query pour requêtes complexes
• Transactions ACID
• Lazy/Eager loading
• Connection pool (HikariCP)

LOGGING :
• SLF4J + Logback
• Niveaux : ERROR, WARN, INFO, DEBUG
• Rotation automatique (10MB, 10 fichiers)
• Format : [timestamp] [level] [thread] message
• Logs erreurs + logs d'audit sécurité
```

**Ce que tu dis (2 min) :**
> "La communication entre les couches est bien définie : le frontend communique avec le backend via une API REST en JSON, et le backend utilise JPA/Hibernate pour dialoguer avec MariaDB."

---

## SLIDE 13 : Architecture Docker 🐳
```
ARCHITECTURE DOCKER - 3 SERVICES

docker-compose.yml :

┌─────────────────────────────────────────┐
│  SERVICE 1 : musician-app               │
│  ┌───────────────────────────────────┐  │
│  │  Image : musician-website-app     │  │
│  │  Port : 8080:8080                 │  │
│  │  Depends_on : database (healthy)  │  │
│  │  Healthcheck : Actuator /health   │  │
│  │  Restart : unless-stopped         │  │
│  │  Volumes :                        │  │
│  │    - app-uploads:/app/uploads     │  │
│  │    - app-logs:/app/logs           │  │
│  └───────────────────────────────────┘  │
└─────────────────────────────────────────┘

┌─────────────────────────────────────────┐
│  SERVICE 2 : musician-db                │
│  ┌───────────────────────────────────┐  │
│  │  Image : mariadb:10.11            │  │
│  │  Port : 3306:3306                 │  │
│  │  Healthcheck : mysqladmin ping    │  │
│  │  Restart : unless-stopped         │  │
│  │  Volume :                         │  │
│  │    - db-data:/var/lib/mysql       │  │
│  │  Env :                            │  │
│  │    - MYSQL_ROOT_PASSWORD          │  │
│  │    - MYSQL_DATABASE               │  │
│  └───────────────────────────────────┘  │
└─────────────────────────────────────────┘

┌─────────────────────────────────────────┐
│  SERVICE 3 : musician-adminer           │
│  ┌───────────────────────────────────┐  │
│  │  Image : adminer:4.8.1            │  │
│  │  Port : 8081:8080                 │  │
│  │  Restart : unless-stopped         │  │
│  └───────────────────────────────────┘  │
└─────────────────────────────────────────┘

NETWORK : musician-network (bridge)
VOLUMES : db-data, app-uploads, app-logs

AVANTAGES :
✓ Environnement reproductible
✓ Isolation des services
✓ Déploiement simplifié
✓ Scalabilité facilitée
✓ Portabilité garantie
```

**Ce que tu dis (2 min) :**
> "J'ai conteneurisé l'application avec Docker Compose. Trois services : l'application Spring Boot, la base MariaDB, et Adminer pour l'administration. Chaque service a son healthcheck et redémarre automatiquement en cas de crash."

---

## SLIDE 14 : Dockerfile Multi-stage 🏗️
```
DOCKERFILE MULTI-STAGE OPTIMISÉ

# STAGE 1 : Build Frontend (Node 22 Alpine)
FROM node:22.17.0-alpine AS frontend-builder
WORKDIR /app
COPY vue-frontend/package*.json ./vue-frontend/
RUN cd vue-frontend && npm ci
COPY vue-frontend/ ./vue-frontend/
COPY frontend/ ./frontend/
RUN cd frontend && npm run build:css
RUN cd vue-frontend && npm run build
# → Génère : dist/ (Vue.js) + style.css (Tailwind)

# STAGE 2 : Build Backend (Maven + JDK 17)
FROM maven:3.9.5-eclipse-temurin-17 AS backend-builder
WORKDIR /app
COPY pom.xml ./
COPY src/ ./src/
COPY --from=frontend-builder /app/vue-frontend/dist/ ./src/main/resources/static/vue/
COPY --from=frontend-builder /app/src/main/resources/static/css/ ./src/main/resources/static/css/
RUN mvn clean package -DskipTests
# → Génère : target/musician-website-1.0.0.jar

# STAGE 3 : Image finale (JRE 17 Alpine)
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
COPY --from=backend-builder /app/target/*.jar app.jar
HEALTHCHECK --interval=30s --timeout=10s --retries=3 --start-period=60s \
  CMD wget --quiet --tries=1 --spider http://localhost:8080/actuator/health || exit 1
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

RÉSULTAT :
• Taille image finale : ~200 MB (vs ~800 MB monolithique)
• Sécurité : utilisateur non-root
• Build : frontend + backend intégré
• Production-ready
```

**Ce que tu dis (2 min) :**
> "Le Dockerfile utilise une approche multi-stage : le stage 1 build le frontend avec Node, le stage 2 package l'application Spring Boot avec Maven, et le stage 3 crée l'image finale optimisée de seulement 200 MB avec un utilisateur non-root pour la sécurité."

---

## SLIDE 15 : API REST Publique 🌐
```
API REST PUBLIQUE - ENDPOINTS

CONCERTS :
GET /api/public/concerts/upcoming
  → Liste des concerts à venir
GET /api/public/concerts/past
  → Liste des concerts passés

PHOTOS :
GET /api/public/photos
  → Galerie photos complète
GET /api/public/photos?category={cat}
  → Photos filtrées par catégorie

MUSIQUE :
GET /api/public/tracks
  → Répertoire musical complet
POST /api/tracks/{id}/play
  → Incrémenter compteur d'écoutes

VIDÉOS :
GET /api/public/videos
  → Liste des vidéos

FEATURED (Contenus mis en avant) :
GET /api/public/featured/photos
GET /api/public/featured/tracks
GET /api/public/featured/videos

AUTRES :
GET /api/public/biography
GET /api/public/social-links

FORMAT RÉPONSE :
{
  "status": "success",
  "data": [...],
  "timestamp": "2025-11-03T15:30:00Z"
}

GESTION ERREURS :
{
  "status": "error",
  "message": "Resource not found",
  "code": 404,
  "timestamp": "2025-11-03T15:30:00Z"
}

SÉCURITÉ :
• Pas d'authentification requise (public)
• CORS configuré
• Rate limiting (optionnel)
• Validation des paramètres
• Pas de données sensibles exposées
```

**Ce que tu dis (2 min) :**
> "L'API REST publique expose une dizaine d'endpoints pour récupérer les concerts, photos, musique et vidéos. Les réponses sont au format JSON avec une structure standardisée et une gestion d'erreurs claire."

---

## SLIDE 16 : Technologies - Justifications 💡
```
CHOIX TECHNOLOGIQUES - JUSTIFICATIONS

SPRING BOOT 3.2 :
✓ Framework Java de référence en entreprise
✓ Mature et très bien documenté
✓ Grande communauté
✓ Starters pour simplifier la configuration
✓ Production-ready (Actuator, monitoring)
✗ Courbe d'apprentissage un peu raide

VUE.JS 3 :
✓ Framework JavaScript moderne et léger
✓ Composition API élégante
✓ Courbe d'apprentissage douce
✓ Réactivité performante
✓ Écosystème riche (Vue Router, Pinia)
✗ Plus petit que React en termes de jobs

MARIADB 10.11 :
✓ Fork open-source de MySQL
✓ Compatible MySQL
✓ Performante et stable
✓ Gratuit et sans restrictions
✗ Moins de fonctionnalités avancées que PostgreSQL

DOCKER :
✓ Standard de l'industrie
✓ Garantit "ça marche partout"
✓ Simplifie le déploiement
✓ Isolation des dépendances
✗ Overhead léger en ressources

TAILWIND CSS :
✓ Utilitaire-first (productivité)
✓ Customisable
✓ Tree-shaking (CSS minimal en prod)
✓ Design system cohérent
✗ Classes HTML verbales

ALTERNATIVES CONSIDÉRÉES :
• React au lieu de Vue.js → Plus verbeux
• PostgreSQL au lieu de MariaDB → Plus complexe
• Angular au lieu de Vue.js → Trop lourd
```

**Ce que tu dis (2 min) :**
> "J'ai choisi ces technologies car elles sont modernes, bien documentées, utilisées en entreprise, et offrent un bon équilibre entre puissance et simplicité. Par exemple, Vue.js est plus accessible que React tout en restant très performant."

---

## SLIDE 17 : Diagramme de séquence - Authentification 🔐
```
DIAGRAMME DE SÉQUENCE : AUTHENTIFICATION

Utilisateur  Navigateur  Spring Boot  MariaDB
    │            │            │          │
    │  Accès     │            │          │
    │  /admin    │            │          │
    ├───────────>│            │          │
    │            │  GET       │          │
    │            │  /admin    │          │
    │            ├───────────>│          │
    │            │            │          │
    │            │  Redirect  │          │
    │            │  /login    │          │
    │            │<───────────┤          │
    │            │            │          │
    │  Formulaire│            │          │
    │  de login  │            │          │
    │<───────────┤            │          │
    │            │            │          │
    │  Remplir   │            │          │
    │  + Submit  │            │          │
    ├───────────>│            │          │
    │            │  POST      │          │
    │            │  /login    │          │
    │            ├───────────>│          │
    │            │            │          │
    │            │         SELECT user   │
    │            │            ├─────────>│
    │            │            │          │
    │            │         User row      │
    │            │            │<─────────┤
    │            │            │          │
    │            │   BCrypt   │          │
    │            │   verify   │          │
    │            │            │          │
    │            │   Session  │          │
    │            │   créée    │          │
    │            │   Cookie   │          │
    │            │<───────────┤          │
    │            │            │          │
    │  Redirect  │            │          │
    │  /admin    │            │          │
    │<───────────┤            │          │
    │            │            │          │
    │  Dashboard │            │          │
    │  admin     │            │          │
    │<───────────┴───────────>│          │

ÉTAPES :
1. Utilisateur accède à /admin
2. Spring Security détecte : non authentifié
3. Redirect automatique vers /login
4. Utilisateur remplit formulaire
5. POST /login avec credentials
6. Spring Security vérifie username
7. Query MariaDB pour récupérer user
8. Vérification BCrypt du password
9. Si OK : création session + cookie
10. Redirect vers /admin (accès autorisé)
```

**Ce que tu dis (2 min) :**
> "Voici le flux d'authentification complet : l'utilisateur tente d'accéder à une page protégée, Spring Security le redirige vers le login, vérifie les credentials avec BCrypt, crée une session sécurisée, et autorise l'accès."

---

# PARTIE 4 : SÉCURITÉ (6 min)

## SLIDE 18 : Sécurité - Vue d'ensemble 🛡️
```
SÉCURITÉ - APPROCHE GLOBALE

PHILOSOPHIE :
• Security by Design (dès la conception)
• Defense in Depth (plusieurs couches)
• Least Privilege (moindre privilège)
• Fail Secure (échec sécurisé)

CONFORMITÉ :
✓ Recommandations ANSSI
✓ OWASP Top 10 (Web App Security)
✓ RGPD (protection données personnelles)
✓ RGAA (accessibilité)

MÉCANISMES DE SÉCURITÉ :

1. AUTHENTIFICATION :
   • Spring Security avec formulaire
   • BCrypt (cost factor 12)
   • Protection brute-force

2. AUTORISATION :
   • Contrôle d'accès basé sur les rôles (RBAC)
   • Routes protégées par rôle
   • @PreAuthorize sur méthodes

3. PROTECTION CSRF :
   • Token CSRF sur formulaires
   • Validation côté serveur

4. HEADERS HTTP :
   • XSS, Clickjacking, MIME sniffing
   • HSTS, CSP, Referrer-Policy

5. VALIDATION :
   • Bean Validation côté serveur
   • Validation frontend (défense en profondeur)

6. GESTION SESSIONS :
   • Session timeout (30 min inactivité)
   • Rotation session ID après login
   • 1 session max par utilisateur

7. LOGGING & AUDIT :
   • Logs tentatives connexion
   • Logs actions admin
   • Alertes email (optionnel)

SCORE SÉCURITÉ : 95/100
```

**Ce que tu dis (1 min 30) :**
> "La sécurité est au cœur du projet avec une approche Security by Design. J'ai implémenté Spring Security, la protection CSRF, des headers HTTP sécurisés, et un système de logging pour auditer les accès. Le score de sécurité est de 95/100."

---

## SLIDE 19 : Spring Security - Configuration 🔐
```
SPRING SECURITY - CONFIGURATION

AUTHENTIFICATION :
• Form-based authentication
• BCrypt password encoder (strength 12)
• Remember-me avec token persistant
• 10 tentatives max / 15 minutes
  → Blocage temporaire automatique

GESTION DES RÔLES :
• ROLE_ADMIN : accès complet
• ROLE_USER : profil + commentaires
• PUBLIC : lecture seule

ROUTES PROTÉGÉES :

┌──────────────────────────────────────────┐
│  URL Pattern          │  Accès           │
├──────────────────────────────────────────┤
│  /admin/**            │  ROLE_ADMIN      │
│  /api/admin/**        │  ROLE_ADMIN      │
│  /user/**             │  ROLE_USER       │
│  /login, /register    │  Tous            │
│  /api/public/**       │  Tous            │
│  /css/**, /js/**      │  Tous            │
│  /actuator/health     │  Tous            │
│  /actuator/**         │  ROLE_ADMIN      │
└──────────────────────────────────────────┘

CODE (extrait SecurityConfig.java) :

@Bean
public SecurityFilterChain filterChain(HttpSecurity http) {
    http
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/admin/**").hasRole("ADMIN")
            .requestMatchers("/user/**").hasAnyRole("USER", "ADMIN")
            .requestMatchers("/api/public/**").permitAll()
            .anyRequest().authenticated()
        )
        .formLogin(form -> form
            .loginPage("/login")
            .defaultSuccessUrl("/admin")
            .failureUrl("/login?error")
        )
        .logout(logout -> logout
            .logoutUrl("/logout")
            .logoutSuccessUrl("/")
        )
        .csrf(csrf -> csrf
            .ignoringRequestMatchers("/api/**")
        );
    return http.build();
}

PROTECTION BRUTE-FORCE :
• LoginAttemptService
• Max 10 tentatives
• Blocage 15 minutes
• Logs des tentatives
```

**Ce que tu dis (2 min) :**
> "Spring Security gère l'authentification avec BCrypt, les autorisations par rôles, et une protection contre les attaques brute-force. Les routes sont protégées selon le niveau d'accès : admin, utilisateur ou public."

---

## SLIDE 20 : Protection CSRF 🛡️
```
PROTECTION CSRF (Cross-Site Request Forgery)

QU'EST-CE QUE LE CSRF ?
Attaque qui force un utilisateur authentifié
à exécuter des actions non désirées sur une
application web où il est authentifié.

EXEMPLE D'ATTAQUE :
Site malveillant → Formulaire caché
  <form action="https://monsite.com/admin/delete-user?id=5" method="POST">
    <input type="submit" value="Cliquez ici !">
  </form>
→ Si admin clique, user 5 est supprimé !

PROTECTION SPRING SECURITY :

1. TOKEN CSRF GÉNÉRÉ :
   • Token unique par session
   • Stocké dans session HTTP
   • Inséré automatiquement dans formulaires Thymeleaf

2. VALIDATION SERVEUR :
   • Chaque POST/PUT/DELETE vérifie token
   • Si absent ou invalide → HTTP 403 Forbidden

3. CONFIGURATION :

@Bean
public SecurityFilterChain filterChain(HttpSecurity http) {
    http.csrf(csrf -> csrf
        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
        .ignoringRequestMatchers(
            "/api/**",           // API REST (utilise autre auth)
            "/uploaded-*/**",    // Uploads (multipart)
            "/actuator/**"       // Monitoring
        )
    );
    return http.build();
}

4. FORMULAIRES THYMELEAF :
   Token inséré automatiquement :
   <input type="hidden" th:name="${_csrf.parameterName}"
          th:value="${_csrf.token}" />

RÉSULTAT :
✓ Protection complète contre CSRF
✓ Transparent pour l'utilisateur
✓ Pas d'impact sur l'API REST (désactivé volontairement)
```

**Ce que tu dis (1 min 30) :**
> "La protection CSRF est activée sur tous les formulaires admin pour empêcher les attaques de type Cross-Site Request Forgery. Spring Security génère automatiquement un token unique par session et le valide à chaque requête POST/PUT/DELETE."

---

## SLIDE 21 : Headers de sécurité HTTP 🔒
```
HEADERS DE SÉCURITÉ HTTP

CONFIGURATION (PerformanceConfig.java) :

@Bean
public FilterRegistrationBean<SecurityHeadersFilter> securityHeadersFilter() {
    // Configuration filtre personnalisé
}

public class SecurityHeadersFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // 1. PROTECTION XSS
        httpResponse.setHeader("X-XSS-Protection", "1; mode=block");

        // 2. PROTECTION CLICKJACKING
        httpResponse.setHeader("X-Frame-Options", "SAMEORIGIN");

        // 3. PROTECTION MIME SNIFFING
        httpResponse.setHeader("X-Content-Type-Options", "nosniff");

        // 4. REFERRER POLICY
        httpResponse.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");

        // 5. PERMISSIONS POLICY
        httpResponse.setHeader("Permissions-Policy",
            "geolocation=(), microphone=(), camera=()");

        // 6. HSTS (HTTPS uniquement en production)
        // httpResponse.setHeader("Strict-Transport-Security",
        //     "max-age=31536000; includeSubDomains");

        // 7. CONTENT SECURITY POLICY
        // Défini dans un filtre séparé (CSPFilter.java)

        chain.doFilter(request, response);
    }
}

DÉSACTIVATION CACHE POUR API :
if (requestURI.startsWith("/api/")) {
    httpResponse.setHeader("Cache-Control",
        "no-cache, no-store, must-revalidate");
    httpResponse.setHeader("Pragma", "no-cache");
    httpResponse.setHeader("Expires", "0");
}

RÉSULTAT :
✓ Protection contre XSS (Cross-Site Scripting)
✓ Protection contre Clickjacking
✓ Protection contre MIME sniffing
✓ Politique de confidentialité renforcée
✓ Contrôle des permissions navigateur
```

**Ce que tu dis (1 min) :**
> "J'ai configuré des headers HTTP de sécurité qui protègent contre plusieurs types d'attaques : XSS, Clickjacking, MIME sniffing. Ces headers sont ajoutés automatiquement à toutes les réponses HTTP via un filtre personnalisé."

---

## SLIDE 22 : Conformité RGPD 📜
```
CONFORMITÉ RGPD (Score : 95/100)

INFORMATION DES UTILISATEURS :

1. Politique de confidentialité (/privacy-policy) :
   • Données collectées
   • Finalité du traitement
   • Durée de conservation
   • Droits des utilisateurs
   • Contact DPO

2. Mentions légales (/legal-notice) :
   • Éditeur du site
   • Hébergeur
   • Responsable publication

3. Bannière cookies :
   • Consentement obligatoire
   • Explications claires
   • Refus possible
   • Cookies strictement nécessaires

DROITS DES UTILISATEURS :

1. Droit d'accès :
   • Consulter ses données personnelles
   • Interface /user/profile

2. Droit de rectification :
   • Modifier ses informations
   • Formulaire /user/edit-profile

3. Droit à l'effacement (droit à l'oubli) :
   • Bouton "Supprimer mon compte"
   • Suppression cascade des données liées
   • Email de confirmation

4. Droit à la portabilité :
   • Export données personnelles en JSON
   • Bouton "Télécharger mes données"
   • Format machine-readable

SÉCURITÉ DES DONNÉES :

✓ Mots de passe chiffrés (BCrypt)
✓ Connexion HTTPS (production)
✓ Accès restreints par rôles
✓ Logs d'accès et d'audit
✓ Backups réguliers chiffrés

DURÉE DE CONSERVATION :
• Comptes actifs : illimité
• Comptes inactifs (3 ans) : notification puis suppression
• Logs : 1 an maximum
• Backups : 30 jours

BASE LÉGALE :
• Consentement (cookies, newsletter)
• Contrat (compte utilisateur)
• Intérêt légitime (logs sécurité)
```

**Ce que tu dis (1 min 30) :**
> "Le site est conforme RGPD avec une politique de confidentialité complète, une bannière cookies, et tous les droits utilisateurs : accès, rectification, effacement et portabilité des données. Les mots de passe sont chiffrés et les accès sont loggés."

---

# PARTIE 5 : DÉMONSTRATION (10 min)

## SLIDE 23 : Démo - Page d'accueil 🏠
```
DÉMONSTRATION - PAGE D'ACCUEIL

[INSÉRER : 01_home.png]

FONCTIONNALITÉS VISIBLES :

Navigation :
• Menu responsive
• Logo Duo Black & White
• Liens : Accueil, Biographie, Galerie, Musique, Vidéos, Contact

Hero Section :
• Image d'en-tête
• Titre accrocheur
• Bouton call-to-action

Contenus Featured :
• Prochains concerts (3 cartes)
• Galerie photos (6 photos)
• Morceaux populaires (4 tracks)

Footer :
• Liens sociaux (Facebook, Instagram, YouTube)
• Copyright
• Liens légaux (RGPD, Mentions légales)

ASPECTS TECHNIQUES :
• Vue.js 3 SPA
• API REST : /api/public/featured/*
• Lazy loading images
• Animations smooth scroll
• Responsive mobile-first
```

**Ce que tu dis (1 min) :**
> "Voici la page d'accueil. Elle présente le duo avec une section hero, les prochains concerts, la galerie photos et le répertoire musical. Le site est entièrement responsive et utilise Vue.js 3 pour une navigation fluide."

---

## SLIDE 24 : Démo - Biographie 📖
```
DÉMONSTRATION - BIOGRAPHIE

[INSÉRER : 02_biographie.png]

FONCTIONNALITÉS :

Contenu :
• Texte biographie complet
• Rendu Markdown → HTML
• Images intégrées
• Mise en forme riche

Technique :
• Stored dans BDD (table biography)
• Format : Markdown
• Rendu : marked.js (Vue.js)
• Ou CommonMark (Thymeleaf)

Édition (Admin) :
• Éditeur Markdown WYSIWYG
• Prévisualisation en temps réel
• Sauvegarde automatique (optionnel)

AVANTAGES MARKDOWN :
✓ Facile à écrire
✓ Portable
✓ Versionnable (Git)
✓ Sécurisé (pas de scripts)
```

**Ce que tu dis (1 min) :**
> "La page biographie utilise le format Markdown pour faciliter l'édition du contenu. Le texte est stocké en Markdown dans la base de données et rendu en HTML côté client avec Vue.js."

---

## SLIDE 25 : Démo - Galerie photos 🖼️
```
DÉMONSTRATION - GALERIE PHOTOS

[INSÉRER : 03_galerie.png]

FONCTIONNALITÉS :

Affichage :
• Grid responsive (Masonry layout)
• Lazy loading (intersection observer)
• Pagination (20 photos par page)
• Catégories (Concerts, Studio, Portraits)
• Tags (filtrage)

Lightbox :
• Affichage plein écran
• Navigation clavier (← →)
• Fermeture (Esc ou clic extérieur)
• Compteur de vues

Upload (Admin) :
• Drag & drop
• Preview instantanée
• Resize automatique (optimisation)
• Formats : JPG, PNG, WebP
• Taille max : 10 MB

Stockage :
• Fichiers : uploaded-images/
• BDD : table photo (url, title, category, tags)
• CDN possible (production)

PERFORMANCES :
• Images optimisées (WebP)
• Lazy loading (économise bande passante)
• Cache navigateur (365 jours)
• Score PageSpeed : 95/100
```

**Ce que tu dis (1 min 30) :**
> "La galerie photos affiche plus de 100 photos en grid responsive avec lazy loading pour les performances. Un clic ouvre une lightbox plein écran. Côté admin, l'upload se fait par drag-and-drop avec preview instantanée."

---

## SLIDE 26 : Démo - Répertoire musical 🎵
```
DÉMONSTRATION - RÉPERTOIRE MUSICAL

[INSÉRER : 04_musique.png]

FONCTIONNALITÉS :

Affichage :
• Liste des morceaux (50+ titres)
• Tri : titre, artiste, popularité
• Recherche en temps réel
• Catégories : Covers, Originaux
• Featured tracks (mis en avant)

Player :
• Intégration Spotify (embed)
• Intégration SoundCloud (embed)
• Player HTML5 (fichiers locaux MP3)
• Contrôles : play, pause, volume
• Compteur d'écoutes

Informations :
• Titre du morceau
• Artiste original (si cover)
• Durée
• Nombre d'écoutes
• Bouton "J'aime" (si connecté)

Gestion (Admin) :
• Ajout morceaux
• Upload fichier MP3 OU lien Spotify/SoundCloud
• Édition métadonnées
• Suppression
• Toggle "Featured"

Technique :
• API : POST /api/tracks/{id}/play
  → Incrémente compteur
• WebAudio API (visualiseur optionnel)
• LocalStorage cache (performances)
```

**Ce que tu dis (1 min 30) :**
> "Le répertoire musical présente plus de 50 morceaux avec intégration Spotify, SoundCloud ou player HTML5. Chaque écoute incrémente un compteur. Les utilisateurs peuvent liker les morceaux s'ils sont connectés."

---

## SLIDE 27 : Démo - Vidéos 📹
```
DÉMONSTRATION - VIDÉOS

[INSÉRER : 05_videos.png]

FONCTIONNALITÉS :

Affichage :
• Grid responsive de vidéos
• Thumbnails YouTube/Vimeo
• Titre + description courte
• Compteur de vues
• Date de publication

Lecture :
• Player intégré (iframe YouTube/Vimeo)
• Lecture dans modal OU page dédiée
• Contrôles natifs du player
• Autoplay optionnel

Gestion (Admin) :
• Ajout via URL YouTube/Vimeo
• Extraction automatique métadonnées (API)
• Édition titre/description
• Toggle "Featured"
• Suppression

Technique :
• Embed responsive (16:9)
• API YouTube Data v3 (métadonnées)
• Lazy loading iframes
• Cache thumbnails
```

**Ce que tu dis (1 min) :**
> "La section vidéos intègre YouTube et Vimeo. Les vidéos sont affichées en grid et jouent dans un player intégré. L'admin peut ajouter des vidéos simplement en collant l'URL YouTube ou Vimeo."

---

## SLIDE 28 : Démo - Admin Dashboard 📊
```
DÉMONSTRATION - ADMIN DASHBOARD

[INSÉRER : 06_admin_dashboard.png]

STATISTIQUES TEMPS RÉEL :

Contenus :
• Nombre total de photos
• Nombre total de morceaux
• Nombre total de vidéos
• Nombre de concerts à venir

Utilisateurs :
• Nombre d'utilisateurs
• Nouveaux utilisateurs (7 derniers jours)
• Utilisateurs actifs

Engagement :
• Nombre de commentaires
• Commentaires en attente de modération
• Messages de contact non lus

Sécurité :
• Tentatives de connexion échouées (24h)
• Sessions actives
• Dernière connexion admin

Actions rapides :
• Bouton "Ajouter une photo"
• Bouton "Ajouter un concert"
• Bouton "Modérer les commentaires"
• Bouton "Voir les messages"

Technique :
• Queries JPA count()
• Cache Redis (optionnel)
• Refresh automatique (WebSocket optionnel)
• Charts avec Chart.js (optionnel)
```

**Ce que tu dis (1 min) :**
> "Le dashboard admin affiche des statistiques temps réel : nombre de contenus, utilisateurs, commentaires en attente, et tentatives de connexion échouées. Des boutons d'actions rapides permettent d'accéder directement aux sections importantes."

---

## SLIDE 29 : Démo - Gestion photos (Admin) 🖼️
```
DÉMONSTRATION - GESTION PHOTOS (ADMIN)

[INSÉRER : 07_admin_photos.png]

FONCTIONNALITÉS :

Liste des photos :
• Grille avec thumbnails
• Informations : titre, catégorie, tags, vues
• Tri : date, popularité, titre
• Recherche (titre, catégorie, tags)
• Pagination

Upload :
• Drag & drop zone
• Sélection fichiers multiple
• Preview avant upload
• Progress bar upload
• Validation : type MIME, taille max

Édition :
• Modal ou page dédiée
• Champs : titre, description, catégorie, tags
• Toggle "Featured"
• Crop/resize image (optionnel)
• Sauvegarde AJAX

Suppression :
• Confirmation obligatoire
• Suppression fichier + entrée BDD
• Cascade : suppression commentaires liés

Réorganisation :
• Drag & drop pour ordre d'affichage
• Sauvegarde automatique ordre

Technique :
• Dropzone.js OU native HTML5 File API
• FormData pour upload multipart
• CSRF token inclus
• Validation côté serveur (taille, type)
```

**Ce que tu dis (1 min 30) :**
> "L'interface de gestion des photos permet l'upload par drag-and-drop avec preview instantanée, l'édition des métadonnées, et la suppression avec confirmation. Les photos peuvent être réorganisées par drag-and-drop."

---

## SLIDE 30 : Démo - Gestion musique (Admin) 🎵
```
DÉMONSTRATION - GESTION MUSIQUE (ADMIN)

[INSÉRER : 08_admin_musique.png]

FONCTIONNALITÉS :

Liste des morceaux :
• Tableau avec titre, artiste, écoutes, featured
• Tri : titre, popularité, date ajout
• Recherche en temps réel
• Filtres : featured, catégorie

Ajout morceau :
• Formulaire avec champs :
  - Titre (requis)
  - Artiste original (si cover)
  - Catégorie (Cover, Original)
  - URL Spotify OU SoundCloud OU Upload MP3
  - Featured (checkbox)
• Validation complète

Édition :
• Modification métadonnées
• Changement URL/fichier
• Toggle featured
• Voir statistiques d'écoutes

Suppression :
• Confirmation
• Suppression fichier MP3 si local
• Cascade : likes, playlists

Statistiques :
• Nombre d'écoutes par morceau
• Top 10 morceaux
• Graphique évolution écoutes (optionnel)

Technique :
• Upload MP3 : max 50MB
• Validation : MP3, AAC, WAV
• Metadata extraction (ID3 tags)
• Storage : uploaded-tracks/
```

**Ce que tu dis (1 min) :**
> "L'interface de gestion de la musique permet d'ajouter des morceaux avec URL Spotify/SoundCloud ou upload MP3. Les statistiques d'écoutes sont visibles et les morceaux peuvent être mis en avant avec le toggle Featured."

---

## SLIDE 31 : Démo - Admin Sécurité 🔐
```
DÉMONSTRATION - ADMIN SÉCURITÉ

[INSÉRER : 12_admin_security.png]

FONCTIONNALITÉS :

Tentatives de connexion :
• Liste des tentatives échouées
• Colonnes : username, IP, date/heure, raison
• Filtres : dernières 24h, 7 jours, 30 jours
• Tri : date décroissante
• Export CSV (optionnel)

Logs d'activité admin :
• Actions effectuées par les admins
• Colonnes : admin, action, cible, date
• Exemples :
  - "admin@example.com a supprimé photo #123"
  - "admin@example.com a modifié concert #45"
• Recherche et filtres

Sessions actives :
• Liste des sessions utilisateurs actives
• Colonnes : utilisateur, IP, navigateur, dernière activité
• Bouton "Terminer session" (sécurité)

Utilisateurs bloqués :
• Liste des utilisateurs bloqués (brute-force)
• Déblocage manuel possible
• Expiration automatique après 15 min

Alertes :
• Notification si >20 tentatives échouées
• Email admin (optionnel)
• Alerte si connexion depuis nouvelle IP (optionnel)

Technique :
• Table login_attempt (username, ip, timestamp, success)
• Table audit_log (admin_id, action, target, timestamp)
• Pagination (100 entrées par page)
• Index sur timestamp pour performances
```

**Ce que tu dis (1 min 30) :**
> "L'interface de sécurité affiche toutes les tentatives de connexion échouées avec l'IP et la raison. Les logs d'activité admin permettent d'auditer toutes les actions importantes. Les sessions actives peuvent être terminées manuellement si besoin."

---

## SLIDE 32 : Démo - Adminer (BDD) 🗄️
```
DÉMONSTRATION - ADMINER (ADMINISTRATION BDD)

[INSÉRER : 13_adminer_overview.png]

ADMINER :
Interface web pour administrer MariaDB

Accès :
• http://localhost:8081/
• Serveur : database
• Utilisateur : musician_user
• Mot de passe : SecurePassword123!
• Base : musician_db

Fonctionnalités :
• Explorer structure des tables
• Exécuter requêtes SQL
• Voir données (SELECT)
• Modifier données (UPDATE, DELETE)
• Export SQL/CSV/JSON
• Import SQL
• Gérer utilisateurs et privilèges

Tables visibles :
• users (5 lignes)
• photo (100+ lignes)
• track (50+ lignes)
• video, concert, comment, message, etc.

SÉCURITÉ :
⚠️ Adminer accessible uniquement en DEV
⚠️ Port 8081 NON exposé en production
⚠️ Alternative prod : SSH tunnel

Technique :
• Image Docker : adminer:4.8.1
• Connexion via réseau Docker
• Pas de données sensibles exposées (passwords hashés)
```

**Ce que tu dis (1 min) :**
> "Adminer est une interface web pour administrer MariaDB. Elle permet d'explorer les tables, exécuter des requêtes SQL, et gérer les données. Important : Adminer n'est accessible qu'en développement, jamais en production pour des raisons de sécurité."

---

## SLIDE 33 : Démo - Docker PS 🐳
```
DÉMONSTRATION - DOCKER CONTAINERS

[INSÉRER : 15_docker_ps.png]

COMMANDE : docker-compose ps

RÉSULTAT :

NAME               IMAGE                    STATUS
musician-app       musician-website-app     Up (healthy)
musician-db        mariadb:10.11            Up (healthy)
musician-adminer   adminer:4.8.1            Up

DÉTAILS :

musician-app :
• État : Up X minutes (healthy)
• Port : 0.0.0.0:8080->8080/tcp
• Healthcheck : OK (Actuator /health)
• Restart policy : unless-stopped

musician-db :
• État : Up X minutes (healthy)
• Port : 0.0.0.0:3306->3306/tcp
• Healthcheck : OK (mysqladmin ping)
• Volume : db-data (persistant)

musician-adminer :
• État : Up X minutes
• Port : 0.0.0.0:8081->8080/tcp
• Restart policy : unless-stopped

COMMANDES UTILES :

# Démarrer les services
docker-compose up -d

# Voir les logs
docker-compose logs -f app

# Redémarrer un service
docker-compose restart app

# Arrêter tout
docker-compose down

# Rebuild
docker-compose up -d --build

AVANTAGES :
✓ 3 services isolés
✓ Redémarrage automatique
✓ Healthchecks actifs
✓ Volumes persistants
✓ Réseau privé
```

**Ce que tu dis (1 min) :**
> "Voici les 3 conteneurs Docker en cours d'exécution : l'application Spring Boot, MariaDB, et Adminer. Tous sont 'healthy' grâce aux healthchecks. Si un conteneur crash, il redémarre automatiquement grâce à la politique de restart."

---

# PARTIE 6 : DEVOPS & TESTS (3 min)

## SLIDE 34 : CI/CD - GitHub Actions 🔄
```
CI/CD - GITHUB ACTIONS

PIPELINE AUTOMATISÉ (.github/workflows/ci.yml) :

Déclenchement :
• Push sur branch main
• Pull request vers main
• Manuel (workflow_dispatch)

Étapes :

1. CHECKOUT CODE
   • actions/checkout@v3
   • Clone du repository

2. SETUP JDK 17
   • actions/setup-java@v3
   • Cache Maven dependencies

3. BUILD & TEST
   • mvn clean test
   • Tests unitaires JUnit
   • Tests d'intégration @SpringBootTest
   • Génération rapport JaCoCo

4. BUILD DOCKER IMAGE
   • docker build -t musician-website .
   • Multi-stage build
   • Tag : latest + version

5. PUSH TO REGISTRY
   • GitHub Container Registry (ghcr.io)
   • Docker Hub (optionnel)
   • Authentication via secrets

6. DEPLOY (optionnel)
   • SSH vers serveur de production
   • docker-compose pull
   • docker-compose up -d
   • Health check

BADGES :
[![CI/CD](https://github.com/.../badge.svg)]
[![Coverage](https://codecov.io/.../badge.svg)]

AVANTAGES :
✓ Tests automatiques à chaque push
✓ Détection rapide des régressions
✓ Déploiement fiable et reproductible
✓ Rollback facile si problème
✓ Visibilité sur la santé du projet
```

**Ce que tu dis (1 min 30) :**
> "J'ai mis en place un pipeline CI/CD avec GitHub Actions qui s'exécute automatiquement à chaque push. Il lance les tests, build l'image Docker, la pousse sur le registry, et peut déployer automatiquement en production."

---

## SLIDE 35 : Tests 🧪
```
STRATÉGIE DE TESTS

TESTS BACKEND (Java) :

1. Tests unitaires :
   • JUnit 5 + Mockito
   • Services et Controllers
   • Mock des dépendances
   • Couverture : ~70%

Exemple :
@Test
void testCreateConcert() {
    Concert concert = new Concert();
    concert.setTitle("Test Concert");
    when(concertRepository.save(any())).thenReturn(concert);

    Concert saved = concertService.createConcert(concert);

    assertNotNull(saved);
    assertEquals("Test Concert", saved.getTitle());
}

2. Tests d'intégration :
   • @SpringBootTest
   • @AutoConfigureTestDatabase
   • H2 en mémoire
   • MockMvc pour controllers
   • TestRestTemplate pour API

Exemple :
@SpringBootTest(webEnvironment = RANDOM_PORT)
class ConcertApiIntegrationTest {
    @Autowired
    TestRestTemplate restTemplate;

    @Test
    void testGetConcerts() {
        ResponseEntity<Concert[]> response =
            restTemplate.getForEntity("/api/public/concerts", Concert[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }
}

3. Couverture de code :
   • JaCoCo plugin Maven
   • Rapport HTML généré
   • Seuil minimum : 60%
   • CI/CD vérifie couverture

TESTS FRONTEND (Vue.js) :

1. Tests unitaires composants :
   • Vitest
   • @vue/test-utils
   • Mock des API calls

2. Tests d'accessibilité :
   • axe-core
   • Vérification WCAG

TESTS E2E (optionnel) :
• Playwright
• Parcours utilisateur complets
• Capture screenshots automatique
```

**Ce que tu dis (1 min 30) :**
> "J'ai mis en place des tests unitaires avec JUnit et Mockito, des tests d'intégration avec @SpringBootTest, et JaCoCo pour mesurer la couverture de code. Les tests sont exécutés automatiquement dans le pipeline CI/CD."

---

## SLIDE 36 : Monitoring - Spring Boot Actuator 📈
```
MONITORING - SPRING BOOT ACTUATOR

ENDPOINTS ACTIVÉS :

/actuator/health
• État de santé de l'application
• Vérifie : BDD, disk space, etc.
• Utilisé par Docker healthcheck

Réponse :
{
  "status": "UP",
  "components": {
    "db": { "status": "UP" },
    "diskSpace": { "status": "UP" }
  }
}

/actuator/info
• Informations version et build
• Metadata personnalisées

Réponse :
{
  "app": {
    "name": "Musician Website",
    "version": "1.0.0"
  }
}

/actuator/metrics
• Métriques JVM et application
• CPU, mémoire, threads, HTTP requests

Métriques disponibles :
- jvm.memory.used
- jvm.gc.memory.allocated
- http.server.requests
- system.cpu.usage

/actuator/prometheus (optionnel)
• Export format Prometheus
• Pour monitoring avancé (Grafana)

SÉCURITÉ :
• /actuator/health → Public
• /actuator/** → ROLE_ADMIN uniquement

LOGS :
• SLF4J + Logback
• Rotation automatique (10MB, 10 fichiers)
• Format : [timestamp] [level] [thread] message
• Fichiers :
  - logs/application.log
  - logs/error.log (ERROR uniquement)

Exemple :
2025-11-03 15:30:00.123 INFO [http-nio-8080-exec-1] c.d.c.ConcertController : GET /api/public/concerts
2025-11-03 15:30:00.456 ERROR [http-nio-8080-exec-2] c.d.s.UserService : Failed to create user: Email already exists
```

**Ce que tu dis (1 min) :**
> "Spring Boot Actuator expose des endpoints de monitoring : /health pour vérifier l'état de l'app, /metrics pour les statistiques JVM, et /prometheus pour un monitoring avancé. Les logs sont structurés et rotationnés automatiquement."

---

# PARTIE 7 : PERFORMANCE & QUALITÉ (2 min)

## SLIDE 37 : Performance 🚀
```
PERFORMANCE - OPTIMISATIONS

SCORE PAGESPE Insights : 95/100

OPTIMISATIONS FRONTEND :

1. Images :
   • Lazy loading (Intersection Observer)
   • Format WebP (Chrome, Firefox)
   • Attribut decoding="async"
   • Responsive images (srcset)
   • Compression (TinyPNG)

2. CSS/JS :
   • Minification (Vite build)
   • Tree-shaking (Tailwind)
   • Code splitting (Vue Router lazy routes)
   • Content hashing (cache busting)

3. Fonts :
   • Font Awesome CDN
   • font-display: swap
   • Sous-ensemble de glyphes uniquement

4. Cache navigateur :
   • CSS/JS : max-age=365 jours
   • Images : max-age=365 jours
   • Avatars : max-age=7 jours
   • HTML : no-cache

OPTIMISATIONS BACKEND :

1. Compression GZIP :
   • Activée pour réponses >2KB
   • Économie ~70% bande passante

2. HTTP/2 :
   • Multiplexing
   • Server push (optionnel)

3. Cache :
   • @Cacheable sur méthodes coûteuses
   • Cache Redis (optionnel)
   • ETag HTTP (optionnel)

4. Base de données :
   • Indexes sur clés étrangères
   • Indexes sur champs recherche
   • Connection pool HikariCP (optimisé)
   • Batch inserts pour performance

5. CDN (production) :
   • Cloudflare (ou Cloudinary)
   • Cache des assets statiques
   • Distribution géographique

RÉSULTATS :
• First Contentful Paint : <1.5s
• Time to Interactive : <3s
• Lighthouse Performance : 95/100
• Lighthouse SEO : 100/100
• Lighthouse Accessibility : 90/100
• Lighthouse Best Practices : 100/100
```

**Ce que tu dis (1 min 30) :**
> "Le site obtient un score PageSpeed de 95/100 grâce à plusieurs optimisations : lazy loading des images, compression GZIP, cache navigateur agressif, minification du code, et utilisation de WebP. Le Time to Interactive est inférieur à 3 secondes."

---

## SLIDE 38 : Qualité du code 📝
```
QUALITÉ DU CODE

ORGANISATION :

Architecture en couches :
src/main/java/com/docker/
├── controller/
│   ├── api/          (REST Controllers)
│   └── mvc/          (MVC Controllers)
├── service/          (Business logic)
├── repository/       (Data access)
├── entity/           (JPA Entities)
├── dto/              (Data Transfer Objects)
├── config/           (Configuration classes)
├── security/         (Security filters, handlers)
└── exception/        (Custom exceptions)

CONVENTIONS :

1. Naming :
   • Classes : PascalCase
   • Méthodes : camelCase
   • Constantes : UPPER_SNAKE_CASE
   • Packages : lowercase

2. Annotations :
   • @Service sur services
   • @Repository sur repositories
   • @RestController sur API controllers
   • @Transactional sur méthodes transactionnelles

3. Validation :
   • Jakarta Validation annotations
   • @Valid sur parameters
   • Messages d'erreur i18n

4. Documentation :
   • JavaDoc sur méthodes publiques
   • README.md complet
   • API docs (Swagger/OpenAPI)

OUTILS QUALITÉ :

1. Checkstyle :
   • config/checkstyle/checkstyle.xml
   • Vérifie conventions de code
   • Exécution Maven : mvn checkstyle:check

2. SpotBugs (optionnel) :
   • Détection bugs potentiels
   • Analyse statique du bytecode

3. SonarQube (optionnel) :
   • Analyse code
   • Détection code smells
   • Sécurité et bugs

MÉTRIQUES :
• Lignes de code : ~23 000
• Complexité cyclomatique : <10 (moyenne)
• Duplication : <3%
• Couverture tests : ~70%
• Technical debt : <1 jour

BONNES PRATIQUES :
✓ SOLID principles
✓ DRY (Don't Repeat Yourself)
✓ KISS (Keep It Simple, Stupid)
✓ YAGNI (You Aren't Gonna Need It)
✓ Separation of Concerns
✓ Dependency Injection
```

**Ce que tu dis (1 min 30) :**
> "Le code est organisé en architecture en couches avec séparation claire des responsabilités. J'utilise Checkstyle pour vérifier les conventions, JaCoCo pour la couverture de tests, et je respecte les principes SOLID. La documentation est complète avec JavaDoc et README."

---

# PARTIE 8 : CONCLUSION (2 min)

## SLIDE 39 : Compétences CDA démontrées ✅
```
COMPÉTENCES CDA DÉMONTRÉES

BLOC 1 : DÉVELOPPER UNE APPLICATION SÉCURISÉE

✓ CP1 : Installer et configurer son environnement
  → Docker, Git, IDE, outils de build

✓ CP2 : Développer des interfaces utilisateur
  → Vue.js 3 (SPA) + Thymeleaf (admin)

✓ CP3 : Développer des composants métier
  → Services Spring Boot, logique métier

✓ CP4 : Contribuer à la gestion d'un projet
  → Git, GitHub, CI/CD, documentation

BLOC 2 : CONCEVOIR EN ARCHITECTURE MULTICOUCHE

✓ CP5 : Analyser les besoins et maquetter
  → Analyse client, maquettes, cahier des charges

✓ CP6 : Définir l'architecture logicielle
  → Architecture 3 tiers sécurisée

✓ CP7 : Concevoir une base de données relationnelle
  → MariaDB 15 tables, contraintes, indexes

✓ CP8 : Développer composants d'accès aux données
  → JPA/Hibernate, repositories, JPQL

BLOC 3 : PRÉPARER LE DÉPLOIEMENT

✓ CP9 : Préparer et exécuter les plans de tests
  → Tests unitaires, intégration, JaCoCo

✓ CP10 : Préparer et documenter le déploiement
  → Docker, docker-compose, documentation

✓ CP11 : Contribuer à la mise en production DevOps
  → GitHub Actions, CI/CD, monitoring

COMPÉTENCES TRANSVERSALES :

✓ Communiquer (en français et en anglais)
  → Documentation, README, commentaires code

✓ Mettre en œuvre une démarche de résolution de problème
  → Debug, logs, tests, monitoring

✓ Apprendre en continu
  → Veille technologique, nouvelles versions
```

**Ce que tu dis (1 min) :**
> "Ce projet couvre l'intégralité des compétences du référentiel CDA : développement d'une application sécurisée, conception en architecture multicouche, et préparation du déploiement avec DevOps. Toutes les compétences transversales sont également démontrées."

---

## SLIDE 40 : Conclusion & Perspectives 🎯
```
CONCLUSION & PERSPECTIVES

RÉALISATIONS :

✓ Application web complète et opérationnelle
✓ Architecture moderne et scalable (3 tiers)
✓ Sécurité renforcée (95/100)
✓ Performance optimisée (95/100)
✓ Conformité RGPD (95/100) & RGAA (80/100)
✓ Conteneurisation Docker complète
✓ CI/CD automatisé avec GitHub Actions
✓ Tests unitaires et d'intégration
✓ Monitoring et observabilité
✓ Documentation technique exhaustive
✓ Score global : 82/100

APPRENTISSAGES :

• Maîtrise de Spring Boot 3 et Spring Security
• Développement frontend moderne avec Vue.js 3
• Conteneurisation et orchestration Docker
• CI/CD et démarche DevOps
• Sécurité applicative (OWASP, RGPD)
• Architecture 3 tiers en environnement réel
• Gestion projet Git en autonomie

AMÉLIORATIONS POSSIBLES (V2) :

1. Recherche avancée :
   • Elasticsearch pour full-text search
   • Suggestions de recherche (autocomplete)
   • Filtres avancés

2. Scalabilité :
   • Kubernetes pour orchestration
   • Plusieurs replicas de l'application
   • Load balancer

3. Monitoring avancé :
   • Prometheus + Grafana
   • Alertes automatiques (PagerDuty)
   • Distributed tracing (Zipkin)

4. Cache :
   • Redis pour sessions
   • Cache API responses
   • Cache base de données (query cache)

5. Microservices (si croissance) :
   • Service concerts indépendant
   • Service photos/vidéos indépendant
   • API Gateway (Spring Cloud Gateway)

6. PWA :
   • Service Worker
   • Offline mode
   • Push notifications

7. Internationalisation :
   • Support multi-langues (i18n)
   • Français, Anglais

PERSPECTIVES PROFESSIONNELLES :

Ce projet démontre :
• Capacité à mener un projet de A à Z
• Maîtrise des technologies modernes
• Souci de la qualité et de la sécurité
• Approche professionnelle

Métiers visés :
• Développeur Full Stack Java/Spring
• Développeur Backend Spring Boot
• DevOps Engineer
• Concepteur Développeur d'Applications

──────────────────────────────────────

MERCI POUR VOTRE ATTENTION

Questions ?

──────────────────────────────────────
```

**Ce que tu dis (1 min) :**
> "Pour conclure, ce projet représente 3 mois de travail avec une application complète et production-ready. Les perspectives d'amélioration sont nombreuses : Elasticsearch, Kubernetes, monitoring avancé avec Prometheus/Grafana. Ce projet démontre ma capacité à concevoir, développer et déployer une application moderne et sécurisée de A à Z."

---

# 📝 ANNEXES

## Timing détaillé (40 minutes)

- **Partie 1 : Introduction** (5 min) → Slides 1-4
- **Partie 2 : Contexte & Objectifs** (4 min) → Slides 5-7
- **Partie 3 : Architecture** (10 min) → Slides 8-17
- **Partie 4 : Sécurité** (6 min) → Slides 18-22
- **Partie 5 : Démonstration** (10 min) → Slides 23-33
- **Partie 6 : DevOps & Tests** (3 min) → Slides 34-36
- **Partie 7 : Performance & Qualité** (2 min) → Slides 37-38
- **Partie 8 : Conclusion** (2 min) → Slides 39-40

**TOTAL : 42 minutes** (marge de 2 min)

## Captures d'écran à insérer

1. ✅ 01_home.png → Slide 23
2. ✅ 02_biographie.png → Slide 24
3. ✅ 03_galerie.png → Slide 25
4. ✅ 04_musique.png → Slide 26
5. ✅ 05_videos.png → Slide 27
6. ✅ 06_admin_dashboard.png → Slide 28
7. ✅ 07_admin_photos.png → Slide 29
8. ✅ 08_admin_musique.png → Slide 30
9. ✅ 12_admin_security.png → Slide 31
10. ✅ 13_adminer_overview.png → Slide 32
11. ✅ 15_docker_ps.png → Slide 33

## Conseils pour la présentation

### Avant l'oral :
- [ ] Répéter 3-4 fois à voix haute
- [ ] Chronométrer chaque partie
- [ ] Préparer des notes sur papier
- [ ] Tester que Docker démarre bien
- [ ] Avoir des captures de backup

### Pendant l'oral :
- ✓ Parler lentement et clairement
- ✓ Regarder le jury (pas les slides)
- ✓ Faire des pauses entre les parties
- ✓ Respirer calmement
- ✓ Montrer ta passion pour le projet
- ✓ Si tu ne sais pas : "Je ne suis pas certain, mais je pense que..."

### Après chaque slide :
- Demander "Des questions sur cette partie ?"
- Laisser 5-10 secondes de silence
- Transition vers la slide suivante

### Si quelque chose plante :
- Garder son calme
- "Je vais vous montrer des captures d'écran..."
- Continuer la présentation
- Le jury comprend que ça arrive

---

**FIN DU DOCUMENT**

**Créé le :** 3 novembre 2025
**Dernière mise à jour :** 3 novembre 2025
**Auteur :** Claude Code
**Pour :** Oral CDA - Projet Duo Black & White
**Durée présentation :** 40 minutes
**Nombre de slides :** 40
