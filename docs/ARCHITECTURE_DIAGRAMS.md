# 📐 SCHÉMAS ARCHITECTURE - À DESSINER PENDANT L'ORAL

## 🎯 OBJECTIF

Ces schémas sont à **dessiner au tableau ou sur papier** pendant votre présentation orale pour illustrer visuellement l'architecture de votre application.

**Conseils :**
- Dessinez lentement et proprement
- Commentez pendant que vous dessinez
- Utilisez des couleurs si possible (3 couleurs max)
- Gardez les schémas simples et lisibles

---

## 1️⃣ ARCHITECTURE 3 TIERS (À dessiner en 2 minutes)

### **Ce que vous dites pendant que vous dessinez :**

> "Je vais vous dessiner l'architecture globale de mon application. Elle est basée sur une architecture 3 tiers classique."

### **Schéma à reproduire :**

```
┌─────────────────────────────────────┐
│         NAVIGATEUR WEB              │
│                                     │
│  ┌──────────┐      ┌──────────┐   │
│  │ Vue.js 3 │      │Thymeleaf │   │
│  │   SPA    │      │  Admin   │   │
│  └────┬─────┘      └────┬─────┘   │
│       │                 │          │
└───────┼─────────────────┼──────────┘
        │                 │
        │ HTTPS           │ HTTPS
        │ JSON            │ HTML
        │                 │
┌───────▼─────────────────▼──────────┐
│      SPRING BOOT 3.2.4             │
│                                     │
│  ┌──────────────────────────────┐  │
│  │    Spring Security           │  │
│  │  (Authentification + CSRF)   │  │
│  └──────────────────────────────┘  │
│                                     │
│  ┌─────────┐     ┌──────────────┐  │
│  │REST API │     │ Controllers  │  │
│  └────┬────┘     └──────┬───────┘  │
│       │                 │          │
│  ┌────▼─────────────────▼───────┐  │
│  │      Services (Métier)       │  │
│  └────┬─────────────────────────┘  │
│       │                            │
│  ┌────▼─────────────────────────┐  │
│  │   Repositories (DAO)         │  │
│  │   Spring Data JPA            │  │
│  └────┬─────────────────────────┘  │
│       │                            │
└───────┼────────────────────────────┘
        │
        │ JDBC
        │
┌───────▼────────────────────────────┐
│         MariaDB 10.11              │
│                                     │
│  ┌─────────────────────────────┐   │
│  │  Tables :                   │   │
│  │  - users                    │   │
│  │  - concerts                 │   │
│  │  - photos                   │   │
│  │  - tracks                   │   │
│  │  - contact_messages         │   │
│  └─────────────────────────────┘   │
└─────────────────────────────────────┘
```

### **Points clés à expliquer :**

1. **Tier 1 - Frontend :**
   > "Le client utilise Vue.js 3 pour l'interface publique et Thymeleaf pour l'interface admin. Les deux communiquent avec le backend via HTTPS."

2. **Tier 2 - Backend :**
   > "Spring Boot gère toute la logique métier. Spring Security protège l'accès. Les contrôleurs REST répondent en JSON, les contrôleurs Thymeleaf en HTML."

3. **Tier 3 - Base de données :**
   > "MariaDB stocke toutes les données de manière persistante. Spring Data JPA gère la communication via JDBC."

### **Variante simplifiée (si peu de temps) :**

```
┌─────────────┐
│  Vue.js 3   │  (Frontend)
└──────┬──────┘
       │ HTTPS/JSON
┌──────▼──────┐
│ Spring Boot │  (Backend)
└──────┬──────┘
       │ JDBC
┌──────▼──────┐
│   MariaDB   │  (Base de données)
└─────────────┘
```

---

## 2️⃣ SCHÉMA DE BASE DE DONNÉES (À dessiner en 3 minutes)

### **Ce que vous dites :**

> "Voici le schéma relationnel de ma base de données. J'ai modélisé 5 entités principales avec leurs relations."

### **Schéma Entity-Relationship :**

