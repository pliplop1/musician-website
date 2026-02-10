# 🎯 SCRIPT ORAL CDA - PRÉSENTATION 40 MINUTES

## ⏱️ TIMING GLOBAL
- **Introduction** : 3 minutes
- **Architecture technique** : 8 minutes
- **Démonstration fonctionnelle** : 15 minutes
- **Aspects techniques avancés** : 10 minutes
- **Conclusion** : 4 minutes

---

## 1️⃣ INTRODUCTION (3 minutes)

### **Ce que vous dites :**

> "Bonjour, je vais vous présenter mon projet de titre professionnel Concepteur Développeur d'Applications : un site web pour le **Duo Black & White**, un groupe musical professionnel d'Abbeville.

> **Contexte :** Le Duo Black & White, composé de Marilyne Dumoulin (chanteuse) et Philippe Prudhomme (guitariste), avait besoin d'un site web moderne pour :
> - Présenter leur biographie et leur répertoire musical
> - Afficher leurs concerts à venir
> - Permettre au public de les contacter
> - Gérer leurs contenus via une interface d'administration

> **Mon rôle :** Conception, développement et déploiement complet de l'application, de la base de données jusqu'à l'interface utilisateur, en passant par la sécurité et les tests.

> **Durée du projet :** 3 mois (conception, développement, tests, déploiement)"

### **💡 Points clés à mentionner :**
- ✅ Client réel = projet professionnel
- ✅ Besoin fonctionnel clair
- ✅ Vous avez tout fait de A à Z

---

## 2️⃣ ARCHITECTURE TECHNIQUE (8 minutes)

### **2.1 Vue d'ensemble (2 min)**

> "J'ai conçu une application web moderne basée sur une **architecture 3 tiers** :

> **Tier 1 - Frontend :**
> - Vue.js 3 pour l'interface utilisateur publique
> - Design responsive mobile-first avec Tailwind CSS
> - Single Page Application (SPA) pour une navigation fluide

> **Tier 2 - Backend :**
> - Spring Boot 3.2.4 avec Java 21
> - API REST pour communiquer avec le frontend
> - Interface d'administration Thymeleaf pour le backoffice
> - Spring Security pour l'authentification et l'autorisation

> **Tier 3 - Base de données :**
> - MariaDB 10.11 en production
> - H2 en mémoire pour les tests
> - Flyway pour les migrations de schéma"

### **2.2 Schéma à dessiner au tableau (2 min)**

```
┌─────────────────┐
│   NAVIGATEUR    │
│   (Vue.js 3)    │
└────────┬────────┘
         │ HTTPS
         │ API REST
┌────────▼────────┐
│  SPRING BOOT    │
│  + Security     │
└────────┬────────┘
         │ JDBC
┌────────▼────────┐
│    MariaDB      │
└─────────────────┘
```

