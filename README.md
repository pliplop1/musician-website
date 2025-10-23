# Duo Black & White - Site Web Officiel

[![CI/CD Pipeline](https://github.com/pliplop1/musician-website/actions/workflows/ci.yml/badge.svg)](https://github.com/pliplop1/musician-website/actions/workflows/ci.yml)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.4-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Vue.js](https://img.shields.io/badge/Vue.js-3-green.svg)](https://vuejs.org/)
[![Docker](https://img.shields.io/badge/Docker-Ready-blue.svg)](https://www.docker.com/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

Site web professionnel pour le Duo Black & White, groupe musical d'Abbeville composé de Marilyne Dumoulin (chanteuse) et Philippe Prudhomme (guitariste/chanteur).

## Vue d'ensemble

Application web moderne combinant un backend Spring Boot robuste avec un frontend Vue.js 3 élégant et réactif. Le site présente la biographie du duo, leur répertoire musical, une galerie photos, les événements à venir et un formulaire de contact.

## Fonctionnalités

### Frontend Public (Vue.js 3)
- Page d'accueil avec section hero animée
- Biographie complète du duo avec rendu Markdown
- Répertoire musical avec intégration Spotify
- Galerie photos responsive avec lightbox
- Calendrier des événements (passés et à venir)
- Formulaire de contact avec validation
- Design responsive mobile-first
- Animations fluides et transitions élégantes

### Backend API REST (Spring Boot)
- API REST publique pour le frontend Vue.js
- Gestion complète des erreurs avec messages standardisés
- Validation des données avec Bean Validation
- Sécurité Spring Security avec authentification par formulaire
- Interface d'administration protégée (Thymeleaf)
- Envoi d'emails automatisé
- Système de commentaires avec modération
- Gestion des badges et notifications

### Administration (Thymeleaf)
- Tableau de bord avec statistiques
- Gestion de la biographie
- Gestion des concerts
- Gestion des photos (upload, réorganisation drag-and-drop)
- Gestion du répertoire musical
- Modération des commentaires
- Gestion des messages de contact
- Gestion des liens sociaux
- Recherche et tri pour toutes les sections

## Stack Technique

### Backend
- **Java 21**
- **Spring Boot 3.2.4**
  - Spring Data JPA
  - Spring Security
  - Spring Boot Validation
  - Spring Boot Mail
- **MariaDB** (base de données)
- **Thymeleaf** (templates admin)
- **Lombok** (réduction du boilerplate)

### Frontend
- **Vue.js 3** (Composition API avec `<script setup>`)
- **Vite 7.1.9** (build tool et dev server)
- **Tailwind CSS** (framework CSS utilitaire)
- **Axios** (client HTTP)

### Build et Déploiement
- **Maven 3.x**
- **frontend-maven-plugin** (intégration Node.js/npm dans Maven)
- **Node.js 22.17.0** / **npm 10.8.2**

## Prérequis

- **Java JDK 17** ou supérieur
- **Maven 3.6+**
- **MariaDB 10.5+** (ou MySQL 8.0+)
- **Node.js 22.17.0+** (installé automatiquement par Maven)
- **npm 10.8.2+** (installé automatiquement par Maven)

## Installation

### 1. Cloner le repository

```bash
git clone <repository-url>
cd musician-website
```

### 2. Configuration de la base de données

Créer une base de données MariaDB/MySQL :

```sql
CREATE DATABASE musician_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'musician_user'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON musician_db.* TO 'musician_user'@'localhost';
FLUSH PRIVILEGES;
```

### 3. Configuration de l'application

Modifier `src/main/resources/application.properties` :

```properties
# Base de données
spring.datasource.url=jdbc:mariadb://localhost:3306/musician_db
spring.datasource.username=musician_user
spring.datasource.password=your_password

# Email (optionnel pour dev)
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
```

### 4. Installation des dépendances

Maven gère automatiquement l'installation de Node.js et toutes les dépendances :

```bash
mvn clean install
```

Cette commande va :
1. Télécharger et installer Node.js 22.17.0 et npm 10.8.2
2. Installer les dépendances npm du frontend Tailwind
3. Installer les dépendances npm du frontend Vue.js
4. Builder le CSS Tailwind
5. Builder l'application Vue.js
6. Compiler le backend Java
7. Créer le package JAR exécutable

## Démarrage de l'application

### Mode Développement (avec données de démonstration)

#### Backend Spring Boot
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```
L'application démarre sur http://localhost:8106

#### Frontend Vue.js (dev server avec hot-reload)
```bash
cd vue-frontend
npm install
npm run dev
```
Le frontend démarre sur http://localhost:5173 (ou 5174/5175 si le port est occupé)

### Mode Production

```bash
mvn clean package
java -jar target/musician-website-0.0.1-SNAPSHOT.jar
```

Le frontend buildé est automatiquement inclus dans le JAR et servi par Spring Boot.

## Structure du projet

```
musician-website/
├── src/main/
│   ├── java/com/docker/
│   │   ├── config/           # Configuration Spring (Security, CORS, etc.)
│   │   ├── controller/       # Contrôleurs REST et MVC
│   │   │   ├── api/          # API REST publique pour Vue.js
│   │   │   └── Admin*.java   # Contrôleurs admin Thymeleaf
│   │   ├── entity/           # Entités JPA
│   │   ├── repository/       # Repositories Spring Data
│   │   ├── service/          # Services métier
│   │   ├── dto/              # Data Transfer Objects
│   │   └── exception/        # Exceptions personnalisées et gestionnaires
│   └── resources/
│       ├── application.properties  # Configuration principale
│       ├── static/           # Ressources statiques
│       │   ├── css/          # CSS Tailwind buildé
│       │   ├── js/           # JavaScript admin
│       │   ├── uploads/      # Photos uploadées
│       │   └── vue/          # Frontend Vue.js buildé (en production)
│       └── templates/        # Templates Thymeleaf (admin)
├── frontend/                 # Frontend Tailwind CSS
│   ├── src/input.css        # CSS source Tailwind
│   ├── tailwind.config.js   # Configuration Tailwind
│   └── package.json
├── vue-frontend/            # Frontend Vue.js 3
│   ├── src/
│   │   ├── components/      # Composants Vue réutilisables
│   │   ├── App.vue          # Composant racine
│   │   └── main.js          # Point d'entrée
│   ├── public/              # Assets publics
│   ├── vite.config.js       # Configuration Vite
│   └── package.json
├── pom.xml                  # Configuration Maven
└── README.md               # Ce fichier
```

## API REST - Documentation

### Endpoints publics (frontend Vue.js)

Toutes les routes API sont préfixées par `/api`

#### Biographie
- `GET /api/biography` - Récupère la biographie

#### Concerts
- `GET /api/concerts` - Liste tous les concerts
- `GET /api/concerts/{id}` - Détails d'un concert
- `GET /api/concerts/upcoming` - Concerts à venir uniquement
- `GET /api/concerts/past` - Concerts passés uniquement

#### Photos
- `GET /api/photos` - Liste toutes les photos
- `GET /api/photos/{id}` - Détails d'une photo

#### Répertoire
- `GET /api/tracks` - Liste tous les morceaux
- `GET /api/tracks/{id}` - Détails d'un morceau

#### Messages
- `POST /api/messages` - Envoyer un message de contact
  ```json
  {
    "name": "string",
    "email": "email@example.com",
    "subject": "string",
    "message": "string"
  }
  ```

#### Commentaires
- `GET /api/comments` - Commentaires approuvés
- `POST /api/comments` - Poster un commentaire
  ```json
  {
    "author": "string",
    "email": "email@example.com",
    "content": "string"
  }
  ```

### Gestion des erreurs API

Toutes les erreurs sont retournées au format standardisé :

```json
{
  "timestamp": "2025-10-10T12:34:56",
  "status": 404,
  "error": "Not Found",
  "message": "Concert not found with id: '123'",
  "path": "/api/concerts/123"
}
```

## Développement

### Backend - Hot Reload

Spring Boot DevTools est configuré pour le hot-reload automatique.

### Frontend Vue.js - Hot Reload

Le dev server Vite recharge automatiquement les modifications.

### Données de démonstration

En mode développement (profil `dev`), des données de démonstration sont chargées automatiquement :
- Biographie complète du Duo Black & White
- 8 concerts (3 passés, 5 à venir)
- 6 photos
- 5 morceaux avec embeds Spotify

Ces données sont initialisées par `DemoDataInitializer.java`

## Compte Administrateur

Par défaut, un compte admin est créé :
- **Username:** `admin`
- **Password:** `admin123`

**Important:** Changez ce mot de passe en production dans `SecurityConfig.java`

## Build Production

Pour créer le JAR de production avec le frontend intégré :

```bash
mvn clean package -DskipTests
```

Le fichier JAR généré (`target/musician-website-0.0.1-SNAPSHOT.jar`) contient :
- L'application Spring Boot
- Le frontend Vue.js buildé
- Toutes les dépendances

Déployez simplement ce JAR sur votre serveur :

```bash
java -jar musician-website-0.0.1-SNAPSHOT.jar
```

## Configuration Production

### Variables d'environnement recommandées

```bash
# Base de données
SPRING_DATASOURCE_URL=jdbc:mariadb://db-server:3306/musician_db
SPRING_DATASOURCE_USERNAME=prod_user
SPRING_DATASOURCE_PASSWORD=secure_password

# Email
SPRING_MAIL_HOST=smtp.server.com
SPRING_MAIL_PORT=587
SPRING_MAIL_USERNAME=noreply@duoblackandwhite.com
SPRING_MAIL_PASSWORD=secure_email_password

# Application
SERVER_PORT=8080
SPRING_PROFILES_ACTIVE=prod
```

### Fichier application-prod.properties

Créez `src/main/resources/application-prod.properties` :

```properties
# Désactiver les données de démonstration
spring.profiles.active=prod

# JPA - Ne pas recréer le schéma
spring.jpa.hibernate.ddl-auto=validate

# Logs
logging.level.root=WARN
logging.level.com.docker=INFO
```

## Tests

- Backend (Spring Boot, JUnit)
  - `mvn -q -DskipITs test`
- Frontend (Vue 3, Vitest)
  - `cd vue-frontend && npm install && npm test`
  - Lint: `cd vue-frontend && npm run lint`
  - Format: `cd vue-frontend && npm run format`

- Qualité & Sécurité (Maven):
  - `mvn -q -DskipITs -Pquality verify` (Checkstyle, SpotBugs, OWASP Dependency-Check)

Intégration continue: un workflow GitHub Actions exécute automatiquement les tests backend et frontend à chaque `push` et `pull_request` (voir `.github/workflows/ci.yml`).

SEO/Accessibilité/Performance:
- SEO: métadonnées Open Graph/Twitter, `robots.txt`, balise `lang="fr"` et `meta description` ajoutées (`vue-frontend/index.html`, `vue-frontend/public/robots.txt`).
- Accessibilité: composant `CookieConsent.vue`, liens d’évitement, landmarks ARIA, test axe-core minimal.
- Performance: preconnect CDN, noscript, CI lint + audit, JaCoCo déjà configuré.

## Sécurité

- Spring Security avec authentification par formulaire
- Protection CSRF
- Headers de sécurité configurés
- Validation des entrées avec Bean Validation
- Gestion centralisée des erreurs
- CORS configuré pour le développement

## Licence

Propriétaire - Duo Black & White

## Contact

**Duo Black & White**
- Email: dumoulin.marilyne@gmail.com
- Téléphone: 06 01 23 45 47
- Facebook: [DuoBlackandWhiteMP](https://www.facebook.com/DuoBlackandWhiteMP/)
- Localisation: Abbeville, France

---

Développé avec Java 17, Spring Boot 3, Vue.js 3 et Vite.
