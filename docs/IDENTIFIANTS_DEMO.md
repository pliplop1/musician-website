# 🔐 IDENTIFIANTS ET URLS - DÉMO ORAL CDA

**⚠️ DOCUMENT À AVOIR SOUS LES YEUX PENDANT L'ORAL**

---

## 🌐 URLS DE L'APPLICATION

### **Application principale (port 8080)**
- **Page d'accueil** : http://localhost:8080/
- **Inscription** : http://localhost:8080/register
- **Connexion admin** : http://localhost:8080/login
- **Interface admin** : http://localhost:8080/admin
- **API REST concerts (à venir)** : http://localhost:8080/api/public/concerts/upcoming
- **API REST concerts (passés)** : http://localhost:8080/api/public/concerts/past
- **API REST photos** : http://localhost:8080/api/public/photos
- **API REST tracks** : http://localhost:8080/api/public/tracks
- **API REST videos** : http://localhost:8080/api/public/videos

### **Adminer - Interface BDD (port 8081)**
- **URL** : http://localhost:8081/

### **MariaDB - Accès direct (port 3306)**
- **Host** : localhost
- **Port** : 3306

---

## 🔑 IDENTIFIANTS ADMINER (POUR MONTRER LA BDD AU JURY)

**URL** : http://localhost:8081/

| Champ | Valeur |
|-------|--------|
| Système | MySQL |
| Serveur | `database` |
| Utilisateur | `musician_user` |
| Mot de passe | `SecurePassword123!` |
| Base de données | `musician_db` |

**Copier/Coller rapide :**
```
Serveur: database
Utilisateur: musician_user
Mot de passe: SecurePassword123!
Base: musician_db
```

**⚠️ Note importante** : Le nom du serveur est `database` (nom du service dans docker-compose.yml), pas `musician-db` (qui est seulement le nom du conteneur).

### **Alternative : Compte ROOT (accès complet)**

Si le jury veut voir toutes les bases :

```
Serveur: database
Utilisateur: root
Mot de passe: RootPassword123!
Base: musician_db
```

---

## 👤 COMPTE ADMIN DE L'APPLICATION

Pour vous connecter à l'interface admin (`http://localhost:8080/login`) :

| Champ | Valeur |
|-------|--------|
| Username | `admin` |
| Password | `admin123` |

**Note** : Ces identifiants sont créés automatiquement au démarrage de l'application (voir `DataInitializer.java`)

---

## 📧 CONFIGURATION EMAIL

**Email de test** : plipplop887@gmail.com
- Envoi d'emails via Gmail SMTP
- Utilisé pour : reset de mot de passe, notifications

**Pour tester l'envoi d'email pendant l'oral** :
1. Aller sur http://localhost:8080/contact
2. Remplir le formulaire
3. Vérifier les logs : `docker logs musician-app -f`

---

## 🐳 COMMANDES DOCKER ESSENTIELLES

### **Démarrer l'application**
```bash
docker-compose up -d
```

### **Vérifier l'état des conteneurs**
```bash
docker-compose ps
```

### **Voir les logs de l'application**
```bash
docker logs musician-app -f
```

### **Arrêter l'application**
```bash
docker-compose down
```

### **Redémarrer si problème**
```bash
docker-compose restart
```

---

## 📊 TABLES DE LA BASE DE DONNÉES

Quand vous montrez Adminer au jury, voici les tables importantes à expliquer :

| Table | Description | Champs clés |
|-------|-------------|-------------|
| **users** | Utilisateurs administrateurs | username, password (BCrypt), email, role |
| **concerts** | Événements du duo | title, date, location, description, created_by (FK) |
| **photos** | Galerie photos | filename, url, category, upload_date |
| **tracks** | Répertoire musical | title, artist, url, duration |
| **contact_messages** | Messages de contact | name, email, subject, message, sent_date |
| **password_reset_tokens** | Tokens de reset password | token, user_id, expiry_date |
| **comments** | Commentaires (si implémenté) | content, author, created_date |

### **Relations importantes**

```
users (1) ----< (*) concerts
  └─ Un utilisateur peut créer plusieurs concerts
  └─ Clé étrangère : concerts.created_by → users.id

users (1) ----< (*) password_reset_tokens
  └─ Un utilisateur peut avoir plusieurs tokens (historique)
```

---

## 🎯 SCÉNARIO DÉMONSTRATION - BASE DE DONNÉES

### **Ce que vous dites au jury :**

> "Je vais vous montrer la base de données via Adminer, une interface web légère que j'ai intégrée dans mon docker-compose."

**Actions :**

1. **Ouvrir** http://localhost:8081/
2. **Entrer les identifiants** (voir ci-dessus)
3. **Cliquer sur "musician_db"** dans le menu de gauche
4. **Montrer la liste des tables**

