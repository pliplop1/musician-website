# 🎯 ORAL CDA - QUESTIONS/RÉPONSES (40 minutes)

Ce document contient **60+ questions types** que le jury CDA peut vous poser, avec des **réponses courtes et précises** basées sur VOTRE projet.

---

## 📚 CATÉGORIES DE QUESTIONS

1. [Architecture et conception](#1-architecture-et-conception)
2. [Technologies et choix techniques](#2-technologies-et-choix-techniques)
3. [Sécurité](#3-sécurité)
4. [Base de données](#4-base-de-données)
5. [Tests](#5-tests)
6. [Déploiement et DevOps](#6-déploiement-et-devops)
7. [Frontend Vue.js](#7-frontend-vuejs)
8. [Backend Spring Boot](#8-backend-spring-boot)
9. [Gestion de projet](#9-gestion-de-projet)
10. [Questions pièges](#10-questions-pièges)

---

## 1. ARCHITECTURE ET CONCEPTION

### Q1.1 : Pouvez-vous nous expliquer l'architecture de votre application ?

**Réponse :**
> "J'ai conçu une architecture 3 tiers :
> - **Frontend** : Vue.js 3 en SPA pour l'interface utilisateur publique
> - **Backend** : Spring Boot avec API REST et interface admin Thymeleaf
> - **Base de données** : MariaDB pour la persistance
>
> Cette séparation permet une maintenance facilitée, une scalabilité et une sécurité renforcée car chaque couche a un rôle bien défini."

---

### Q1.2 : Pourquoi avez-vous choisi une architecture 3 tiers ?

**Réponse :**
> "L'architecture 3 tiers offre plusieurs avantages :
> - **Séparation des responsabilités** : chaque couche a un rôle précis
> - **Maintenance** : on peut modifier le frontend sans toucher au backend
> - **Scalabilité** : on peut ajouter des serveurs backend si nécessaire
> - **Sécurité** : le frontend n'accède jamais directement à la base de données
> - **Testabilité** : chaque couche peut être testée indépendamment"

---

### Q1.3 : Comment les différentes couches communiquent-elles entre elles ?

**Réponse :**
> "- **Frontend → Backend** : Appels HTTP via API REST (GET, POST, PUT, DELETE)
> - **Backend → Base de données** : Spring Data JPA avec JDBC
>
> Par exemple, quand l'utilisateur charge la page des concerts :
> 1. Vue.js fait un GET /api/concerts/upcoming
> 2. Spring Boot interroge la BDD via JPA
> 3. Les données sont renvoyées en JSON
> 4. Vue.js affiche les résultats"

---

### Q1.4 : Qu'est-ce qu'une API REST ?

**Réponse :**
> "REST (Representational State Transfer) est un style d'architecture pour les API web. Dans mon projet :
> - **GET /api/concerts** : récupérer la liste des concerts
> - **POST /api/contact** : envoyer un message
> - **PUT /admin/concerts/{id}** : modifier un concert
> - **DELETE /admin/photos/{id}** : supprimer une photo
>
> Les données sont échangées en JSON. C'est stateless (chaque requête est indépendante) et utilise les verbes HTTP standards."

---

### Q1.5 : Qu'est-ce qu'une SPA (Single Page Application) ?

**Réponse :**
> "Une SPA charge une seule page HTML au départ, puis met à jour le contenu dynamiquement sans recharger la page entière.
>
> **Avantages dans mon projet :**
> - Navigation plus fluide et rapide
> - Expérience utilisateur proche d'une application native
> - Moins de bande passante (on ne recharge que les données, pas toute la page)
>
> Vue.js gère le routage côté client avec vue-router."

---

## 2. TECHNOLOGIES ET CHOIX TECHNIQUES

### Q2.1 : Pourquoi avez-vous choisi Spring Boot ?

**Réponse :**
> "J'ai choisi Spring Boot pour plusieurs raisons :
> - **Maturité** : framework de référence en entreprise Java
> - **Productivité** : configuration automatique (auto-configuration)
> - **Écosystème** : Spring Security, Spring Data JPA intégrés
> - **Documentation** : excellente documentation et grande communauté
> - **Performance** : serveur Tomcat embarqué performant
> - **Standards** : respecte les bonnes pratiques Java EE"

---

### Q2.2 : Pourquoi Vue.js et pas React ou Angular ?

**Réponse :**
> "J'ai choisi Vue.js car :
> - **Courbe d'apprentissage douce** : plus simple à apprendre que React ou Angular
> - **Léger** : ~70 KB vs 40 KB pour React (avec ReactDOM) vs 500 KB pour Angular
> - **Composition API moderne** : organisation du code claire
> - **Documentation excellente** : documentation en français disponible
> - **Réactivité** : système de réactivité très performant
>
> Pour ce projet, Vue.js était le meilleur compromis entre puissance et simplicité."

---

### Q2.3 : Pourquoi MariaDB et pas PostgreSQL ou MySQL ?

**Réponse :**
> "MariaDB est un fork open-source de MySQL créé par le créateur original de MySQL. Je l'ai choisi car :
> - **Compatibilité** : compatible avec MySQL (facile à migrer)
> - **Open-source** : vraiment open-source (contrairement à MySQL qui appartient à Oracle)
> - **Performant** : optimisations pour les lectures/écritures
> - **Mature** : utilisé en production par Wikipédia, Google, etc.
> - **Gratuit** : pas de coûts de licence"

---

### Q2.4 : Qu'est-ce que Docker et pourquoi l'avez-vous utilisé ?

**Réponse :**
> "Docker est une plateforme de containerisation qui package une application avec toutes ses dépendances.
>
> **Avantages dans mon projet :**
> - **Reproductibilité** : 'fonctionne sur ma machine' → fonctionne partout
> - **Isolation** : chaque service (app, BDD) dans son propre conteneur
> - **Déploiement simple** : une commande `docker-compose up -d`
> - **Portabilité** : peut tourner sur n'importe quel serveur avec Docker
> - **Environnements multiples** : dev, test, prod identiques"

---

### Q2.5 : Qu'est-ce que Thymeleaf et pourquoi l'utilisez-vous ?

**Réponse :**
> "Thymeleaf est un moteur de templates pour Java. Je l'utilise pour l'interface d'administration car :
> - **Intégré à Spring Boot** : fonctionne nativement avec Spring MVC
> - **Server-side rendering** : les pages sont générées côté serveur
> - **Sécurité** : échappe automatiquement le HTML (protection XSS)
> - **Syntaxe claire** : `th:text`, `th:if`, etc.
>
> Pour le frontend public, j'utilise Vue.js (SPA), et pour le backend admin, Thymeleaf (SSR)."

---

## 3. SÉCURITÉ

### Q3.1 : Comment avez-vous sécurisé votre application ?

**Réponse :**
> "J'ai mis en place plusieurs couches de sécurité :
>
> **Authentification :**
> - Spring Security avec authentification par formulaire
> - Mots de passe hashés avec BCrypt (algorithme unidirectionnel)
> - Sessions sécurisées (cookies HTTP-only, SameSite)
>
> **Autorisation :**
> - Contrôle d'accès basé sur les rôles (ROLE_ADMIN, ROLE_USER)
> - Routes protégées (ex: /admin/* nécessite ROLE_ADMIN)
>
> **Protection contre les attaques :**
> - CSRF (Cross-Site Request Forgery) activé
> - Rate limiting sur le login (max 5 tentatives)
> - Validation des entrées (Bean Validation)
> - Protection XSS (Thymeleaf échappe le HTML)
>
> **Transport :**
> - HTTPS en production avec certificats TLS"

---

### Q3.2 : Qu'est-ce que le CSRF et comment vous en protégez-vous ?

**Réponse :**
> "CSRF (Cross-Site Request Forgery) est une attaque où un site malveillant force l'utilisateur à exécuter des actions non désirées sur mon site.
>
> **Protection dans mon projet :**
> Spring Security génère automatiquement un token CSRF unique par session. Ce token doit être inclus dans chaque formulaire POST/PUT/DELETE.
>
> **Exemple :** Dans mes formulaires Thymeleaf :
> ```html
> <input type=\"hidden\" th:name=\"${_csrf.parameterName}\" th:value=\"${_csrf.token}\" />
> ```
>
> Si le token est absent ou invalide, Spring rejette la requête avec une erreur 403."

---

### Q3.3 : Comment sont stockés les mots de passe ?

**Réponse :**
> "Les mots de passe sont **JAMAIS stockés en clair**. J'utilise BCrypt, un algorithme de hachage unidirectionnel :
>
> **Lors de l'inscription :**
> ```java
> String hashedPassword = passwordEncoder.encode(rawPassword);
> user.setPassword(hashedPassword);
> ```
>
> **Lors de la connexion :**
> Spring Security compare automatiquement le mot de passe saisi avec le hash stocké.
>
> **Avantages de BCrypt :**
> - Algorithme lent (protection contre brute-force)
> - Salt automatique (chaque hash est unique)
> - Impossible à déchiffrer (unidirectionnel)"

---

### Q3.4 : Qu'est-ce que le rate limiting et comment l'avez-vous implémenté ?

**Réponse :**
> "Le rate limiting limite le nombre de tentatives de connexion pour éviter les attaques par brute-force.
>
> **Implémentation :**
> J'ai créé un service `LoginAttemptService` qui :
> - Compte les tentatives de connexion échouées par adresse IP
> - Bloque l'IP pendant 15 minutes après 5 échecs
> - Se réinitialise après une connexion réussie
>
> Si un attaquant essaie 1000 mots de passe, il sera bloqué après 5 essais. Cela rend les attaques brute-force inefficaces."

---

### Q3.5 : Qu'est-ce que XSS et comment vous en protégez-vous ?

**Réponse :**
> "XSS (Cross-Site Scripting) est une attaque où un attaquant injecte du JavaScript malveillant dans une page web.
>
> **Protection dans mon projet :**
> - **Thymeleaf** : échappe automatiquement le HTML (remplace `<script>` par `&lt;script&gt;`)
> - **Vue.js** : échappe aussi automatiquement le HTML avec `{{ }}` (mustache)
> - **Validation** : Bean Validation côté serveur valide toutes les entrées
>
> Exemple : si un utilisateur tape `<script>alert('XSS')</script>` dans le formulaire de contact, c'est échappé et affiché comme du texte, pas exécuté."

---

## 4. BASE DE DONNÉES

### Q4.1 : Pouvez-vous expliquer votre modèle de données ?

**Réponse :**
> "Mon modèle de données est organisé autour de plusieurs entités principales :
>
> **Entités principales :**
> - **User** : utilisateurs du site (admin, membres)
> - **Concert** : événements avec date, lieu, description
> - **Photo** : images avec catégorie, ordre d'affichage
> - **Track** : pistes musicales avec titre, artiste, fichier
> - **Message** : messages de contact reçus
> - **Comment** : commentaires sur concerts/photos/tracks
> - **Article** : articles de blog
> - **SocialLink** : liens réseaux sociaux
>
> **Relations :**
> - **User ↔ Concert** : ManyToMany (favoris)
> - **User → Comment** : OneToMany
> - **User → Message** : OneToMany (auteur)"

---

### Q4.2 : Qu'est-ce que JPA et à quoi ça sert ?

**Réponse :**
> "JPA (Java Persistence API) est une spécification Java pour gérer la persistance des objets en base de données (ORM).
>
> **Dans mon projet avec Spring Data JPA :**
> - Je manipule des objets Java (entités)
> - JPA traduit automatiquement en SQL
> - Je n'écris pas de requêtes SQL manuellement (la plupart du temps)
>
> **Exemple :**
> ```java
> @Entity
> public class Concert {
>     @Id
>     @GeneratedValue
>     private Long id;
>     private String title;
>     private LocalDate date;
> }
> ```
>
> JPA crée automatiquement la table `concert` avec ces colonnes."

---

### Q4.3 : Qu'est-ce qu'une clé primaire et une clé étrangère ?

**Réponse :**
> "**Clé primaire (Primary Key) :**
> Identifiant unique d'un enregistrement. Dans mon projet, toutes les entités ont un `@Id Long id` auto-généré.
>
> **Clé étrangère (Foreign Key) :**
> Référence vers la clé primaire d'une autre table pour créer des relations.
>
> **Exemple dans Comment :**
> ```java
> @ManyToOne
> @JoinColumn(name = \"user_id\")
> private User user; // user_id est la clé étrangère
> ```
>
> Cela crée une colonne `user_id` dans la table `comments` qui référence la table `users`."

---

### Q4.4 : Qu'est-ce qu'une migration de base de données ?

**Réponse :**
> "Une migration est un script qui modifie le schéma de la base de données de manière contrôlée et versionnée.
>
> **Dans mon projet avec Flyway :**
> - Les migrations sont dans `src/main/resources/db/migration/`
> - Nommées `V1__Initial_schema.sql`, `V2__Add_comments.sql`, etc.
> - Flyway exécute automatiquement les migrations manquantes au démarrage
>
> **Avantages :**
> - Historique des changements de schéma
> - Reproductible (même schéma en dev, test, prod)
> - Rollback possible si problème"

---

### Q4.5 : Pourquoi utilisez-vous H2 pour les tests ?

**Réponse :**
> "H2 est une base de données en mémoire très rapide, idéale pour les tests car :
> - **Rapide** : tout en RAM, pas d'I/O disque
> - **Isolée** : chaque test repart d'une BDD vierge
> - **Compatible** : dialecte SQL proche de MariaDB
> - **Légère** : pas besoin d'installer un serveur BDD
>
> En production j'utilise MariaDB (persistance réelle), en test H2 (rapidité et isolation)."

---

## 5. TESTS

### Q5.1 : Quels types de tests avez-vous écrits ?

**Réponse :**
> "J'ai développé trois types de tests :
>
> **1. Tests unitaires (534 tests) :**
> - Tests des services avec Mockito (mock des dépendances)
> - Tests des contrôleurs avec MockMvc (sans serveur HTTP)
> - Tests des repositories avec H2
>
> **2. Tests d'intégration (11 tests) :**
> - Tests du contexte Spring complet (@SpringBootTest)
> - Tests de la persistance avec base H2 réelle
> - Validation des transactions
>
> **3. Tests E2E (frontend) :**
> - Tests Playwright qui simulent un utilisateur réel
> - Testent le parcours complet (frontend + backend)
>
> **Total : 545 tests, 544 réussis = 99,8% de succès**"

---

### Q5.2 : Qu'est-ce que Mockito et à quoi ça sert ?

**Réponse :**
> "Mockito est une bibliothèque pour créer des mocks (objets simulés) dans les tests.
>
> **Exemple dans un test de service :**
> ```java
> @Mock
> private UserRepository userRepository;
>
> @InjectMocks
> private UserService userService;
>
> @Test
> void testFindByEmail() {
>     // On simule le comportement du repository
>     when(userRepository.findByEmail(\"test@example.com\"))
>         .thenReturn(mockUser);
>
>     // On teste le service
>     User result = userService.findByEmail(\"test@example.com\");
>
>     assertThat(result).isEqualTo(mockUser);
> }
> ```
>
> Ça permet de tester le service **sans avoir besoin d'une vraie base de données**."

---

### Q5.3 : Qu'est-ce que la couverture de code (code coverage) ?

**Réponse :**
> "La couverture de code mesure le pourcentage de code source exécuté par les tests.
>
> **Dans mon projet avec JaCoCo :**
> - JaCoCo instrument le bytecode pour tracer les lignes exécutées
> - Génère un rapport HTML avec les statistiques
> - Intégré au pipeline CI/CD (rapport automatique à chaque push)
>
> **Métriques :**
> - **Couverture des lignes** : % de lignes exécutées
> - **Couverture des branches** : % de conditions testées (if/else)
> - **Couverture des méthodes** : % de méthodes appelées
>
> Mon projet a une bonne couverture car 99,8% des tests passent."

---

### Q5.4 : Pourquoi les tests sont-ils importants ?

**Réponse :**
> "Les tests sont essentiels pour plusieurs raisons :
>
> **1. Détection précoce des bugs :**
> Les tests automatiques détectent les régressions avant la production.
>
> **2. Documentation vivante :**
> Les tests montrent comment utiliser le code (exemples concrets).
>
> **3. Refactoring en confiance :**
> On peut modifier le code en sachant que les tests vérifieront qu'on n'a rien cassé.
>
> **4. Qualité du code :**
> Écrire des tests force à écrire du code testable (donc mieux conçu).
>
> **5. Certification CDA :**
> Les tests d'intégration sont une compétence obligatoire (CP08)."

---

### Q5.5 : Qu'est-ce qu'un test d'intégration ?

**Réponse :**
> "Un test d'intégration teste plusieurs composants ensemble, contrairement au test unitaire qui teste un seul composant isolé.
>
> **Exemple dans mon projet :**
> ```java
> @SpringBootTest // Charge TOUT le contexte Spring
> @Transactional // Rollback automatique après chaque test
> class DatabaseIntegrationTest {
>     @Autowired
>     private ConcertRepository concertRepository;
>
>     @Test
>     void testCRUD() {
>         // Teste la vraie persistance avec H2
>         Concert concert = new Concert();
>         concertRepository.save(concert);
>
>         assertThat(concertRepository.findAll()).hasSize(1);
>     }
> }
> ```
>
> Cela teste que Spring Data JPA, Hibernate, H2 fonctionnent bien ensemble."

---

## 6. DÉPLOIEMENT ET DEVOPS

### Q6.1 : Qu'est-ce que le CI/CD ?

**Réponse :**
> "CI/CD signifie Continuous Integration / Continuous Deployment.
>
> **CI (Intégration Continue) :**
> À chaque push de code, des tests automatiques s'exécutent pour vérifier qu'on n'a rien cassé.
>
> **CD (Déploiement Continu) :**
> Si les tests passent, le code est automatiquement déployé.
>
> **Dans mon projet avec GitHub Actions :**
> 1. Je push du code sur GitHub
> 2. GitHub Actions lance automatiquement :
>    - Build Maven
>    - Tests (545 tests)
>    - Build Docker
>    - Scan de sécurité Trivy
> 3. Si tout est vert, l'image Docker est prête à déployer
>
> Cela garantit qu'aucun bug ne passe en production."

---

### Q6.2 : Qu'est-ce que GitHub Actions ?

**Réponse :**
> "GitHub Actions est un service de CI/CD intégré à GitHub. Il exécute des workflows (scripts automatisés) lors d'événements (push, pull request, etc.).
>
> **Mon workflow (`.github/workflows/ci.yml`) :**
> - **Triggers** : à chaque push sur main
> - **Jobs** :
>   1. Backend : tests Maven + JaCoCo
>   2. Frontend : tests Vitest + ESLint + npm audit
>   3. E2E : tests Playwright
>   4. Lighthouse : audit performance/SEO
>   5. Docker : build + push + scan Trivy
>
> **Avantages :**
> - Gratuit pour projets open-source
> - Intégré à GitHub (pas besoin de service externe)
> - Matrice de tests (plusieurs versions de Node, Java, etc.)"

---

### Q6.3 : Comment déployez-vous votre application ?

**Réponse :**
> "Le déploiement se fait en plusieurs étapes :
>
> **1. Build de l'image Docker :**
> ```bash
> docker build -t musician-website .
> ```
>
> **2. Démarrage avec docker-compose :**
> ```bash
> docker-compose up -d
> ```
>
> Cela lance 3 conteneurs :
> - `musician-db` : MariaDB
> - `musician-app` : Spring Boot + Vue.js
> - `musician-adminer` : Interface BDD
>
> **3. Configuration via variables d'environnement :**
> Le fichier `.env` contient les secrets (DB password, SMTP, etc.)
>
> **Avantages :**
> - Déploiement en une commande
> - Reproductible (même environnement partout)
> - Facile à déployer sur n'importe quel serveur avec Docker"

---

### Q6.4 : Qu'est-ce qu'un Dockerfile ?

**Réponse :**
> "Un Dockerfile est un script qui décrit comment construire une image Docker.
>
> **Mon Dockerfile utilise un build multi-stage :**
>
> **Stage 1 - Build :**
> ```dockerfile
> FROM maven:3.9-openjdk-21 AS build
> COPY . .
> RUN mvn clean package -DskipTests
> ```
>
> **Stage 2 - Runtime :**
> ```dockerfile
> FROM openjdk:21-jdk-slim
> COPY --from=build target/*.jar app.jar
> ENTRYPOINT [\"java\",\"-jar\",\"/app.jar\"]
> ```
>
> **Avantages du multi-stage :**
> - Image finale légère (pas de Maven, seulement le JAR)
> - Build reproductible
> - Cache des layers optimisé"

---

### Q6.5 : Qu'est-ce que Trivy et pourquoi faites-vous un scan de sécurité ?

**Réponse :**
> "Trivy est un scanner de vulnérabilités pour images Docker. Il détecte :
> - Vulnérabilités dans les dépendances (CVE)
> - Secrets hardcodés (mots de passe, clés API)
> - Mauvaises configurations
>
> **Dans mon pipeline CI/CD :**
> ```yaml
> - name: Security scan Docker image
>   uses: aquasecurity/trivy-action@master
>   with:
>     image-ref: musician-website:latest
>     severity: 'CRITICAL,HIGH'
> ```
>
> Cela permet de détecter les failles de sécurité **avant** de déployer en production. C'est une bonne pratique DevSecOps."

---

## 7. FRONTEND VUE.JS

### Q7.1 : Qu'est-ce que Vue.js ?

**Réponse :**
> "Vue.js est un framework JavaScript progressif pour construire des interfaces utilisateur.
>
> **Caractéristiques dans mon projet :**
> - **Réactif** : l'interface se met à jour automatiquement quand les données changent
> - **Composants** : l'UI est découpée en composants réutilisables
> - **SPA** : navigation sans rechargement de page
> - **Composition API** : organisation moderne du code
>
> **Exemple de composant :**
> ```vue
> <template>
>   <div>{{ message }}</div>
> </template>
>
> <script setup>
> import { ref } from 'vue'
> const message = ref('Hello CDA!')
> </script>
> ```"

---

### Q7.2 : Qu'est-ce que la réactivité dans Vue.js ?

**Réponse :**
> "La réactivité signifie que l'interface se met à jour automatiquement quand les données changent.
>
> **Exemple :**
> ```javascript
> const concerts = ref([])
>
> // Récupération des concerts
> axios.get('/api/concerts/upcoming')
>   .then(response => {
>     concerts.value = response.data
>     // L'affichage se met à jour automatiquement !
>   })
> ```
>
> Vue.js suit les changements de `concerts` et re-rend seulement les parties de l'interface qui ont changé (Virtual DOM). C'est très performant."

---

### Q7.3 : Qu'est-ce que Tailwind CSS ?

**Réponse :**
> "Tailwind CSS est un framework CSS utility-first. Au lieu d'écrire du CSS personnalisé, on utilise des classes utilitaires.
>
> **Exemple :**
> ```html
> <div class=\"bg-blue-500 text-white p-4 rounded-lg shadow-md\">
>   Hello
> </div>
> ```
>
> **Avantages :**
> - **Rapide** : pas besoin d'écrire de CSS custom
> - **Cohérent** : design system intégré (couleurs, espacements)
> - **Responsive** : `md:flex`, `lg:grid` pour les breakpoints
> - **Optimisé** : PurgeCSS supprime les classes non utilisées
>
> Mon projet utilise Tailwind pour le design responsive et moderne."

---

### Q7.4 : Comment gérez-vous le routage côté client ?

**Réponse :**
> "J'utilise Vue Router pour gérer la navigation sans rechargement de page.
>
> **Configuration des routes :**
> ```javascript
> const routes = [
>   { path: '/', component: Home },
>   { path: '/concerts', component: Concerts },
>   { path: '/photos', component: Photos },
>   { path: '/contact', component: Contact }
> ]
> ```
>
> Quand l'utilisateur clique sur un lien, Vue Router :
> 1. Change l'URL (sans recharger)
> 2. Affiche le bon composant
> 3. Garde l'état de l'application
>
> C'est plus fluide qu'un site multi-pages classique."

---

### Q7.5 : Comment communiquez-vous avec le backend depuis Vue.js ?

**Réponse :**
> "J'utilise Axios pour faire des requêtes HTTP vers l'API REST Spring Boot.
>
> **Exemple :**
> ```javascript
> import axios from 'axios'
>
> // GET : récupérer des concerts
> const { data } = await axios.get('/api/concerts/upcoming')
> concerts.value = data
>
> // POST : envoyer un message
> await axios.post('/api/contact', {
>   name: 'John',
>   email: 'john@example.com',
>   message: 'Hello'
> })
> ```
>
> Axios gère automatiquement :
> - Conversion en JSON
> - Headers (Content-Type)
> - Gestion des erreurs
> - Interceptors (ajout automatique de tokens, etc.)"

---

## 8. BACKEND SPRING BOOT

### Q8.1 : Qu'est-ce que Spring Boot ?

**Réponse :**
> "Spring Boot est un framework Java qui simplifie le développement d'applications Spring.
>
> **Avantages :**
> - **Auto-configuration** : détecte automatiquement les dépendances et se configure
> - **Serveur embarqué** : Tomcat intégré, pas besoin de serveur externe
> - **Starters** : dépendances prépackagées (`spring-boot-starter-web`, etc.)
> - **Production-ready** : Actuator pour monitoring, métriques, health checks
> - **Convention over configuration** : fonctionne out-of-the-box
>
> Dans mon projet, Spring Boot gère l'API REST, la sécurité, JPA, les emails, etc."

---

### Q8.2 : Qu'est-ce qu'un contrôleur REST ?

**Réponse :**
> "Un contrôleur REST expose des endpoints HTTP pour communiquer avec le frontend.
>
> **Exemple :**
> ```java
> @RestController
> @RequestMapping(\"/api/concerts\")
> public class PublicApiController {
>
>     @GetMapping(\"/upcoming\")
>     public List<Concert> getUpcomingConcerts() {
>         return concertService.findUpcomingConcerts();
>     }
>
>     @PostMapping
>     public Concert createConcert(@RequestBody Concert concert) {
>         return concertService.save(concert);
>     }
> }
> ```
>
> Spring Boot convertit automatiquement les objets Java en JSON grâce à Jackson."

---

### Q8.3 : Qu'est-ce qu'une couche service ?

**Réponse :**
> "La couche service contient la logique métier de l'application. Elle se situe entre les contrôleurs (qui gèrent les requêtes HTTP) et les repositories (qui gèrent la persistance).
>
> **Exemple :**
> ```java
> @Service
> @Transactional
> public class ConcertService {
>
>     private final ConcertRepository repository;
>
>     public List<Concert> findUpcomingConcerts() {
>         return repository.findByDateAfter(LocalDate.now());
>     }
>
>     public Concert save(Concert concert) {
>         // Validation métier
>         if (concert.getDate().isBefore(LocalDate.now())) {
>             throw new IllegalArgumentException(\"Date invalide\");
>         }
>         return repository.save(concert);
>     }
> }
> ```
>
> **Avantages :**
> - Logique réutilisable (plusieurs contrôleurs peuvent appeler le même service)
> - Testable (on peut mocker le repository)
> - Transactions (@Transactional)"

---

### Q8.4 : Qu'est-ce que @Transactional ?

**Réponse :**
> "@Transactional indique que la méthode doit s'exécuter dans une transaction de base de données.
>
> **Comportement :**
> - Si la méthode réussit → COMMIT (changements sauvegardés)
> - Si une exception est levée → ROLLBACK (changements annulés)
>
> **Exemple :**
> ```java
> @Transactional
> public void updateConcert(Long id, Concert newData) {
>     Concert concert = repository.findById(id);
>     concert.setTitle(newData.getTitle());
>     repository.save(concert);
>
>     // Si une exception est levée ici,
>     // le changement de titre est annulé
> }
> ```
>
> Cela garantit la cohérence des données (principe ACID)."

---

### Q8.5 : Comment gérez-vous les erreurs dans l'API ?

**Réponse :**
> "J'utilise un @ControllerAdvice pour gérer les erreurs globalement et renvoyer des réponses JSON cohérentes.
>
> **Exemple :**
> ```java
> @RestControllerAdvice
> public class GlobalExceptionHandler {
>
>     @ExceptionHandler(ResourceNotFoundException.class)
>     public ResponseEntity<ErrorResponse> handleNotFound(
>         ResourceNotFoundException ex
>     ) {
>         ErrorResponse error = new ErrorResponse(
>             HttpStatus.NOT_FOUND.value(),
>             ex.getMessage()
>         );
>         return ResponseEntity.status(404).body(error);
>     }
>
>     @ExceptionHandler(MethodArgumentNotValidException.class)
>     public ResponseEntity<ErrorResponse> handleValidation(
>         MethodArgumentNotValidException ex
>     ) {
>         // Renvoie les erreurs de validation
>         return ResponseEntity.badRequest().body(errors);
>     }
> }
> ```
>
> Cela évite de dupliquer la gestion d'erreurs dans chaque contrôleur."

---

## 9. GESTION DE PROJET

### Q9.1 : Comment avez-vous organisé votre projet ?

**Réponse :**
> "J'ai suivi une méthodologie agile avec des itérations courtes :
>
> **Phase 1 - Conception (1 semaine) :**
> - Analyse des besoins du client
> - Maquettes Figma
> - Modèle de données
>
> **Phase 2 - MVP (3 semaines) :**
> - Authentification
> - CRUD concerts/photos
> - Interface admin basique
>
> **Phase 3 - Fonctionnalités avancées (3 semaines) :**
> - API REST pour Vue.js
> - Frontend SPA complet
> - Emails, commentaires
>
> **Phase 4 - Tests et déploiement (2 semaines) :**
> - Tests unitaires et d'intégration
> - CI/CD GitHub Actions
> - Déploiement Docker
>
> J'ai utilisé Git pour versionner le code et GitHub Projects pour suivre les tâches."

---

### Q9.2 : Quelles difficultés avez-vous rencontrées ?

**Réponse :**
> "**Difficulté 1 : Configuration SMTP**
> Microsoft a désactivé l'authentification basique SMTP pendant le projet. J'ai dû migrer vers Gmail avec des mots de passe d'application.
>
> **Difficulté 2 : Upload de fichiers volumineux**
> Les uploads de vidéos échouaient. J'ai dû augmenter `spring.servlet.multipart.max-file-size=20MB`.
>
> **Difficulté 3 : Performance frontend**
> Le score Lighthouse initial était de 70/100. J'ai optimisé avec lazy loading, compression d'images et Font Awesome CDN. Résultat : 95/100.
>
> **Difficulté 4 : Docker en production**
> Les variables d'environnement dans `.env` n'étaient pas chargées. J'ai dû utiliser `docker-compose down && docker-compose up -d` au lieu de `restart`."

---

### Q9.3 : Si vous deviez refaire le projet, que changeriez-vous ?

**Réponse :**
> "**Ce que je ferais différemment :**
>
> **1. Tests dès le début :**
> J'ai écrit les tests à la fin. La prochaine fois, je ferais du TDD (Test-Driven Development).
>
> **2. Documentation continue :**
> J'aurais documenté au fur et à mesure plutôt que tout à la fin.
>
> **3. CI/CD plus tôt :**
> J'aurais configuré GitHub Actions dès le début pour détecter les bugs plus tôt.
>
> **4. Revue de code :**
> J'aurais demandé des code reviews régulières (même si c'est un projet solo).
>
> **Ce que je garderais :**
> - Architecture 3 tiers (très maintenable)
> - Spring Boot + Vue.js (excellent combo)
> - Docker (déploiement simplifié)"

---

### Q9.4 : Comment avez-vous géré les versions de votre code ?

**Réponse :**
> "J'utilise Git avec une stratégie de branches :
>
> **Branches principales :**
> - `main` : code stable en production
> - `develop` : intégration des fonctionnalités
>
> **Branches de feature :**
> - `feature/authentication` : système de login
> - `feature/concerts-crud` : gestion des concerts
> - `feature/email-notifications` : envoi d'emails
>
> **Workflow :**
> 1. Je crée une branche feature depuis develop
> 2. Je développe la fonctionnalité
> 3. Je merge dans develop
> 4. Quand develop est stable, je merge dans main
>
> **Commits :**
> J'utilise des messages de commit conventionnels :
> - `feat: add concert creation form`
> - `fix: email SMTP configuration`
> - `test: add integration tests`"

---

### Q9.5 : Comment avez-vous communiqué avec le client ?

**Réponse :**
> "J'ai maintenu une communication régulière avec le Duo Black & White :
>
> **Réunions hebdomadaires :**
> - Démonstration des nouvelles fonctionnalités
> - Recueil du feedback
> - Ajustements en fonction de leurs besoins
>
> **Environnement de staging :**
> J'ai déployé une version de test pour qu'ils puissent tester avant la mise en production.
>
> **Documentation utilisateur :**
> J'ai créé un guide pour l'interface d'administration (comment ajouter un concert, uploader des photos, etc.).
>
> **Support :**
> Je reste disponible pour les questions et problèmes après la livraison."

---

## 10. QUESTIONS PIÈGES

### Q10.1 : Pourquoi n'avez-vous pas utilisé [autre techno] ?

**Stratégie de réponse :**
> "C'est une bonne question. [Autre techno] aurait aussi pu convenir, mais j'ai choisi [ma techno] car :
> - [Raison 1 en lien avec le projet]
> - [Raison 2 en lien avec mes compétences]
> - [Raison 3 en lien avec le contexte]
>
> Cela dit, dans un autre contexte, [autre techno] pourrait être plus adapté si [condition]."

**Exemple concret :**
> "Pourquoi pas MongoDB au lieu de MariaDB ?"
>
> "MongoDB aurait aussi pu convenir, mais j'ai choisi MariaDB car :
> - Mes données sont relationnelles (concerts ↔ utilisateurs, commentaires ↔ photos)
> - Je voulais utiliser SQL (plus d'offres d'emploi en SQL qu'en NoSQL)
> - MariaDB est plus mature et stable pour un site de production
>
> MongoDB serait plus adapté pour des données non structurées ou du big data."

---

### Q10.2 : Ce projet n'est-il pas trop simple pour un niveau CDA ?

**Réponse :**
> "Au premier abord, le concept peut paraître simple (site vitrine), mais la complexité technique est là :
>
> **Architecture :**
> - Application 3-tier complète
> - API REST documentée
> - Frontend SPA moderne
>
> **Sécurité :**
> - Spring Security complet (authentification, autorisation, CSRF, rate limiting)
> - Hashage BCrypt
> - Protection XSS
>
> **Tests :**
> - 545 tests automatisés (99,8% succès)
> - Tests unitaires, d'intégration, E2E
> - Couverture de code JaCoCo
>
> **DevOps :**
> - Pipeline CI/CD complet
> - Containerisation Docker
> - Scan de sécurité Trivy
>
> Le projet couvre **toutes les compétences CDA** (11 compétences validées). La simplicité du concept permet de se concentrer sur la qualité technique."

---

### Q10.3 : Avez-vous tout fait tout seul ou avez-vous été aidé ?

**Réponse honnête :**
> "J'ai développé l'ensemble du code moi-même, de la conception à la mise en production.
>
> **Ressources utilisées :**
> - Documentation officielle (Spring, Vue.js, etc.)
> - StackOverflow pour des problèmes spécifiques
> - Tutoriels vidéo pour approfondir certains concepts
> - Aide ponctuelle d'un assistant IA pour :
>   - Débogage de problèmes complexes
>   - Optimisation de performance
>   - Révision de code
>
> C'est une pratique courante en entreprise (pair programming, code review, consultation d'experts). L'important est que **je comprends tout le code que j'ai écrit** et que je peux l'expliquer."

---

### Q10.4 : Que faites-vous si votre application plante en production ?

**Réponse :**
> "J'ai mis en place plusieurs mécanismes pour gérer les incidents :
>
> **1. Monitoring :**
> - Spring Boot Actuator expose des métriques (/actuator/health)
> - Logs détaillés avec niveaux (ERROR, WARN, INFO)
>
> **2. Gestion des erreurs :**
> - @ControllerAdvice capture toutes les exceptions
> - Messages d'erreur clairs pour l'utilisateur
> - Logs techniques pour le debug
>
> **3. Rollback :**
> - Docker permet de revenir à la version précédente rapidement :
>   ```bash
>   docker-compose down
>   docker pull musician-website:previous-tag
>   docker-compose up -d
>   ```
>
> **4. Process d'incident :**
> - Identifier la cause (logs)
> - Corriger le bug
> - Tester la correction
> - Déployer via CI/CD
> - Post-mortem pour éviter la récidive"

---

### Q10.5 : Votre code est-il vraiment sécurisé ?

**Réponse :**
> "J'ai appliqué les bonnes pratiques de sécurité reconnues :
>
> **OWASP Top 10 (principales vulnérabilités web) :**
> - ✅ Injection SQL : protégé par JPA (requêtes paramétrées)
> - ✅ Authentification cassée : Spring Security + BCrypt + sessions sécurisées
> - ✅ Exposition de données : endpoints protégés par rôles
> - ✅ XXE : désactivé dans les parsers XML
> - ✅ Contrôle d'accès : @PreAuthorize sur les méthodes sensibles
> - ✅ Mauvaise configuration : scan Trivy dans le CI/CD
> - ✅ XSS : échappement automatique Thymeleaf/Vue.js
> - ✅ Désérialisation : validation des entrées
> - ✅ Composants vulnérables : npm audit + dependabot
> - ✅ Logging insuffisant : logs détaillés avec SLF4J
>
> **Mais :**
> Aucune application n'est 100% sécurisée. Je continue à surveiller les CVE et à mettre à jour les dépendances régulièrement."

---

## 🎯 CONSEILS POUR RÉPONDRE AUX QUESTIONS

### ✅ DOs (À faire) :

1. **Respirer** : Prenez 2 secondes avant de répondre
2. **Reformuler** : "Si je comprends bien, vous me demandez..."
3. **Structurer** : "Il y a 3 points importants : premièrement..."
4. **Exemples concrets** : Donnez toujours un exemple de VOTRE projet
5. **Honnêteté** : Si vous ne savez pas, dites "Je n'ai pas approfondi ce point, mais voici comment j'aborderais le problème..."

### ❌ DON'Ts (À éviter) :

1. **Ne dites jamais "Je ne sais pas" sans réfléchir**
2. **Ne critiquez pas vos choix** (pas de "Oui j'aurais dû faire autrement")
3. **Ne mentez pas** (ils vont creuser et vous coincer)
4. **Ne paniquez pas** si vous ne savez pas (c'est normal !)
5. **Ne vous perdez pas dans les détails** (réponses courtes = 1-2 minutes max)

### 💡 Phrases magiques :

**Si la question est floue :**
> "Pouvez-vous préciser votre question ? Vous parlez de [aspect A] ou [aspect B] ?"

**Si vous ne connaissez pas :**
> "Je n'ai pas eu l'occasion d'approfondir [technologie X] dans ce projet, mais je connais le principe : [explication générale]. Dans mon cas, j'ai utilisé [votre solution] qui résout le même problème."

**Si la question est trop technique :**
> "C'est une question très technique. Voici comment ça fonctionne dans les grandes lignes : [explication simple]. Si vous voulez plus de détails, je peux creuser [aspect spécifique]."

**Si vous n'êtes pas sûr :**
> "D'après ce que j'ai compris, [votre réponse]. Mais je serais curieux d'approfondir ce point si vous avez des ressources à recommander."

---

## 📚 MÉMO FINAL

**Préparez-vous à :**
- 10-15 questions techniques détaillées
- 5-10 questions sur vos choix (pourquoi X et pas Y)
- 3-5 questions sur les difficultés rencontrées
- 2-3 questions pièges ("et si je vous dis que...")
- 5-10 minutes de discussion informelle (votre parcours, vos projets futurs)

**Le jury veut voir :**
- ✅ Que vous **comprenez** ce que vous avez fait
- ✅ Que vous savez **justifier** vos choix
- ✅ Que vous avez une **démarche professionnelle**
- ✅ Que vous êtes **capable d'apprendre** et de vous améliorer
- ❌ **PAS** que vous êtes un expert de tout !

**Vous allez réussir ! 🚀**