> "Cette séparation en couches permet :
> - Une maintenance facilitée (chaque couche est indépendante)
> - Une scalabilité (on peut ajouter des serveurs backend)
> - Une sécurité renforcée (le frontend n'accède jamais directement à la BDD)"

### **2.3 Technologies choisies et justifications (4 min)**

| Technologie | Justification |
|-------------|---------------|
| **Spring Boot** | Framework Java de référence en entreprise, mature et bien documenté. Simplifie la configuration avec les starters. |
| **Vue.js 3** | Framework JavaScript moderne et léger. Courbe d'apprentissage douce. Composition API moderne. |
| **MariaDB** | Base de données relationnelle open-source compatible MySQL. Performante et stable. |
| **Docker** | Containerisation pour garantir que l'application fonctionne partout de la même façon. Simplifie le déploiement. |
| **GitHub Actions** | CI/CD intégré à GitHub. Tests automatisés à chaque push. Gratuit pour les projets open-source. |

> "J'ai choisi ces technologies car elles sont :
> - **Modernes** : encore maintenues et utilisées en 2025
> - **Documentées** : beaucoup de ressources disponibles
> - **Professionnelles** : utilisées en entreprise
> - **Open-source** : pas de coûts de licences"

---

## 3️⃣ DÉMONSTRATION FONCTIONNELLE (15 minutes)

### **3.1 Frontend Public (7 min)**

**🌐 Page d'accueil**
> "Voici la page d'accueil du site. Elle présente :
> - Une section hero avec image et appel à l'action
> - Les prochains concerts à venir
> - Les dernières photos du duo
> - Un lecteur audio intégré pour le répertoire musical"

**Montrer :**
- Navigation responsive (réduire la fenêtre)
- Menu hamburger sur mobile
- Animations au scroll

**📅 Page Concerts**
> "La page concerts affiche les événements à venir et passés.
> Les données sont récupérées via l'API REST `/api/concerts/upcoming`"

**Montrer :**
- Liste des concerts
- Filtrage futur/passé
- Affichage responsive

**🖼️ Galerie Photos**
> "La galerie utilise une lightbox pour afficher les photos en grand format.
> Les images sont optimisées et lazy-loaded pour les performances."

**Montrer :**
- Grille responsive de photos
- Lightbox avec navigation
- Catégories de photos

**📧 Formulaire de contact**
> "Le formulaire de contact inclut :
> - Validation côté client (Vue.js)
> - Validation côté serveur (Spring Validation)
> - Protection CSRF
> - Envoi d'email via SMTP Gmail"

**Montrer :**
- Remplir le formulaire
- Erreurs de validation
- Message de succès

### **3.2 Interface d'administration (8 min)**

**🔐 Authentification**
> "L'accès à l'interface admin est protégé par Spring Security :
> - Authentification par formulaire
> - Mots de passe hashés avec BCrypt
> - Protection contre brute-force avec rate limiting
> - Sessions sécurisées (cookies HTTP-only)"

**Montrer :**
- Page de login
- Tentative de connexion échouée
- Connexion réussie et redirection

**📊 Tableau de bord admin**
> "Le tableau de bord affiche :
> - Statistiques (nombre de concerts, photos, messages)
> - Derniers messages reçus
> - Activité récente"

**Montrer :**
- Vue d'ensemble
- Navigation dans le menu admin

**🎵 Gestion des concerts**
> "L'admin peut créer, modifier et supprimer des concerts.
> Les données sont persistées en base MariaDB."

**Montrer :**
- Liste des concerts
- Création d'un nouveau concert
- Modification
- Validation des champs

**📸 Gestion des photos**
> "Upload de photos avec :
> - Validation du format et de la taille
> - Stockage sur le serveur
> - Réorganisation drag-and-drop
> - Catégorisation"

**Montrer :**
- Upload d'une image
- Réorganisation par drag-and-drop
- Suppression

---

## 4️⃣ ASPECTS TECHNIQUES AVANCÉS (10 minutes)

### **4.1 Sécurité (3 min)**

> "La sécurité a été une priorité dans ce projet. J'ai mis en place :

> **Authentification et autorisation :**
> - Spring Security avec authentification par formulaire
> - Rôles ROLE_ADMIN et ROLE_USER
> - Mots de passe hashés avec BCrypt (algorithme unidirectionnel)
> - Sessions sécurisées avec cookies HTTP-only et SameSite

> **Protection contre les attaques :**
> - Protection CSRF (Cross-Site Request Forgery) activée
> - Rate limiting sur le login (max 5 tentatives)
> - Validation des entrées utilisateur (Bean Validation)
> - Protection XSS (Thymeleaf échappe automatiquement le HTML)

> **HTTPS :**
> - Configuration TLS en production
> - Redirection HTTP → HTTPS automatique"

### **4.2 Tests (3 min)**

> "J'ai développé une suite de tests complète :

> **Tests unitaires (534 tests) :**
> - Tests des services avec Mockito
> - Tests des contrôleurs avec MockMvc
> - Tests des repositories avec H2 en mémoire
> - Couverture de code mesurée avec JaCoCo

> **Tests d'intégration (11 tests) :**
> - Tests du contexte Spring complet
> - Tests de la persistance avec base H2 réelle
> - Validation des transactions

> **Résultat : 545 tests, 544 réussis = 99,8% de succès**

> Les tests sont exécutés automatiquement à chaque push via GitHub Actions."

### **4.3 CI/CD (2 min)**

> "J'ai mis en place un pipeline CI/CD avec GitHub Actions :

> **À chaque push sur la branche main :**
> 1. Build du backend Maven + tests unitaires
> 2. Build du frontend Vue.js + tests Vitest
> 3. Tests E2E avec Playwright
> 4. Audit Lighthouse (SEO, Performance, Accessibilité)
> 5. Build de l'image Docker
> 6. Scan de sécurité avec Trivy
> 7. Push vers GitHub Container Registry

> **Résultat :** Score Lighthouse 95/100 !

> Ce pipeline garantit qu'aucun bug ne passe en production."

### **4.4 Déploiement (2 min)**

> "Le déploiement utilise Docker et docker-compose :

> **3 conteneurs :**
> - `musician-db` : MariaDB 10.11
> - `musician-app` : Spring Boot + Vue.js
> - `musician-adminer` : Interface web pour gérer la BDD

> **Avantages :**
> - Déploiement en une commande : `docker-compose up -d`
> - Environnement reproductible
> - Isolation des services
> - Facile à déployer sur n'importe quel serveur

> Le projet inclut aussi :
> - Volumes Docker pour persister les données
> - Healthchecks pour surveiller l'état des services
> - Variables d'environnement pour la configuration"

---

## 5️⃣ CONCLUSION (4 minutes)

### **5.1 Compétences CDA validées (1 min 30)**

> "Ce projet me permet de valider les compétences du référentiel CDA :

> **Bloc 1 - Développer une application sécurisée :**
> - ✅ CP01 : Maquetter l'interface (Vue.js responsive)
> - ✅ CP02 : Développer le frontend (Vue.js 3 + API REST)
> - ✅ CP03 : Développer le backend sécurisé (Spring Boot + Security)

> **Bloc 2 - Concevoir et développer en couches :**
> - ✅ CP04 : Concevoir la persistance (MariaDB, relations, contraintes)
> - ✅ CP05 : Architecture multicouche (3-tier)
> - ✅ CP06 : Composants d'accès aux données (Spring Data JPA)
> - ✅ CP07 : Composants métier (Services)

> **Bloc 3 - Préparer le déploiement :**
> - ✅ CP08 : Tests d'intégration (545 tests)
> - ✅ CP09 : Environnement de test et CI (GitHub Actions)
> - ✅ CP10 : Déploiement DevOps (Docker + CI/CD)
> - ✅ CP11 : Automatisation (Pipeline complet)"

### **5.2 Difficultés rencontrées et solutions (1 min 30)**

> "**Difficulté 1 : Configuration SMTP**
> - Problème : Microsoft a désactivé l'authentification basique SMTP
> - Solution : Migration vers Gmail avec mot de passe d'application

> **Difficulté 2 : Gestion des fichiers volumineux**
> - Problème : Upload de vidéos et musiques échouait
> - Solution : Configuration `spring.servlet.multipart.max-file-size=20MB`

> **Difficulté 3 : Performance frontend**
> - Problème : Score Lighthouse initial à 70/100
> - Solution : Lazy loading, compression images, Font Awesome optimisé
> - Résultat : 95/100"

### **5.3 Améliorations futures (1 min)**

> "Pistes d'amélioration si je devais continuer le projet :

> - **Fonctionnalités :**
>   - Système de réservation de concerts
>   - Espace membre avec profils utilisateurs
>   - Newsletter automatique

> - **Technique :**
>   - Migration vers une base PostgreSQL
>   - Mise en place de Redis pour le cache
>   - Monitoring avec Prometheus + Grafana
>   - Déploiement Kubernetes pour la scalabilité

> Merci pour votre attention, je suis prêt à répondre à vos questions."

---

## 📝 MÉMO - POINTS CLÉS À RETENIR

### ✅ Ce que le jury veut entendre :
- Vous comprenez l'architecture globale
- Vous savez justifier vos choix techniques
- Vous avez pensé à la sécurité
- Vous testez votre code
- Vous savez déployer

### ❌ Ce qu'il faut éviter :
- Dire "je ne sais pas" sans réfléchir
- Mentir sur ce que vous avez fait
- Critiquer les technologies que vous avez utilisées
- Paniquer si une démo plante

### 💡 Si une démo plante :
> "Je rencontre un problème technique en démo, mais voici comment ça fonctionne normalement [expliquer]. J'ai des captures d'écran de backup si besoin."

### 💡 Si on vous pose une question trop technique :
> "Je n'ai pas approfondi ce point spécifique, mais voici comment j'ai abordé le problème dans mon contexte [répondre sur ce que vous savez]."

---

## ⏱️ CHECKLIST AVANT L'ORAL

### Une semaine avant :
- [ ] Répéter la présentation 3 fois
- [ ] Chronométrer (doit faire 40 min ±2 min)
- [ ] Vérifier que la démo fonctionne
- [ ] Préparer des captures d'écran de backup

### La veille :
- [ ] Tester l'environnement de démo
- [ ] Relire les fiches techniques
- [ ] Dormir 8 heures

### Le jour J :
- [ ] Arriver 15 minutes en avance
- [ ] Respirer calmement
- [ ] Avoir de l'eau
- [ ] Sourire et montrer votre passion !

**Vous allez assurer ! 🚀**
