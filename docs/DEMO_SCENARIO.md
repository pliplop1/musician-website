# 🎬 SCÉNARIO DE DÉMONSTRATION - ORAL CDA

Ce document contient le **script exact** de la démonstration que vous allez faire pendant l'oral (15 minutes).

---

## ⏱️ TIMING DE LA DÉMO

- **Frontend public** : 7 minutes
- **Interface d'administration** : 8 minutes
- **TOTAL** : 15 minutes

**💡 Astuce :** Répétez avec un chronomètre pour respecter le timing !

---

## 🎯 OBJECTIFS DE LA DÉMO

Le jury veut voir :
- ✅ Que l'application **fonctionne vraiment**
- ✅ Que vous **maîtrisez** ce que vous avez développé
- ✅ Les **fonctionnalités clés**
- ✅ La **qualité technique** (responsive, sécurité, UX)

---

## 🚀 PRÉPARATION AVANT LA DÉMO

### Checklist 1 semaine avant :
- [ ] Tester que l'application démarre (docker-compose up -d)
- [ ] Préparer un compte admin de test (login: admin / password: Admin123!)
- [ ] Avoir des données de démo (2-3 concerts, 5-6 photos)
- [ ] Tester sur plusieurs navigateurs (Chrome, Firefox)
- [ ] Préparer des captures d'écran de backup

### Checklist le jour J :
- [ ] Démarrer l'application 10 minutes avant
- [ ] Vérifier que http://localhost:8080 fonctionne
- [ ] Ouvrir les onglets nécessaires dans le navigateur
- [ ] Avoir de l'eau à portée de main
- [ ] Fermer toutes les applications non nécessaires

