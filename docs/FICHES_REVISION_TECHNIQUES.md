# 📚 FICHES DE RÉVISION TECHNIQUES - ORAL CDA

Ce document contient des **résumés concis** de chaque technologie utilisée dans votre projet. **À apprendre par cœur** pour l'oral !

---

## 🎯 COMMENT UTILISER CES FICHES

1. **Lisez une fiche par jour** (10-15 minutes)
2. **Notez ce que vous ne comprenez pas** et creusez
3. **Répétez à voix haute** les définitions
4. **Reliez avec VOTRE projet** (exemples concrets)

**Ne pas essayer d'apprendre tout d'un coup !**

---

## 📑 SOMMAIRE

1. [Spring Boot](#1-spring-boot)
2. [Spring Security](#2-spring-security)
3. [Spring Data JPA](#3-spring-data-jpa)
4. [Vue.js 3](#4-vuejs-3)
5. [MariaDB](#5-mariadb)
6. [Docker](#6-docker)
7. [GitHub Actions (CI/CD)](#7-github-actions-cicd)
8. [REST API](#8-rest-api)
9. [Thymeleaf](#9-thymeleaf)
10. [Tests (JUnit, Mockito)](#10-tests-junit-mockito)

---

## 1. SPRING BOOT

### 📖 Définition simple
Framework Java qui simplifie le développement d'applications Spring avec une configuration automatique et un serveur embarqué.

### 🎯 À savoir par cœur (10 points)

1. **Framework** : Basé sur Spring Framework, ajoute l'auto-configuration
2. **Serveur embarqué** : Tomcat intégré, pas besoin de serveur externe
3. **Starters** : Dépendances prépackagées (`spring-boot-starter-web`, `-data-jpa`, `-security`)
4. **Auto-configuration** : Détecte les dépendances et configure automatiquement
5. **Convention over configuration** : Fonctionne out-of-the-box avec peu de config
6. **Application.properties** : Fichier central de configuration
7. **@SpringBootApplication** : Annotation principale qui active tout
8. **Actuator** : Monitoring et health checks (/actuator/health)
9. **Production-ready** : Métriques, logs, gestion d'erreurs incluses
10. **Ecosystème** : Intégration facile avec Spring Security, JPA, etc.

### 💡 Dans votre projet
- **Version** : Spring Boot 3.2.4 avec Java 21
- **Utilisé pour** : API REST, authentification, persistance, emails
- **Pourquoi** : Mature, bien documenté, standard en entreprise

### 🗣️ Phrase à dire à l'oral
> "J'ai choisi Spring Boot car c'est le framework Java de référence en entreprise. Il simplifie énormément le développement avec son système d'auto-configuration et son serveur Tomcat embarqué. Ça me permet de me concentrer sur la logique métier plutôt que sur la configuration."

---

## 2. SPRING SECURITY

### 📖 Définition simple
Framework de sécurité pour les applications Spring qui gère l'authentification, l'autorisation et la protection contre les attaques.

### 🎯 À savoir par cœur (10 points)

1. **Authentification** : Vérifier l'identité (login/password)
2. **Autorisation** : Vérifier les permissions (rôles : ADMIN, USER)
3. **Filter Chain** : Chaîne de filtres qui interceptent toutes les requêtes
4. **SecurityContext** : Stocke les infos de l'utilisateur connecté
5. **Password Encoder** : BCrypt pour hasher les mots de passe
6. **CSRF Protection** : Protection contre Cross-Site Request Forgery
7. **Session Management** : Gestion des sessions avec cookies sécurisés
8. **Method Security** : `@PreAuthorize("hasRole('ADMIN')")` sur les méthodes
9. **Remember Me** : "Se souvenir de moi" avec tokens persistants
10. **Logout** : Invalidation de session + suppression des cookies

### 💡 Dans votre projet
- **Authentification** : Formulaire de login (/login)
- **Rôles** : ROLE_ADMIN pour l'interface admin
- **BCrypt** : Mots de passe hashés en base
- **CSRF** : Activé sur tous les formulaires POST/PUT/DELETE
- **Rate limiting** : Max 5 tentatives de login

### 🗣️ Phrase à dire à l'oral
> "Spring Security gère toute la sécurité de l'application. Les utilisateurs s'authentifient via un formulaire, leurs mots de passe sont hashés avec BCrypt (algorithme unidirectionnel impossible à déchiffrer), et l'accès à l'interface admin est restreint au rôle ADMIN. J'ai aussi activé la protection CSRF contre les attaques par formulaire malveillant."

---

## 3. SPRING DATA JPA

### 📖 Définition simple
Abstraction au-dessus de JPA qui simplifie l'accès aux données avec des repositories et des méthodes auto-générées.

### 🎯 À savoir par cœur (10 points)

1. **JPA (Java Persistence API)** : Spécification Java pour la persistance objet-relationnel
2. **ORM (Object-Relational Mapping)** : Mapper des objets Java ↔ tables SQL
3. **Repository** : Interface qui fournit les méthodes CRUD
4. **Entity** : Classe Java annotée @Entity qui représente une table
5. **@Id** : Clé primaire d'une entité
6. **Relations** : @OneToMany, @ManyToOne, @ManyToMany
7. **Query Methods** : Méthodes auto-générées (findByEmail, findByDateAfter)
8. **@Transactional** : Gestion automatique des transactions (COMMIT/ROLLBACK)
9. **Hibernate** : Implémentation JPA par défaut dans Spring Boot
10. **Lazy/Eager Loading** : Chargement différé ou immédiat des relations

### 💡 Dans votre projet
- **Entités** : User, Concert, Photo, Track, Message, Comment, etc.
- **Repositories** : ConcertRepository, PhotoRepository, etc.
- **Relations** : User ↔ Concert (favoris), User → Comment, etc.
- **Query Methods** : `findByDateAfter(LocalDate)`, `findByEmail(String)`

### 🗣️ Phrase à dire à l'oral
> "Spring Data JPA me permet de manipuler les données en base sans écrire de SQL. Je définis des entités Java (Concert, Photo, etc.) et des repositories qui fournissent automatiquement les méthodes CRUD (save, findById, delete). Les requêtes complexes sont générées automatiquement à partir des noms de méthodes."

---

## 4. VUE.JS 3

### 📖 Définition simple
Framework JavaScript progressif pour construire des interfaces utilisateur interactives avec un système de composants réactifs.

### 🎯 À savoir par cœur (10 points)

1. **Framework progressif** : S'adopte progressivement (widget → SPA)
2. **Réactivité** : L'interface se met à jour automatiquement quand les données changent
3. **Composants** : UI découpée en composants réutilisables (.vue files)
4. **SPA (Single Page Application)** : Navigation sans rechargement de page
5. **Virtual DOM** : Re-render optimisé (seulement ce qui change)
6. **Composition API** : Nouvelle façon d'organiser le code (script setup)
7. **Vue Router** : Gestion du routage côté client
8. **Directives** : v-if, v-for, v-bind, v-model, v-on
9. **Props et Events** : Communication parent ↔ enfant
10. **Lifecycle Hooks** : onMounted, onUpdated, onUnmounted

### 💡 Dans votre projet
- **Version** : Vue.js 3 avec Composition API
- **Utilisé pour** : Frontend public (pages concerts, photos, contact)
- **Bundler** : Vite (build ultra-rapide)
- **Styling** : Tailwind CSS

### 🗣️ Phrase à dire à l'oral
> "Vue.js 3 me permet de créer une interface utilisateur moderne et réactive. Quand je récupère des concerts depuis l'API, Vue met automatiquement à jour l'affichage sans que j'aie à manipuler le DOM manuellement. C'est une SPA, donc la navigation est fluide sans rechargement de page."

---

## 5. MARIADB

### 📖 Définition simple
Système de gestion de base de données relationnelle (SGBDR) open-source, fork de MySQL.

### 🎯 À savoir par cœur (10 points)

1. **SGBDR** : Base de données relationnelle (tables, lignes, colonnes)
2. **SQL** : Langage de requête (SELECT, INSERT, UPDATE, DELETE)
3. **Fork de MySQL** : Créé par le créateur original de MySQL (Michael Widenius)
4. **Open-source** : Vraiment libre (contrairement à MySQL propriété d'Oracle)
5. **ACID** : Atomicité, Cohérence, Isolation, Durabilité (transactions fiables)
6. **Clé primaire** : Identifiant unique d'une ligne (PRIMARY KEY)
7. **Clé étrangère** : Référence vers une autre table (FOREIGN KEY)
8. **Index** : Accélère les recherches sur une colonne
9. **Contraintes** : NOT NULL, UNIQUE, CHECK, etc.
10. **Transactions** : Groupe d'opérations (tout réussit ou tout échoue)

### 💡 Dans votre projet
- **Version** : MariaDB 10.11
- **Tables** : users, concerts, photos, tracks, messages, comments, etc.
- **Relations** : Clés étrangères entre les tables
- **Migrations** : Gérées avec Flyway

### 🗣️ Phrase à dire à l'oral
> "J'utilise MariaDB car c'est une base de données relationnelle mature et performante. Mes données sont structurées avec des relations (un utilisateur peut avoir plusieurs commentaires, un concert a plusieurs photos, etc.). MariaDB garantit la cohérence des données avec les contraintes et les transactions ACID."

---

## 6. DOCKER

### 📖 Définition simple
Plateforme de containerisation qui package une application avec toutes ses dépendances dans un conteneur portable et isolé.

### 🎯 À savoir par cœur (10 points)

1. **Conteneur** : Environnement isolé qui contient l'application + dépendances
2. **Image** : Template pour créer des conteneurs (blueprint)
3. **Dockerfile** : Script qui décrit comment construire une image
4. **Multi-stage build** : Build en plusieurs étapes pour optimiser la taille
5. **docker-compose** : Orchestration de plusieurs conteneurs
6. **Volume** : Stockage persistant (survit à l'arrêt du conteneur)
7. **Network** : Communication entre conteneurs
8. **Port mapping** : `-p 8080:8080` (hôte:conteneur)
9. **Isolation** : Chaque conteneur est isolé des autres
10. **Portabilité** : "Fonctionne sur ma machine" → fonctionne partout

### 💡 Dans votre projet
- **3 conteneurs** : musician-db (MariaDB), musician-app (Spring Boot + Vue.js), musician-adminer
- **docker-compose.yml** : Orchestration des 3 services
- **Volumes** : Persistance de la BDD et des uploads
- **Déploiement** : `docker-compose up -d` en une commande

### 🗣️ Phrase à dire à l'oral
> "Docker me permet de packager toute l'application (Spring Boot + Vue.js + MariaDB) dans des conteneurs. Ça garantit que l'application fonctionne de la même façon en développement, test et production. Le déploiement se fait en une seule commande : docker-compose up."

---

## 7. GITHUB ACTIONS (CI/CD)

### 📖 Définition simple
Service de CI/CD intégré à GitHub qui exécute automatiquement des workflows (tests, build, déploiement) lors d'événements (push, PR, etc.).

### 🎯 À savoir par cœur (10 points)

1. **CI (Continuous Integration)** : Tests automatiques à chaque push
2. **CD (Continuous Deployment)** : Déploiement automatique si tests OK
3. **Workflow** : Fichier YAML qui définit les étapes (.github/workflows/ci.yml)
4. **Trigger** : Événement qui déclenche le workflow (push, pull_request, etc.)
5. **Job** : Ensemble de steps qui s'exécutent (backend, frontend, docker)
6. **Runner** : Machine virtuelle qui exécute le workflow (ubuntu-latest)
7. **Actions** : Blocs réutilisables (actions/checkout, actions/setup-java)
8. **Matrix** : Tester sur plusieurs versions (Java 17, 21 / Node 18, 20)
9. **Artifacts** : Fichiers générés (JAR, rapport de tests, etc.)
10. **Secrets** : Variables sensibles (tokens, passwords) stockées de façon sécurisée

### 💡 Dans votre projet
- **Workflow** : `.github/workflows/ci.yml`
- **Déclenchement** : À chaque push sur main
- **Jobs** : Backend (Maven), Frontend (Vitest), E2E (Playwright), Docker (build + scan)
- **Résultat** : Badge dans README (vert si OK, rouge si KO)

### 🗣️ Phrase à dire à l'oral
> "J'ai mis en place un pipeline CI/CD avec GitHub Actions. À chaque push, ça lance automatiquement les 545 tests, build l'image Docker, et scan les vulnérabilités avec Trivy. Si tout est vert, l'image est prête à être déployée. Ça garantit qu'aucun bug ne passe en production."

---

## 8. REST API

### 📖 Définition simple
Style d'architecture pour les API web qui utilise HTTP et JSON, basé sur des ressources et des verbes standards.

### 🎯 À savoir par cœur (10 points)

1. **REST** : Representational State Transfer (style d'architecture)
2. **Ressource** : Entité accessible via une URL (/api/concerts, /api/photos)
3. **Verbes HTTP** : GET (lire), POST (créer), PUT (modifier), DELETE (supprimer)
4. **Stateless** : Chaque requête est indépendante (pas d'état côté serveur)
5. **JSON** : Format d'échange de données (JavaScript Object Notation)
6. **Status codes** : 200 OK, 201 Created, 400 Bad Request, 404 Not Found, 500 Error
7. **HATEOAS** : Hypermedia As The Engine Of Application State (liens dans les réponses)
8. **Content-Type** : application/json (header HTTP)
9. **Idempotence** : GET, PUT, DELETE sont idempotents (même résultat si répété)
10. **CRUD** : Create, Read, Update, Delete → POST, GET, PUT, DELETE

### 💡 Dans votre projet
- **Endpoints** :
  - GET /api/concerts/upcoming → Liste des concerts futurs
  - GET /api/photos → Liste des photos
  - POST /api/contact → Envoyer un message
  - GET /api/articles → Liste des articles
- **Format** : Tout en JSON
- **Client** : Frontend Vue.js (via Axios)

### 🗣️ Phrase à dire à l'oral
> "L'API REST permet au frontend Vue.js de communiquer avec le backend Spring Boot. Par exemple, quand l'utilisateur charge la page concerts, Vue fait un GET /api/concerts/upcoming, Spring renvoie du JSON, et Vue affiche les données. C'est stateless, donc scalable."

---

## 9. THYMELEAF

### 📖 Définition simple
Moteur de templates côté serveur pour générer des pages HTML dynamiques avec Java/Spring Boot.

### 🎯 À savoir par cœur (10 points)

1. **Server-Side Rendering (SSR)** : Le HTML est généré côté serveur
2. **Template** : Fichier HTML avec balises spéciales (th:text, th:if, etc.)
3. **Natural templates** : Les templates sont du HTML valide (prévisualisables dans le navigateur)
4. **Intégration Spring** : Fonctionne nativement avec Spring MVC
5. **Expression Language** : ${user.name}, *{email}, @{/admin/concerts}
6. **Conditional** : th:if, th:unless, th:switch
7. **Iteration** : th:each pour boucler sur des listes
8. **Forms** : Binding automatique avec @ModelAttribute
9. **Fragments** : Réutilisation de morceaux de templates (th:fragment, th:replace)
10. **Sécurité** : Échappe automatiquement le HTML (protection XSS)

### 💡 Dans votre projet
- **Utilisé pour** : Interface d'administration (/admin/*)
- **Templates** : src/main/resources/templates/admin/*.html
- **Fragments** : Header, sidebar, footer réutilisés
- **Forms** : Formulaires de création/édition avec validation

### 🗣️ Phrase à dire à l'oral
> "Pour l'interface admin, j'utilise Thymeleaf plutôt que Vue.js. C'est un moteur de templates côté serveur qui génère du HTML dynamique. Ça simplifie la gestion des formulaires sécurisés avec Spring Security (tokens CSRF auto-inclus). Le backend admin n'a pas besoin d'être aussi dynamique que le frontend public."

---

## 10. TESTS (JUNIT, MOCKITO)

### 📖 Définition simple
Frameworks pour écrire et exécuter des tests automatiques en Java.

### 🎯 À savoir par cœur (10 points)

1. **JUnit 5** : Framework de tests unitaires pour Java
2. **Test unitaire** : Teste une unité de code isolée (méthode, classe)
3. **Test d'intégration** : Teste plusieurs composants ensemble (@SpringBootTest)
4. **@Test** : Annotation qui marque une méthode de test
5. **Assertions** : assertThat(), assertEquals(), assertTrue(), etc.
6. **Mockito** : Bibliothèque pour créer des mocks (objets simulés)
7. **@Mock** : Crée un mock d'une dépendance
8. **@InjectMocks** : Injecte les mocks dans la classe testée
9. **when().thenReturn()** : Définit le comportement d'un mock
10. **MockMvc** : Teste les contrôleurs sans démarrer le serveur HTTP

### 💡 Dans votre projet
- **545 tests** : 534 unitaires + 11 intégration
- **Taux de réussite** : 99,8% (544/545)
- **Couverture** : JaCoCo mesure la couverture de code
- **CI/CD** : Tests exécutés automatiquement dans GitHub Actions

### 🗣️ Phrase à dire à l'oral
> "J'ai écrit 545 tests automatiques : tests unitaires avec Mockito pour isoler la logique, et tests d'intégration avec @SpringBootTest pour tester l'application complète. Le taux de réussite est de 99,8%. Ces tests sont exécutés automatiquement à chaque push via GitHub Actions, ce qui détecte les régressions immédiatement."

---

## 🎯 MÉTHODE DE RÉVISION

### Jour 1-2 : Lire toutes les fiches
- Lisez chaque fiche tranquillement
- Notez ce que vous ne comprenez pas
- Cherchez sur Google/documentation les points flous

### Jour 3-5 : Apprendre par cœur
- **Technique Flashcards** :
  - Recto : "Qu'est-ce que Spring Boot ?"
  - Verso : "Framework Java qui simplifie..."
- **Répétition espacée** :
  - Jour 3 : Apprendre
  - Jour 4 : Réviser
  - Jour 7 : Réviser à nouveau

### Jour 6-10 : Relier avec VOTRE projet
- Pour chaque techno, trouvez 2-3 exemples concrets dans VOTRE code
- Répétez à voix haute : "Dans mon projet, j'utilise X pour Y"

### Jour 11-14 : Simulation d'oral
- Demandez à quelqu'un de vous poser des questions
- Répondez avec les phrases types de ces fiches
- Chronométrez-vous (réponses de 1-2 minutes max)

---

## 💡 PHRASES MAGIQUES POUR L'ORAL

Si on vous demande **"Qu'est-ce que [technologie] ?"** :

**Structure de réponse :**
1. **Définition simple** (1 phrase)
2. **Pourquoi vous l'avez choisi** (1-2 raisons)
3. **Comment vous l'utilisez dans votre projet** (1 exemple concret)

**Exemple pour Spring Boot :**
> "Spring Boot est un framework Java qui simplifie le développement avec une configuration automatique et un serveur embarqué. Je l'ai choisi car c'est le standard en entreprise, avec une excellente documentation. Dans mon projet, je l'utilise pour développer l'API REST et gérer l'authentification avec Spring Security."

---

## 🚀 VOUS ALLEZ RÉUSSIR !

**Ces 10 fiches couvrent 90% des questions techniques de l'oral.**

Si vous maîtrisez ces concepts + votre projet, **vous êtes prêt pour la CDA !** 🏆
