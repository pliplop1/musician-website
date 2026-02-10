# 📊 STRUCTURE PRÉSENTATION IMPRESS - 35 SLIDES POUR 40 MINUTES

**Oral CDA :**
- 40 minutes : Présentation (tu parles)
- 40 minutes : Questions jury (ils questionnent)

---

## PARTIE 1 : INTRODUCTION (5 min - 3 slides)

### SLIDE 1 : Page de titre
```
Duo Black & White
Site Web Officiel - Projet CDA

[Ton nom]
[Date de l'oral]

Spring Boot 3.2 + Vue.js 3 + Docker
```

### SLIDE 2 : Sommaire
```
1. Présentation du projet
2. Architecture technique
3. Sécurité
4. Démonstration
5. Tests & DevOps
6. Déploiement
7. Conclusion
```

### SLIDE 3 : Contexte du projet
```
Besoin :
• Site officiel pour groupe musical Duo Black & White

Objectifs :
• Vitrine publique (biographie, galerie, musique, vidéos)
• Interface administration complète
• Responsive et moderne

Cible :
• Fans, organisateurs de concerts, presse
```

---

## PARTIE 2 : VUE D'ENSEMBLE (5 min - 4 slides)

### SLIDE 4 : Périmètre fonctionnel
```
Partie publique :
• Biographie avec rendu Markdown
• Galerie photos responsive
• Répertoire musical avec intégration Spotify/SoundCloud
• Section vidéos
• Agenda concerts (passés/à venir)
• Formulaire de contact

Partie administration :
• Dashboard statistiques temps réel
• Gestion complète du contenu
• Upload photos/vidéos drag-and-drop
• Modération commentaires
• Gestion utilisateurs et sécurité
```

### SLIDE 5 : Stack technique
```
Backend :
• Spring Boot 3.2 (Java 17)
• Spring Security, Data JPA, Mail, Validation
• Maven

Frontend :
• Vue.js 3 + Vite (SPA publique)
• Thymeleaf (interface admin)
• Tailwind CSS
• Axios

Base de données :
• MariaDB 10.11
• JPA/Hibernate

DevOps :
• Docker & Docker Compose
• GitHub Actions (CI/CD)
```

### SLIDE 6 : Métriques du projet
```
Code :
• Backend Java : [X] classes
• Frontend Vue.js : [X] composants
• Templates Thymeleaf : [X] pages

Architecture :
• [X] endpoints REST
• 15 tables relationnelles
• 3 conteneurs Docker

Développement :
• [X] semaines
• Git : [X] commits
```

### SLIDE 7 : Scores qualité
```
Performance :
• PageSpeed : 95/100
• Temps de chargement : < 2s

Sécurité :
• Score global : 95/100
• Headers sécurité : OK
• Protection CSRF : Activée

Conformité :
• RGPD : 95/100
• RGAA : 80/100

Score projet global : 82/100
```

---

## PARTIE 3 : ARCHITECTURE (8 min - 6 slides)

### SLIDE 8 : Architecture 3 tiers - Vue d'ensemble
```
Architecture multicouche répartie sécurisée

Couche Présentation :
• Vue.js 3 (SPA publique)
• Thymeleaf (Interface admin)

Couche Application :
• Spring Boot 3.2
• Controllers REST + MVC
• Services métier
• Spring Security

Couche Données :
• MariaDB 10.11
• JPA/Hibernate
• Repositories
```

### SLIDE 9 : Couche Présentation
```
Vue.js 3 - SPA Publique :
• Single Page Application
• Routing Vue Router
• Composants réutilisables
• Appels API REST (Axios)
• Cache localStorage

Thymeleaf - Interface Admin :
• Rendu côté serveur
• Templates dynamiques
• Intégration Spring Security
• Formulaires avec CSRF
```

### SLIDE 10 : Couche Application
```
Controllers :
• REST Controllers (/api/**)
• MVC Controllers (pages admin)
• Gestion des erreurs (@ControllerAdvice)

Services :
• Logique métier
• Transactions (@Transactional)
• Validation des données

Repositories :
• Spring Data JPA
• Requêtes personnalisées
• Pagination et tri
```