> "Voici toutes les tables de mon application. Le schéma a été créé automatiquement par Flyway lors du démarrage."

5. **Cliquer sur la table "concerts"**

> "Cette table stocke tous les concerts du duo. Elle a une relation avec la table users via la clé étrangère created_by."

6. **Montrer la structure**

> "Vous voyez ici les colonnes : id (clé primaire auto-incrémentée), title, date, location, description, et created_by qui référence l'utilisateur qui a créé le concert."

7. **Cliquer sur "Sélectionner les données"**

> "Voici quelques concerts que j'ai créés pour la démo."

8. **Montrer la table "users"**

> "Dans la table users, les mots de passe sont hashés avec BCrypt. Vous voyez que ce n'est pas stocké en clair pour des raisons de sécurité."

---

## 🛠️ EN CAS DE PROBLÈME PENDANT L'ORAL

### **Problème 1 : Adminer ne se connecte pas**

**Symptôme** : "php_network_getaddresses: getaddrinfo failed"

**Solution rapide** :
```bash
docker-compose down
docker-compose up -d
```

Attendre 30 secondes et réessayer.

**Alternative** : Utiliser l'IP directe
```bash
docker inspect musician-db --format='{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}'
```
Puis utiliser cette IP au lieu de `database`

**Rappel** : Le nom du serveur dans Docker est `database` (nom du service), pas `musician-db` (nom du conteneur).

### **Problème 2 : L'application ne répond pas**

**Vérifier l'état** :
```bash
docker-compose ps
```

**Si musician-app n'est pas "healthy"** :
```bash
docker logs musician-app --tail 50
```

**Redémarrer** :
```bash
docker-compose restart musician-app
```

### **Problème 3 : Mot de passe oublié**

**Tous les mots de passe sont dans ce document !**

Sinon, regarder le fichier `.env` à la racine du projet.

---

## 💡 PHRASES À DIRE AU JURY

### **Quand vous montrez Adminer :**

> "Adminer est un outil open-source léger, équivalent à phpMyAdmin mais en un seul fichier PHP. Je l'ai intégré dans mon docker-compose pour faciliter la visualisation de la base de données en développement."

### **Quand vous montrez les tables :**

> "J'ai conçu un schéma relationnel avec 6 tables principales. Les migrations sont gérées par Flyway, ce qui permet de versionner le schéma de base de données comme du code."

### **Quand vous montrez les données :**

> "Les données que vous voyez ont été insérées par mon composant DataInitializer au démarrage de l'application. C'est pratique pour avoir des données de démo sans intervention manuelle."

### **Si on vous demande "Pourquoi MariaDB ?"**

> "J'ai choisi MariaDB car c'est un fork open-source de MySQL, performant et compatible. C'est très utilisé en production et il y a beaucoup de documentation."

---

## 📸 CAPTURES D'ÉCRAN DE BACKUP

**Si Adminer ne fonctionne pas pendant l'oral**, ayez des captures d'écran de :
- La liste des tables
- Le contenu de la table `concerts`
- Le contenu de la table `users` (montrer BCrypt)
- La structure d'une table avec les clés étrangères

**Créer les captures maintenant** :
1. Connectez-vous à Adminer
2. Faites des captures d'écran
3. Mettez-les dans un dossier `docs/screenshots/`

---

## ✅ CHECKLIST AVANT LA DÉMO

**30 minutes avant l'oral :**
- [ ] Démarrer Docker Desktop
- [ ] `docker-compose up -d`
- [ ] Vérifier http://localhost:8080/ (application)
- [ ] Vérifier http://localhost:8081/ (Adminer)
- [ ] Tester la connexion à Adminer avec les identifiants ci-dessus
- [ ] Se connecter à l'interface admin (admin/admin123)

**Si tout fonctionne :**
- [ ] NE PLUS TOUCHER À RIEN ! 😊

---

## 🎓 COMPÉTENCES CDA VALIDÉES PAR LA BASE DE DONNÉES

Quand vous montrez la base de données, vous validez :

✅ **CP04 - Concevoir une base de données** :
- Modèle relationnel (tables, clés primaires, clés étrangères)
- Contraintes d'intégrité
- Normalisation

✅ **CP06 - Développer les composants d'accès aux données** :
- Spring Data JPA
- Repositories
- Requêtes personnalisées

✅ **Sécurité des données** :
- Mots de passe hashés (BCrypt)
- Tokens de reset sécurisés
- Validation des entrées

---

## 🚀 VOUS ÊTES PRÊT !

Avec ce document sous les yeux, vous avez :
- ✅ Toutes les URLs
- ✅ Tous les identifiants
- ✅ Toutes les commandes Docker
- ✅ Toutes les explications pour le jury

**Imprimez ce document ou gardez-le ouvert sur un écran secondaire pendant l'oral !**

**BON COURAGE POUR L'ORAL ! 🏆**