```
┌─────────────────────┐
│       users         │
├─────────────────────┤
│ id (PK)             │
│ username            │
│ password            │
│ email               │
│ role                │
│ enabled             │
└──────────┬──────────┘
           │
           │ 1
           │
           │ *
┌──────────▼──────────┐
│     concerts        │
├─────────────────────┤
│ id (PK)             │
│ title               │
│ date                │
│ location            │
│ description         │
│ created_by (FK)     │
└─────────────────────┘


┌─────────────────────┐
│      photos         │
├─────────────────────┤
│ id (PK)             │
│ filename            │
│ url                 │
│ category            │
│ upload_date         │
└─────────────────────┘


┌─────────────────────┐
│      tracks         │
├─────────────────────┤
│ id (PK)             │
│ title               │
│ artist              │
│ url                 │
│ duration            │
└─────────────────────┘


┌─────────────────────┐
│ contact_messages    │
├─────────────────────┤
│ id (PK)             │
│ name                │
│ email               │
│ subject             │
│ message             │
│ sent_date           │
│ is_read             │
└─────────────────────┘
```

### **Relations à expliquer :**

1. **users → concerts (1:N)**
   > "Un utilisateur admin peut créer plusieurs concerts. C'est une relation one-to-many avec clé étrangère `created_by`."

2. **Tables indépendantes**
   > "Les autres tables (photos, tracks, contact_messages) sont indépendantes pour simplifier la gestion."

### **Contraintes importantes :**

```sql
-- Exemples de contraintes

users:
  - PK: id
  - UNIQUE: username, email
  - NOT NULL: username, password, email

concerts:
  - PK: id
  - FK: created_by → users(id)
  - NOT NULL: title, date, location

photos:
  - PK: id
  - NOT NULL: filename, url
```

---

## 3️⃣ DIAGRAMME DE SÉQUENCE - AUTHENTIFICATION (À dessiner en 3 minutes)

### **Ce que vous dites :**

> "Je vais vous montrer le flux d'authentification avec Spring Security. C'est un exemple de séquence classique avec validation de session."

### **Schéma :**

```
Utilisateur      Navigateur      Spring Security      UserService      Base de données
    │                │                  │                  │                 │
    │   Clique       │                  │                  │                 │
    │   "Login"      │                  │                  │                 │
    ├───────────────►│                  │                  │                 │
    │                │  POST /login     │                  │                 │
    │                ├─────────────────►│                  │                 │
    │                │                  │ loadUserByUsername()               │
    │                │                  ├─────────────────►│                 │
    │                │                  │                  │ SELECT * FROM   │
    │                │                  │                  │    users        │
    │                │                  │                  ├────────────────►│
    │                │                  │                  │                 │
    │                │                  │                  │ User (BCrypt)   │
    │                │                  │                  │◄────────────────┤
    │                │                  │                  │                 │
    │                │                  │  User object     │                 │
    │                │                  │◄─────────────────┤                 │
    │                │                  │                  │                 │
    │                │  Validation OK   │                  │                 │
    │                │  + Session créée │                  │                 │
    │                │◄─────────────────┤                  │                 │
    │                │                  │                  │                 │
    │  Redirection   │                  │                  │                 │
    │   /admin       │                  │                  │                 │
    │◄───────────────┤                  │                  │                 │
    │                │                  │                  │                 │
```

### **Étapes clés à expliquer :**

1. **POST /login** → Spring Security intercepte la requête
2. **loadUserByUsername()** → Recherche l'utilisateur en base
3. **BCrypt.matches()** → Compare le mot de passe hashé
4. **Session créée** → Cookie JSESSIONID avec HTTP-only + SameSite
5. **Redirection** → Vers /admin si succès

---

## 4️⃣ DIAGRAMME DE SÉQUENCE - CRÉATION DE CONCERT (À dessiner en 2 minutes)

### **Ce que vous dites :**

> "Voici le flux complet de création d'un concert par un admin. On voit bien la séparation des couches."

