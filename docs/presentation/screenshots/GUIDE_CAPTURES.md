# 📸 Guide des Captures d'Écran pour la Présentation

## 🎯 Comment prendre les captures

**Méthode rapide Windows :**
- Appuyez sur **Win + Shift + S**
- Sélectionnez la zone à capturer
- La capture est copiée dans le presse-papiers
- Collez-la dans Paint ou un éditeur d'images
- Sauvegardez avec le nom indiqué ci-dessous

**OU utilisez l'outil Capture d'écran Windows (Snipping Tool)**

---

## 📋 Liste des Captures à Prendre (15 captures)

### 🏠 PARTIE 1 - Pages Publiques (5 captures)

#### 1. Page d'Accueil
- **URL :** `http://localhost:8080/`
- **Nom fichier :** `01_home.png`
- **Que capturer :** Toute la page d'accueil avec le header et le hero
- **Points à montrer :** Design, navigation, appel à l'action

#### 2. Page Biographie
- **URL :** `http://localhost:8080/biographie`
- **Nom fichier :** `02_biographie.png`
- **Que capturer :** Section biographie avec le texte formaté
- **Points à montrer :** Rendu Markdown, mise en page

#### 3. Page Galerie
- **URL :** `http://localhost:8080/galerie`
- **Nom fichier :** `03_galerie.png`
- **Que capturer :** La grille de photos avec la barre de recherche
- **Points à montrer :** Design responsive, recherche fonctionnelle

#### 4. Page Musique
- **URL :** `http://localhost:8080/musique`
- **Nom fichier :** `04_musique.png`
- **Que capturer :** Liste des morceaux avec intégration Spotify
- **Points à montrer :** Cartes musicales, recherche

#### 5. Page Vidéos
- **URL :** `http://localhost:8080/videos`
- **Nom fichier :** `05_videos.png`
- **Que capturer :** Grille de vidéos
- **Points à montrer :** Layout responsive

---

### 🔐 PARTIE 2 - Interface Admin (7 captures)

**⚠️ Connectez-vous d'abord avec pliplop / Test123456! à http://localhost:8080/login**

#### 6. Dashboard Admin
- **URL :** `http://localhost:8080/admin/dashboard`
- **Nom fichier :** `06_admin_dashboard.png`
- **Que capturer :** Vue d'ensemble du dashboard
- **Points à montrer :** Statistiques, aperçu des données

#### 7. Gestion Photos
- **URL :** `http://localhost:8080/admin/photos`
- **Nom fichier :** `07_admin_photos.png`
- **Que capturer :** Interface de gestion des photos
- **Points à montrer :** Upload, drag-and-drop, gestion

#### 8. Gestion Musique
- **URL :** `http://localhost:8080/admin/tracks`
- **Nom fichier :** `08_admin_musique.png`
- **Que capturer :** Interface de gestion des morceaux
- **Points à montrer :** Formulaire d'ajout, liste

#### 9. Gestion Événements
- **URL :** `http://localhost:8080/admin/concerts`
- **Nom fichier :** `09_admin_concerts.png`
- **Que capturer :** Liste des concerts
- **Points à montrer :** CRUD concerts, dates

#### 10. Modération Commentaires
- **URL :** `http://localhost:8080/admin/comments`
- **Nom fichier :** `10_admin_comments.png`
- **Que capturer :** Interface de modération
- **Points à montrer :** Approbation, suppression

#### 11. Messages de Contact
- **URL :** `http://localhost:8080/admin/messages`
- **Nom fichier :** `11_admin_messages.png`
- **Que capturer :** Liste des messages reçus
- **Points à montrer :** Gestion messages, statuts

#### 12. Tableau de Bord Sécurité
- **URL :** `http://localhost:8080/admin/security`
- **Nom fichier :** `12_admin_security.png`
- **Que capturer :** Dashboard sécurité avec tentatives de connexion
- **Points à montrer :** Protection anti-brute force

---

### 🗄️ PARTIE 3 - Base de Données (2 captures)

#### 13. Adminer - Vue d'ensemble
- **URL :** `http://localhost:8081/`
- **Connexion :** Système: MySQL, Serveur: `database`, Utilisateur: `musician_user`, Mot de passe: `SecurePassword123!`, Base: `musician_db`
- **Nom fichier :** `13_adminer_overview.png`
- **Que capturer :** Liste des tables après connexion
- **Points à montrer :** Structure de la BDD

#### 14. Adminer - Table Photos
- **URL :** Cliquez sur la table `photos` dans Adminer
- **Nom fichier :** `14_adminer_photos_table.png`
- **Que capturer :** Structure de la table photos
- **Points à montrer :** Colonnes, types, relations

---

### 🔧 PARTIE 4 - Technique (1 capture)

#### 15. Docker Containers
- **Commande dans terminal :** `docker-compose ps`
- **Nom fichier :** `15_docker_ps.png`
- **Que capturer :** Résultat de la commande montrant les 3 containers
- **Points à montrer :** app, database, adminer en état "healthy"

---

## ✅ Checklist de Vérification

Avant de prendre les captures, vérifiez que :
- [ ] Les containers Docker sont démarrés (`docker-compose up -d`)
- [ ] L'application est accessible sur http://localhost:8080/
- [ ] Vous êtes connecté comme admin pour les captures 6-12
- [ ] La fenêtre du navigateur est en taille raisonnable (pas trop petite)
- [ ] Pas d'onglets personnels visibles dans les captures
- [ ] Barre de favoris masquée si possible (pour un rendu propre)

---

## 📁 Organisation des Fichiers

Sauvegardez **TOUTES** les captures dans ce dossier :
```
C:\Users\plipl\Documents\workspace-spring-tools-for-eclipse-4.31.0.RELEASE\musician-website\docs\presentation\screenshots\
```

---

## ⏱️ Timing

**Temps estimé :** 15-20 minutes pour prendre toutes les captures

**Astuce :** Ouvrez toutes les URLs dans des onglets séparés, puis prenez les captures une par une dans l'ordre.

---

## 🆘 Problèmes Courants

**"Je ne vois pas la page admin"**
→ Connectez-vous d'abord à http://localhost:8080/login avec pliplop / Test123456!

**"Adminer refuse la connexion"**
→ Utilisez `database` comme serveur (PAS `musician-db`)

**"L'application ne répond pas"**
→ Vérifiez avec `docker-compose ps` que tous les containers sont UP et healthy

---

## 📢 Après avoir pris les captures

Une fois que vous aurez sauvegardé toutes les images, **prévenez-moi** et je :
1. Créerai automatiquement votre présentation LibreOffice Impress complète
2. Intégrerai les captures aux bons endroits
3. Ajouterai les diagrammes SVG
4. Générerai les notes de présentation

Bon courage ! 🚀 