### SLIDE 11 : Couche Données
```
MariaDB 10.11 :
• 15 tables relationnelles
• Contraintes d'intégrité référentielle
• Indexes pour optimisation

Entités principales :
• User, Photo, Track, Video
• Concert, Comment, Message
• Article, Biography

Relations :
• ManyToMany (likes)
• OneToMany (commentaires)
• ManyToOne (auteurs)
```

### SLIDE 12 : Architecture Docker
```
3 services conteneurisés :

musician-app :
• Spring Boot application
• Healthcheck Actuator
• Restart automatique
• Volumes : uploads, logs

musician-db :
• MariaDB 10.11
• Volume persistant : db-data
• Healthcheck MySQL
• Backup réguliers

musician-adminer :
• Interface admin BDD
• Port 8081
• Accessible en dev uniquement
```

### SLIDE 13 : Communication entre couches
```
Frontend → Backend :
• API REST avec JSON
• CORS configuré
• Authentification par cookies
• Gestion erreurs HTTP

Backend → BDD :
• JPA/Hibernate
• Pool de connexions HikariCP
• Transactions ACID
• Lazy/Eager loading

Logging :
• Logs applicatifs structurés
• Rotation automatique (10MB)
• Niveaux : ERROR, WARN, INFO, DEBUG
```

---

## PARTIE 4 : SÉCURITÉ (8 min - 6 slides)

### SLIDE 14 : Sécurité - Vue d'ensemble
```
Approche sécurité :
• Security by Design
• Recommandations ANSSI
• Protection OWASP Top 10
• Conformité RGPD

Mécanismes :
• Spring Security
• Protection CSRF
• Headers HTTP sécurisés
• Validation des entrées
• Gestion des sessions
• Logging des tentatives
```

### SLIDE 15 : Authentification
```
Mécanisme :
• Formulaire de connexion sécurisé
• BCrypt pour hash mots de passe (cost 12)
• Remember-me avec token

Gestion des rôles :
• ADMIN : accès complet
• USER : profil + commentaires
• PUBLIC : lecture seule

Protection brute-force :
• Max 10 tentatives / 15 minutes
• Blocage temporaire automatique
• Logging des tentatives échouées
```

### SLIDE 16 : Autorisation
```
Routes protégées :
• /admin/** → ROLE_ADMIN uniquement
• /user/** → ROLE_USER ou ROLE_ADMIN
• /api/admin/** → ROLE_ADMIN

Routes publiques :
• Pages statiques
• /api/public/**
• Assets (CSS, JS, images)
• Swagger UI
• Actuator /health

Contrôle d'accès :
• @PreAuthorize sur méthodes
• HttpSecurity dans configuration
• Redirections automatiques
```

### SLIDE 17 : Protection CSRF
```
CSRF activé :
• Tous les formulaires POST/PUT/DELETE
• Token synchronisé avec session
• Régénération après login

CSRF désactivé :
• API REST (/api/**)
• Uploads (/uploaded-*/**)
• Endpoints Actuator

Implémentation :
• CsrfTokenRepository
• Hidden input dans formulaires Thymeleaf
• Validation côté serveur
```

### SLIDE 18 : Headers de sécurité
```
Headers HTTP configurés :

• X-XSS-Protection: 1; mode=block
• X-Frame-Options: SAMEORIGIN (anti-clickjacking)
• X-Content-Type-Options: nosniff
• Referrer-Policy: strict-origin-when-cross-origin
• Permissions-Policy: restrictions micro/camera/geolocation
• Content-Security-Policy: politique restrictive
• HSTS: max-age=31536000 (production uniquement)

Configuration :
• Filtre personnalisé PerformanceConfig
• Désactivation cache pour API dynamiques
```