### **Schéma :**

```
Admin     Navigateur     Controller     Service     Repository     Base de données
  │           │              │             │            │                │
  │ Remplit   │              │             │            │                │
  │ formulaire│              │             │            │                │
  ├──────────►│              │             │            │                │
  │           │ POST /admin/concerts/add   │            │                │
  │           ├─────────────►│             │            │                │
  │           │              │ Validation  │            │                │
  │           │              │ (@Valid)    │            │                │
  │           │              │             │            │                │
  │           │              │ saveConcert()            │                │
  │           │              ├────────────►│            │                │
  │           │              │             │ save()     │                │
  │           │              │             ├───────────►│                │
  │           │              │             │            │ INSERT INTO    │
  │           │              │             │            │   concerts     │
  │           │              │             │            ├───────────────►│
  │           │              │             │            │                │
  │           │              │             │            │   Concert      │
  │           │              │             │            │◄───────────────┤
  │           │              │             │   Concert  │                │
  │           │              │             │◄───────────┤                │
  │           │              │   Concert   │            │                │
  │           │              │◄────────────┤            │                │
  │           │  Redirect    │             │            │                │
  │           │  /admin/concerts            │            │                │
  │           │◄─────────────┤             │            │                │
  │  Message  │              │             │            │                │
  │  succès   │              │             │            │                │
  │◄──────────┤              │             │            │                │
  │           │              │             │            │                │
```

### **Couches traversées :**

1. **Controller** → Reçoit la requête HTTP + valide avec `@Valid`
2. **Service** → Applique la logique métier (si nécessaire)
3. **Repository** → Persiste en base via Spring Data JPA
4. **Redirect** → Pattern Post-Redirect-Get pour éviter double soumission

---

## 5️⃣ ARCHITECTURE CI/CD PIPELINE (À dessiner en 2 minutes)

### **Ce que vous dites :**

> "J'ai mis en place un pipeline CI/CD automatisé avec GitHub Actions. À chaque push, tout est testé et déployé automatiquement."

### **Schéma :**

```
┌──────────────────────────────────────────────────────────────────┐
│                     GITHUB ACTIONS PIPELINE                      │
└──────────────────────────────────────────────────────────────────┘

    Développeur push code
            │
            ▼
    ┌───────────────┐
    │  Git commit   │
    │  + push       │
    └───────┬───────┘
            │
            ▼
    ┌───────────────────────────────────────────────────────────┐
    │                  ÉTAPE 1 : BUILD & TESTS                  │
    ├───────────────────────────────────────────────────────────┤
    │                                                           │
    │  ┌──────────────────┐       ┌─────────────────────┐     │
    │  │  Backend Maven   │       │  Frontend Vue.js    │     │
    │  │  mvn clean test  │       │  npm run test       │     │
    │  └────────┬─────────┘       └──────────┬──────────┘     │
    │           │                            │                │
    │           ▼                            ▼                │
    │      545 tests                    Tests Vitest          │
    │      (99.8% OK)                                         │
    └───────────────────────┬───────────────────────────────────┘
                            │
                            ▼
    ┌───────────────────────────────────────────────────────────┐
    │              ÉTAPE 2 : TESTS E2E + LIGHTHOUSE             │
    ├───────────────────────────────────────────────────────────┤
    │                                                           │
    │  ┌──────────────────┐       ┌─────────────────────┐     │
    │  │  Playwright E2E  │       │  Lighthouse Audit   │     │
    │  │  Tests UI        │       │  SEO/Perf/A11y      │     │
    │  └────────┬─────────┘       └──────────┬──────────┘     │
    │           │                            │                │
    │           ▼                            ▼                │
    │      Tests OK                     Score 95/100          │
    └───────────────────────┬───────────────────────────────────┘
                            │
                            ▼
    ┌───────────────────────────────────────────────────────────┐
    │              ÉTAPE 3 : BUILD DOCKER + SÉCURITÉ            │
    ├───────────────────────────────────────────────────────────┤
    │                                                           │
    │  ┌──────────────────┐       ┌─────────────────────┐     │
    │  │ docker build     │       │  Trivy Security     │     │
    │  │ Image créée      │       │  Scan vulnérabilités│     │
    │  └────────┬─────────┘       └──────────┬──────────┘     │
    │           │                            │                │
    │           ▼                            ▼                │
    │      Image Docker                 Pas de CVE critique   │
    └───────────────────────┬───────────────────────────────────┘
                            │
                            ▼
    ┌───────────────────────────────────────────────────────────┐
    │           ÉTAPE 4 : PUSH VERS REGISTRY                    │
    ├───────────────────────────────────────────────────────────┤
    │                                                           │
    │         GitHub Container Registry (ghcr.io)              │
    │         Image prête pour déploiement                     │
    │                                                           │
    └───────────────────────────────────────────────────────────┘
```