### URLs à ouvrir dans des onglets :
1. `http://localhost:8080/` (page d'accueil)
2. `http://localhost:8080/concerts` (concerts)
3. `http://localhost:8080/photos` (galerie)
4. `http://localhost:8080/contact` (formulaire)
5. `http://localhost:8080/login` (admin login)
6. `http://localhost:8080/admin` (dashboard)

---

## 📱 PARTIE 1 : FRONTEND PUBLIC (7 minutes)

### 1.1 Page d'accueil (1 min 30)

**Ce que vous dites :**
> "Je vais commencer par vous montrer l'interface publique du site, celle que les visiteurs voient.
>
> Voici la page d'accueil du Duo Black & White."

**Ce que vous montrez :**

1. **Section hero :**
   - Image d'en-tête avec titre
   - Bouton d'appel à l'action

> "La page d'accueil présente le duo avec une section hero qui attire l'attention."

2. **Scroll vers le bas :**
   - Section "Prochains concerts"
   - Section "Galerie photos"
   - Section "Écouter notre musique"

> "On a ensuite les sections principales : concerts à venir, galerie photos, et un aperçu du répertoire musical."

3. **Responsive :**
   - Réduisez la fenêtre du navigateur
   - Montrez le menu hamburger sur mobile

> "Le site est entièrement responsive. Sur mobile, le menu se transforme en hamburger et les éléments s'adaptent automatiquement."

**💡 Si ça plante :** "Je vais vous montrer des captures d'écran de la version fonctionnelle..."

---

### 1.2 Page Concerts (1 min 30)

**Changez d'onglet vers `/concerts`**

> "Passons à la page des concerts."

**Ce que vous montrez :**

1. **Liste des concerts :**
   - Cartes avec date, lieu, heure
   - Design responsive
   - Affichage de tous les concerts (à venir et passés)

> "Les visiteurs peuvent consulter tous les concerts du duo. Chaque concert affiche la date, le lieu et l'heure. Pour le moment, tous les concerts sont affichés ensemble. Une amélioration future serait de séparer les concerts à venir et l'historique."

2. **Interaction :**
   - Survolez une carte (effet hover)
   - Scrollez pour voir tous les concerts

> "Les cartes sont interactives avec des effets au survol. L'affichage est responsive et s'adapte à tous les écrans."

**Détail technique à mentionner :**
> "Ces données proviennent de l'API REST `/api/concerts` que j'ai développée avec Spring Boot. Le frontend Vue.js les récupère en JSON et les affiche dynamiquement. L'API retourne tous les concerts avec leurs informations complètes."

---

### 1.3 Galerie Photos (1 min 30)

**Changez d'onglet vers `/photos`**

> "Voyons maintenant la galerie photos."

**Ce que vous montrez :**

1. **Grille de photos :**
   - Layout responsive (grid)
   - Miniatures optimisées

> "La galerie affiche les photos du duo en grille responsive. Les images sont optimisées pour un chargement rapide."

2. **Lightbox :**
   - Cliquez sur une photo
   - Montrez la navigation dans la lightbox (flèches gauche/droite)
   - Fermez la lightbox (ESC ou bouton X)

> "Quand on clique sur une photo, elle s'ouvre en plein écran dans une lightbox avec navigation."

3. **Recherche par catégorie :**
   - Tapez une catégorie dans la barre de recherche (ex: "concert")
   - Les photos se filtrent automatiquement
   - Chaque photo affiche sa catégorie dans un badge

> "Les photos ont des catégories comme 'concert', 'studio', etc. La barre de recherche permet de filtrer les photos par catégorie, nom de fichier ou ID."

**Détail technique :**
> "Les images sont lazy-loadées, c'est-à-dire qu'elles ne se chargent que quand elles deviennent visibles. Ça améliore la performance."

---

### 1.4 Formulaire de Contact (1 min 30)

**Changez d'onglet vers `/contact`**

> "Passons au formulaire de contact."

**Ce que vous montrez :**

1. **Formulaire vide :**
   - Champs : Nom, Email, Sujet, Message

> "Les visiteurs peuvent contacter le duo via ce formulaire."

2. **Validation côté client :**
   - Essayez de soumettre le formulaire vide
   - Montrez les messages d'erreur
   - Entrez un email invalide (ex: "test")
   - Montrez l'erreur de validation

> "Le formulaire inclut une validation côté client. Si on essaie de soumettre avec des champs vides ou un email invalide, des messages d'erreur s'affichent."

3. **Soumission valide :**
   - Remplissez le formulaire correctement :
     - Nom : "Jean Dupont"
     - Email : "jean.dupont@example.com"
     - Sujet : "Demande de renseignements"
     - Message : "Bonjour, je souhaiterais réserver le duo pour un événement."
   - Cliquez sur "Envoyer"
   - Montrez le message de succès

> "Une fois correctement rempli, le message est envoyé au backend Spring Boot. Le backend valide à nouveau les données, les sauvegarde en base de données, et envoie un email de notification."

**Détails techniques :**
> "Il y a deux niveaux de validation :
> - **Côté client** avec Vue.js (UX rapide)
> - **Côté serveur** avec Bean Validation (sécurité)
>
> Le formulaire est aussi protégé contre les attaques CSRF avec un token généré par Spring Security."

---

### 1.5 Résumé Frontend (30 secondes)

> "Donc pour résumer le frontend :
> - **Vue.js 3** en SPA pour une navigation fluide
> - **Tailwind CSS** pour le design responsive
> - **Axios** pour communiquer avec l'API REST
> - **Lazy loading** et optimisations pour les performances
> - **Score Lighthouse : 95/100** (Performance, SEO, Accessibilité, Best Practices)
>
> Passons maintenant à l'interface d'administration."

---

## 🔐 PARTIE 2 : INTERFACE D'ADMINISTRATION (8 minutes)

### 2.1 Authentification (1 min 30)

**Changez d'onglet vers `/login`**

> "L'interface d'administration est protégée par Spring Security."

**Ce que vous montrez :**

1. **Tentative de connexion échouée :**
   - Entrez un mauvais mot de passe
   - Montrez le message d'erreur
   - Expliquez le rate limiting

> "Si je me trompe de mot de passe, une erreur s'affiche. Après 5 tentatives échouées, l'adresse IP est bloquée pendant 15 minutes pour éviter les attaques brute-force."

2. **Connexion réussie :**
   - Username : `admin`
   - Password : `Admin123!`
   - Cliquez sur "Se connecter"
   - Redirection vers `/admin`

> "Avec les bons identifiants, je suis redirigé vers le tableau de bord admin."

**Détails techniques :**
> "L'authentification utilise Spring Security :
> - Les mots de passe sont hashés avec **BCrypt** (algorithme unidirectionnel)
> - Les sessions sont sécurisées avec des **cookies HTTP-only** et **SameSite**
> - La protection **CSRF** est activée sur tous les formulaires"

---

### 2.2 Tableau de bord (1 min)

**Vous êtes sur `/admin`**

> "Voici le tableau de bord administrateur."

**Ce que vous montrez :**

1. **Statistiques :**
   - Nombre de concerts
   - Nombre de photos
   - Nombre de messages reçus
   - Commentaires en attente

> "Le dashboard affiche des statistiques globales sur le site."

2. **Navigation :**
   - Montrez le menu latéral ou top menu
   - Listez les sections disponibles

> "L'admin peut gérer les concerts, photos, pistes musicales, messages, commentaires, articles et liens sociaux."

**Détail technique :**
> "Cette interface utilise **Thymeleaf** (server-side rendering) plutôt que Vue.js. C'est un choix délibéré : le backoffice n'a pas besoin d'être aussi dynamique que le frontend public, et Thymeleaf simplifie la gestion des formulaires sécurisés."

---

### 2.3 Gestion des Concerts (2 min)

**Cliquez sur "Concerts" dans le menu**

> "Je vais vous montrer la gestion des concerts."

**Ce que vous montrez :**

1. **Liste des concerts :**
   - Tableau avec titre, date, lieu, statut
   - Boutons Actions (Modifier, Supprimer)

> "L'admin voit tous les concerts dans un tableau."

2. **Créer un concert :**
   - Cliquez sur "Nouveau concert"
   - Remplissez le formulaire :
     - Titre : "Concert test CDA"
     - Date : (date future)
     - Lieu : "Salle des fêtes"
     - Heure : "20:00"
     - Description : "Concert de démonstration"
   - Cliquez sur "Enregistrer"
   - Montrez le message de succès

> "Je crée un nouveau concert. Le formulaire inclut une validation : par exemple, la date ne peut pas être dans le passé."

3. **Vérification frontend :**
   - Retournez sur l'onglet `/concerts` (frontend public)
   - Rafraîchissez la page (F5)
   - Montrez que le nouveau concert apparaît

> "Si je retourne sur le frontend et rafraîchis, le nouveau concert apparaît immédiatement. Les données sont persistées en base de données MariaDB."

**Détails techniques :**
> "Le workflow complet est :
> 1. L'admin soumet le formulaire (POST /admin/concerts)
> 2. Spring MVC valide les données (Bean Validation)
> 3. Le service métier sauvegarde en BDD (Spring Data JPA)
> 4. L'API REST expose les données (/api/concerts)
> 5. Le frontend Vue.js récupère et affiche les données"

---

### 2.4 Gestion des Photos (2 min)

**Cliquez sur "Photos" dans le menu admin**

> "Voyons maintenant la gestion des photos."

**Ce que vous montrez :**

1. **Liste des photos :**
   - Miniatures avec titre, catégorie
   - Drag & drop pour réorganiser (si implémenté)

> "L'admin peut voir toutes les photos et les réorganiser par drag-and-drop pour contrôler l'ordre d'affichage."

2. **Upload d'une photo :**
   - Cliquez sur "Ajouter une photo"
   - Sélectionnez une image (ou glissez-déposez)
   - Remplissez :
     - Titre : "Photo de démo"
     - Catégorie : "Concert"
   - Cliquez sur "Enregistrer"
   - Montrez la photo uploadée

> "Je peux uploader une nouvelle photo. Le backend valide le format (JPEG, PNG) et la taille (max 20 MB) avant de la sauvegarder."

3. **Suppression :**
   - Cliquez sur "Supprimer" sur une photo
   - Confirmez la suppression
   - Montrez qu'elle a disparu

> "La suppression inclut une confirmation pour éviter les suppressions accidentelles."

**Détails techniques :**
> "L'upload de fichiers utilise `MultipartFile` de Spring Boot. Les fichiers sont stockés sur le serveur dans `/app/uploads/photos/` (dans le conteneur Docker). Les images sont accessibles via un endpoint static `/uploads/**`."

---

### 2.5 Gestion des Messages (1 min)

**Cliquez sur "Messages" dans le menu admin**

> "L'admin reçoit tous les messages envoyés via le formulaire de contact."

**Ce que vous montrez :**

1. **Liste des messages :**
   - Tableau avec nom, email, sujet, date
   - Badge "Non lu" / "Lu"

> "Voici les messages reçus. On voit le message que j'ai envoyé tout à l'heure depuis le formulaire de contact."

2. **Voir un message :**
   - Cliquez sur un message
   - Montrez le détail (nom, email, sujet, message complet)
   - Marquez comme lu

> "L'admin peut lire chaque message et le marquer comme traité."

**Détail technique :**
> "Quand un message est envoyé :
> 1. Il est sauvegardé en base de données
> 2. Un email de notification est envoyé à l'admin via SMTP Gmail
> 3. L'admin peut répondre directement par email"

---

### 2.6 Résumé Admin (30 secondes)

> "Pour résumer l'interface admin :
> - **Spring Security** pour l'authentification sécurisée
> - **Thymeleaf** pour les templates server-side
> - **Spring Data JPA** pour la persistance
> - **CRUD complet** sur toutes les entités
> - **Validation** des données à chaque étape
> - **Protection CSRF** sur tous les formulaires
>
> L'interface est simple mais complète, permettant au client de gérer tout son contenu en autonomie."

---

## 🎬 CONCLUSION DE LA DÉMO (30 secondes)

**Revenez sur le frontend (page d'accueil)**

> "Voilà pour la démonstration fonctionnelle.
>
> **Récapitulatif :**
> - ✅ Frontend Vue.js moderne et responsive
> - ✅ Backend Spring Boot sécurisé avec API REST
> - ✅ Interface admin complète avec authentification
> - ✅ Base de données MariaDB persistante
> - ✅ Déploiement Docker en un clic
> - ✅ Pipeline CI/CD avec tests automatisés
>
> L'application est **prête pour la production** et répond aux besoins du client.
>
> Avez-vous des questions sur la démonstration ?"

---

## ⚠️ GESTION DES PROBLÈMES PENDANT LA DÉMO

### Si l'application ne démarre pas :

> "Je rencontre un problème technique, mais j'ai préparé des captures d'écran qui montrent le fonctionnement normal de l'application."

**Montrez les captures d'écran de backup**

### Si une fonctionnalité plante :

> "Cette fonctionnalité rencontre un problème en démo, mais laissez-moi vous expliquer comment elle fonctionne normalement..."

**Expliquez avec des mots + montrez le code si besoin**

### Si le jury vous interrompt :

> "Oui bien sûr, je suis à votre disposition."

**Répondez à la question, puis :**

> "Puis-je continuer la démonstration ?"

### Si vous oubliez quelque chose :

> "Ah oui, j'ai oublié de mentionner un point important : [rattraper l'oubli]."

**C'est normal, le jury comprend le stress !**

---

## 📋 CHECKLIST POST-DÉMO

Après la démo, le jury va probablement poser des questions sur :

- [ ] Les choix techniques (pourquoi Vue.js ? pourquoi MariaDB ?)
- [ ] La sécurité (comment sont protégés les mots de passe ?)
- [ ] Les tests (comment avez-vous testé ?)
- [ ] Le déploiement (comment déployer en production ?)
- [ ] Les difficultés rencontrées (quels problèmes avez-vous résolus ?)

**Toutes les réponses sont dans `ORAL_CDA_QUESTIONS_REPONSES.md` !**

---

## 🎯 CONSEILS POUR UNE DÉMO RÉUSSIE

### Avant la démo :
1. **Répétez 3 fois** avec chronomètre
2. **Testez tout** la veille et le matin même
3. **Préparez des captures d'écran** de backup
4. **Fermez les applications inutiles** (Spotify, Discord, etc.)

### Pendant la démo :
1. **Parlez clairement** et pas trop vite
2. **Montrez votre enthousiasme** (sourire !)
3. **Ne vous excusez pas** pour des détails mineurs
4. **Naviguez lentement** (laissez le jury voir)
5. **Faites des pauses** (demandez "C'est clair ?")

### Si ça plante :
1. **Restez calme** (c'est pas grave)
2. **Expliquez ce qui devrait se passer**
3. **Montrez le code** si possible
4. **Passez à la suite** rapidement

---

## 💪 VOUS ALLEZ ASSURER !

**Votre démo va montrer :**
- ✅ Une application **qui fonctionne**
- ✅ Une architecture **professionnelle**
- ✅ Des fonctionnalités **complètes**
- ✅ Une sécurité **robuste**
- ✅ Une UX **soignée**

**Le jury va être impressionné ! 🏆**

---

## 📝 APRÈS LA DÉMO

Le jury va vous poser des questions pendant 40 minutes. Préparez-vous avec :

- 📄 **ORAL_CDA_QUESTIONS_REPONSES.md** (60+ questions types)
- 📄 **FICHES_REVISION_TECHNIQUES.md** (résumés par technologie)
- 📄 **ARCHITECTURE_DIAGRAMS.md** (schémas à dessiner)

**Vous êtes prêt pour l'oral CDA ! 🎓**