### SLIDE 19 : Conformité RGPD
```
Mesures RGPD (95/100) :

Information :
• Politique de confidentialité détaillée
• Mentions légales complètes
• Bannière cookies avec consentement

Droits utilisateurs :
• Export données personnelles (JSON)
• Modification profil
• Suppression compte
• Droit à l'oubli (cascade delete)

Sécurité données :
• Chiffrement mots de passe
• Accès restreints par rôles
• Logs d'accès
• Backup réguliers
```

---

## PARTIE 5 : DÉMONSTRATION (10 min - 10 slides)

### SLIDE 20 : Démo - Page d'accueil
```
[INSÉRER : 01_home.png]

Fonctionnalités :
• Contenu dynamique "featured"
• Navigation responsive
• Animation smooth scroll
• Vue.js SPA
```

### SLIDE 21 : Démo - Biographie
```
[INSÉRER : 02_biographie.png]

Fonctionnalités :
• Rendu Markdown dynamique
• Design épuré
• Responsive mobile
```

### SLIDE 22 : Démo - Galerie photos
```
[INSÉRER : 03_galerie.png]

Fonctionnalités :
• Grid responsive (masonry)
• Lazy loading images
• Modal lightbox
• Catégories et tags
• Pagination
```

### SLIDE 23 : Démo - Répertoire musical
```
[INSÉRER : 04_musique.png]

Fonctionnalités :
• Intégration Spotify/SoundCloud
• Player HTML5 pour fichiers locaux
• Compteur d'écoutes
• Featured tracks
• Recherche et filtres
```

### SLIDE 24 : Démo - Vidéos
```
[INSÉRER : 05_videos.png]

Fonctionnalités :
• Lecteur intégré
• Support YouTube/Vimeo
• Compteur de vues
• Grid responsive
```

### SLIDE 25 : Admin - Dashboard
```
[INSÉRER : 06_admin_dashboard.png]

Fonctionnalités :
• Statistiques temps réel
• Vue d'ensemble contenu
• Actions rapides
• Graphiques (optionnel)
```

### SLIDE 26 : Admin - Gestion photos
```
[INSÉRER : 07_admin_photos.png]

Fonctionnalités :
• Upload drag-and-drop
• Prévisualisation instantanée
• Gestion tags/catégories
• Featured toggle
• Recherche et tri
```

### SLIDE 27 : Admin - Gestion musique
```
[INSÉRER : 08_admin_musique.png]

Fonctionnalités :
• Ajout/édition tracks
• Upload fichiers audio
• Embed Spotify/SoundCloud
• Featured toggle
• Statistiques écoutes
```

### SLIDE 28 : Admin - Sécurité
```
[INSÉRER : 12_admin_security.png]

Fonctionnalités :
• Historique tentatives connexion
• Logs d'activité admin
• Blocage automatique (brute-force)
• Monitoring temps réel
```

### SLIDE 29 : Adminer - Administration BDD
```
[INSÉRER : 13_adminer_overview.png]

Fonctionnalités :
• Interface web MariaDB
• Requêtes SQL
• Structure des tables
• Export/Import
• Accessible en dev uniquement
```

---

## PARTIE 6 : TESTS (3 min - 2 slides)

### SLIDE 30 : Stratégie de tests
```
Tests Backend :
• Tests unitaires (services, controllers)
• Tests d'intégration (@SpringBootTest)
• Couverture de code avec JaCoCo
• Base H2 en mémoire pour tests
• MockMvc pour tests controllers

Tests Frontend :
• Vitest pour composants Vue.js
• @vue/test-utils pour rendering
• Tests d'accessibilité (axe-core)

Tests E2E (optionnel) :
• Playwright pour parcours utilisateur
```

### SLIDE 31 : CI/CD - GitHub Actions
```
Pipeline automatisé :

1. Déclenchement :
   • Push sur main
   • Pull request

2. Build & Tests :
   • mvn clean test
   • Compilation Maven
   • Tests unitaires et intégration

3. Docker :
   • Build image multi-stage
   • Tag version + latest
   • Push vers GitHub Container Registry

4. Déploiement (optionnel) :
   • Déploiement SSH automatisé
   • Health check après déploiement
```