### **Points à mentionner :**

1. **Automatisation complète** → Aucune intervention manuelle
2. **Quality gates** → Si un test échoue, le pipeline s'arrête
3. **Sécurité intégrée** → Scan Trivy détecte les vulnérabilités
4. **Lighthouse** → Score 95/100 validé automatiquement

---

## 6️⃣ ARCHITECTURE DOCKER COMPOSE (À dessiner en 1 minute 30)

### **Ce que vous dites :**

> "Pour le déploiement, j'utilise Docker Compose avec 3 conteneurs qui communiquent via un réseau Docker."

### **Schéma :**

```
┌────────────────────────────────────────────────────────────┐
│               DOCKER NETWORK (musician-network)            │
│                                                            │
│  ┌──────────────────┐                                     │
│  │  musician-app    │                                     │
│  │  (Spring Boot)   │                                     │
│  │  Port: 8080      │                                     │
│  └────────┬─────────┘                                     │
│           │                                               │
│           │ JDBC                                          │
│           │                                               │
│  ┌────────▼─────────┐      ┌──────────────────┐          │
│  │  musician-db     │      │ musician-adminer │          │
│  │  (MariaDB)       │◄─────┤ (Interface web)  │          │
│  │  Port: 3306      │      │ Port: 8081       │          │
│  └──────────────────┘      └──────────────────┘          │
│           │                                               │
│           │                                               │
│  ┌────────▼─────────┐                                     │
│  │  Volume Docker   │                                     │
│  │  /var/lib/mysql  │                                     │
│  │  (persistance)   │                                     │
│  └──────────────────┘                                     │
└────────────────────────────────────────────────────────────┘
```

### **Commandes essentielles :**

```bash
# Lancer tous les services
docker-compose up -d

# Vérifier l'état
docker-compose ps

# Voir les logs
docker-compose logs -f musician-app

# Arrêter tout
docker-compose down
```

---

## 7️⃣ FLUX DE SÉCURITÉ CSRF (À dessiner si question sur la sécurité)

### **Ce que vous dites :**

> "Spring Security génère automatiquement un token CSRF pour protéger contre les attaques Cross-Site Request Forgery."

### **Schéma :**

```
┌─────────────────────────────────────────────────────────────┐
│                    PROTECTION CSRF                          │
└─────────────────────────────────────────────────────────────┘

  Utilisateur                 Spring Security            Base
      │                              │                    │
      │  1. GET /admin/concerts/add  │                    │
      ├─────────────────────────────►│                    │
      │                              │                    │
      │  2. Formulaire HTML +        │                    │
      │     <input name="_csrf"      │                    │
      │      value="abc123xyz">      │                    │
      │◄─────────────────────────────┤                    │
      │                              │                    │
      │  3. POST /admin/concerts/add │                    │
      │     _csrf=abc123xyz          │                    │
      ├─────────────────────────────►│                    │
      │                              │                    │
      │                              │ 4. Validation token│
      │                              │    OK ✓            │
      │                              │                    │
      │                              │ 5. INSERT concert  │
      │                              ├───────────────────►│
      │                              │                    │
      │  6. Succès                   │                    │
      │◄─────────────────────────────┤                    │
      │                              │                    │


  Attaquant malveillant
      │
      │  POST /admin/concerts/add
      │  (SANS token _csrf)
      ├─────────────────────────────►│
      │                              │
      │  403 Forbidden               │
      │  "Invalid CSRF Token"        │
      │◄─────────────────────────────┤
      │                              │
      └──────────────────────────────┘
         ❌ ATTAQUE BLOQUÉE
```

