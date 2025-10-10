# Vue.js Frontend - Musician Website

## Structure du Projet

Ce projet est divisé en deux parties:
- **Backend Spring Boot** (port 8106) - API REST + Admin Thymeleaf
- **Frontend Vue.js** (port 5173 en dev) - SPA public moderne

## Modes de Développement

### Mode Développement (2 serveurs séparés)

**Backend (Spring Boot):**
```bash
# Depuis la racine du projet
mvn spring-boot:run
```
Accessible sur: http://localhost:8106
- Admin Thymeleaf: http://localhost:8106/admin
- API REST: http://localhost:8106/api/public/*

**Frontend (Vue.js):**
```bash
# Depuis le dossier vue-frontend
cd vue-frontend
npm install
npm run dev
```
Accessible sur: http://localhost:5173

### Mode Production (1 seul serveur)

```bash
# Build complet
mvn clean package

# Lancer
java -jar target/musician-website-0.0.1-SNAPSHOT.jar
```

Tout sur: http://localhost:8106
- Frontend: http://localhost:8106/public
- Admin: http://localhost:8106/admin

## Architecture

### Backend - API REST

- GET /api/public/hero
- GET /api/public/biography
- GET /api/public/discography
- GET /api/public/gallery
- GET /api/public/concerts/upcoming
- GET /api/public/stats

### Frontend - Composants

- HeroSection.vue - Section héros
- BiographySection.vue - Biographie
- DiscographySection.vue - Albums
- GallerySection.vue - Photos
- ConcertsSection.vue - Concerts