---

## PARTIE 7 : DÉPLOIEMENT (3 min - 2 slides)

### SLIDE 32 : Docker - Conteneurs
```
[INSÉRER : 15_docker_ps.png]

Vérification des services :
• 3 conteneurs UP
• Healthchecks OK (healthy)
• Uptime stable
• Réseau isolé
```

### SLIDE 33 : Déploiement production
```
Dockerfile multi-stage :
• Stage 1: Node 22 alpine → Build Vue.js + Tailwind
• Stage 2: Maven + JDK 17 → Build Spring Boot
• Stage 3: JRE 17 alpine → Image finale optimisée
• Taille finale : ~200MB (vs 800MB monolithique)

Configuration production :
• Variables d'environnement (secrets)
• Volumes Docker persistants
• Proxy HTTPS (Caddy/Nginx)
• Certificats TLS Let's Encrypt
• Monitoring avec Spring Boot Actuator
• Backup automatisés quotidiens
```

---

## PARTIE 8 : CONCLUSION (3 min - 2 slides)

### SLIDE 34 : Compétences CDA démontrées
```
Bloc 1 - Développer une application sécurisée :
✓ Environnement de travail (Docker, Git)
✓ Interfaces utilisateur (Vue.js, Thymeleaf)
✓ Composants métier (Services Spring)
✓ Gestion de projet (Git, CI/CD)

Bloc 2 - Concevoir en architecture multicouche :
✓ Analyse besoins et maquettage
✓ Architecture logicielle 3 tiers
✓ Base de données relationnelle (MariaDB)
✓ Composants d'accès données (JPA)

Bloc 3 - Préparer le déploiement :
✓ Plans de tests (unitaires, intégration)
✓ Documentation déploiement
✓ Démarche DevOps (CI/CD, Docker)

Compétences transversales :
✓ Communication (documentation, README)
✓ Résolution de problèmes
✓ Veille technologique
```

### SLIDE 35 : Conclusion & Perspectives
```
Réalisations :
✓ Application web complète et opérationnelle
✓ Architecture moderne et scalable (3 tiers)
✓ Sécurité renforcée (95/100)
✓ Conformité RGPD (95/100) & RGAA (80/100)
✓ Conteneurisation Docker complète
✓ CI/CD automatisé avec GitHub Actions
✓ Tests et monitoring en place
✓ Score global : 82/100

Améliorations possibles :
• Elasticsearch pour recherche full-text avancée
• Kubernetes pour haute disponibilité et scalabilité
• Monitoring avancé (Prometheus + Grafana)
• Cache Redis pour performances
• Microservices (décomposition si croissance)

MERCI POUR VOTRE ATTENTION

Questions ?
```

---

## 📝 NOTES POUR LA PRÉSENTATION

### Timing par partie :
- Introduction : 5 min (slides 1-3)
- Vue d'ensemble : 5 min (slides 4-7)
- Architecture : 8 min (slides 8-13)
- Sécurité : 8 min (slides 14-19)
- Démonstration : 10 min (slides 20-29)
- Tests : 3 min (slides 30-31)
- Déploiement : 3 min (slides 32-33)
- Conclusion : 3 min (slides 34-35)

**Total : 40 minutes** ⏱️

### Conseils :
- Parler lentement et clairement
- Ne pas lire les slides (juste regarder)
- Faire des pauses entre les parties
- Montrer ta passion pour le projet
- Respirer calmément

### Captures à insérer :
- 01_home.png
- 02_biographie.png
- 03_galerie.png
- 04_musique.png
- 05_videos.png
- 06_admin_dashboard.png
- 07_admin_photos.png
- 08_admin_musique.png
- 12_admin_security.png
- 13_adminer_overview.png
- 15_docker_ps.png

---

**Créé le :** 31 octobre 2025
**Pour :** Oral CDA
**Durée présentation :** 40 minutes
**Durée questions jury :** 40 minutes
**Total :** 1h20