---

## 8️⃣ RÉCAPITULATIF - CHECKLIST DE SCHÉMAS

### **À préparer pour l'oral :**

| Schéma | Temps | Quand le dessiner |
|--------|-------|-------------------|
| **Architecture 3 tiers** | 2 min | Section "Architecture technique" (minute 10) |
| **Base de données** | 3 min | Section "Conception de la persistance" (minute 15) |
| **Séquence authentification** | 3 min | Section "Sécurité" (minute 28) |
| **Séquence création concert** | 2 min | Question du jury sur "Comment ça marche ?" |
| **CI/CD Pipeline** | 2 min | Section "DevOps" (minute 35) |
| **Docker Compose** | 1m30 | Section "Déploiement" (minute 37) |
| **CSRF** | 2 min | Si question sur la sécurité |

### **Matériel à avoir :**

- ✅ Tableau blanc + 3 marqueurs (noir, bleu, rouge)
- ✅ Ou feuilles A4 + stylos
- ✅ Gomme/effaceur
- ✅ Ce document en version papier

---

## 💡 CONSEILS POUR BIEN DESSINER

### **Règle d'or :**
> "Un schéma simple bien expliqué vaut mieux qu'un schéma complexe confus."

### **Checklist avant de commencer :**
1. ✅ Annoncer ce que vous allez dessiner
2. ✅ Commencer par le haut (pas le bas)
3. ✅ Utiliser des rectangles pour les composants
4. ✅ Utiliser des flèches pour les flux
5. ✅ Annoter avec des légendes courtes
6. ✅ Expliquer pendant que vous dessinez (ne pas dessiner en silence)

### **Phrases types :**

- "Je vais vous dessiner l'architecture globale..."
- "Ici, on a le frontend qui communique via HTTPS..."
- "Cette flèche représente un appel JDBC vers la base..."
- "Vous voyez ici la séparation des couches..."

### **Si vous vous trompez :**
> "Excusez-moi, je corrige : en fait, c'est plutôt comme ça..."

Ne paniquez pas ! Le jury comprend le stress. L'important est de montrer que vous comprenez l'architecture.

---

## 🎯 ENTRAÎNEMENT

### **Semaine avant l'oral :**

**Jour 1-2 :** Dessinez chaque schéma 3 fois sur papier
**Jour 3-4 :** Chronométrez-vous (ne pas dépasser les temps indiqués)
**Jour 5-6 :** Dessinez + expliquez à voix haute
**Jour 7 :** Repos (juste révision mentale)

### **Simulation complète :**

1. Mettez un chronomètre
2. Dessinez l'architecture 3 tiers en 2 minutes
3. Expliquez pendant que vous dessinez
4. Recommencez jusqu'à ce que ce soit fluide

---

## ✅ VOUS ÊTES PRÊT !

Avec ces 7 schémas, vous pouvez illustrer visuellement :
- ✅ L'architecture globale
- ✅ La base de données
- ✅ La sécurité
- ✅ Les flux métier
- ✅ Le CI/CD
- ✅ Le déploiement

**Le jury sera impressionné par votre capacité à vulgariser votre architecture ! 🚀**

---

## 📌 AIDE-MÉMOIRE ULTRA-RAPIDE

Si vous avez un trou de mémoire, rappelez-vous juste :

```
3 TIERS = 3 RECTANGLES

Vue.js
  ↓ HTTPS/JSON
Spring Boot
  ↓ JDBC
MariaDB
```

Tout le reste découle de cette base !

**VOUS ALLEZ ASSURER ! 💪**
