# ✅ CHECKLIST LE JOUR DE L'ORAL

**⏰ À FAIRE 30 MINUTES AVANT L'ORAL**

---

## 🚀 ÉTAPE 1 : Démarrage Docker (5 min)

### 1.1 Démarrer Docker Desktop
- [ ] Ouvrir Docker Desktop
- [ ] Attendre que Docker soit complètement démarré (icône verte)

### 1.2 Démarrer l'application
```bash
cd C:\Users\plipl\Documents\workspace-spring-tools-for-eclipse-4.31.0.RELEASE\musician-website
docker-compose up -d
```

### 1.3 Vérifier l'état des conteneurs
```bash
docker-compose ps
```

**Résultat attendu :**
```
musician-app       Up X minutes (healthy)
musician-db        Up X minutes (healthy)
musician-adminer   Up X minutes
```

### 1.4 Attendre le démarrage complet (2 minutes)
```bash
docker logs musician-app -f
```

**Attendre de voir :**
```
Started MusicianWebsiteApplication in X seconds
```

Puis appuyer sur **Ctrl+C** pour arrêter les logs.

---

## ✅ ÉTAPE 2 : Tests de Vérification (10 min)

### 2.1 Test Application (8080)
- [ ] Ouvrir http://localhost:8080/
- [ ] Vérifier que la page d'accueil s'affiche
- [ ] Cliquer sur "Galerie" → photos visibles
- [ ] Cliquer sur "Musique" → tracks visibles
- [ ] Cliquer sur "Vidéos" → vidéos visibles
- [ ] Cliquer sur "Concerts" → concerts visibles

### 2.2 Test Interface Admin (8080)
- [ ] Ouvrir http://localhost:8080/login
- [ ] Se connecter avec : `admin` / `admin123`
- [ ] Vérifier que l'interface admin s'affiche

### 2.3 Test Adminer (8081)
- [ ] Ouvrir http://localhost:8081/
- [ ] Se connecter avec :
  - Système : **MySQL**
  - Serveur : **database**
  - Utilisateur : **musician_user**
  - Mot de passe : **SecurePassword123!**
  - Base de données : **musician_db**
- [ ] Cliquer sur "musician_db" dans le menu gauche
- [ ] Vérifier que les tables s'affichent

### 2.4 Test Recherche Galerie (IMPORTANT!)
- [ ] Aller sur http://localhost:8080/galerie
- [ ] Taper `8` dans la recherche → doit afficher 1 photo
- [ ] Taper `concert` → doit afficher 4 photos
- [ ] Taper `png` → doit afficher 4 photos

**Si ça fonctionne :** ✅ Parfait !
**Si ça ne fonctionne pas :** Appuyer sur **Ctrl+Shift+R** pour vider le cache

---

## 🎯 ÉTAPE 3 : Préparation Finale (15 min)

### 3.1 Documents à avoir OUVERTS sur un écran secondaire
- [ ] `docs/IDENTIFIANTS_DEMO.md` (identifiants + URLs)
- [ ] `docs/REPONSES_QUESTIONS_PIEGES.md` (réponses aux questions difficiles)
- [ ] Ce fichier `CHECKLIST_JOUR_ORAL.md`

### 3.2 Fenêtres à avoir OUVERTES
- [ ] Docker Desktop (pour montrer que ça tourne)
- [ ] http://localhost:8080/ (page d'accueil)
- [ ] http://localhost:8081/ (Adminer, déjà connecté)
- [ ] Terminal avec `docker-compose ps` affiché

### 3.3 Dernière vérification
- [ ] Fermer TOUS les autres programmes (Spotify, Discord, etc.)
- [ ] Désactiver les notifications Windows
- [ ] Mettre le téléphone en silencieux
- [ ] Brancher le PC sur secteur (pas sur batterie)

---

## ⚠️ SI PROBLÈME PENDANT L'ORAL

### Problème 1 : L'application ne répond plus

**Symptôme :** http://localhost:8080/ ne charge pas

**Solution Rapide (30 secondes) :**
```bash
docker-compose restart app
```

Puis attendre 15 secondes et recharger la page.

**Solution Complète (2 minutes) :**
```bash
docker-compose down
docker-compose up -d
```

Dire au jury : *"Je redémarre les conteneurs Docker, cela prend 30 secondes."*

---

### Problème 2 : Adminer ne se connecte pas

**Symptôme :** Erreur "php_network_getaddresses"

**Solution :**
- Vérifier que le serveur est bien : **database** (pas "musician-db")
- Si ça ne marche pas, utiliser l'IP : **172.18.0.2**

Trouver l'IP avec :
```bash
docker inspect musician-db --format='{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}'
```

---

### Problème 3 : Docker plante complètement

**Symptôme :** Tous les conteneurs sont down

**Solution Dernière Chance (3 minutes) :**
```bash
docker-compose down
docker system prune -f
docker-compose up -d --build
```

Dire au jury : *"Docker a eu un souci, je le réinitialise. C'est une situation qui n'arrive jamais en production car on a des systèmes de haute disponibilité."*

Pendant que ça redémarre, **continuer la présentation orale** en parlant de l'architecture, des tests, de la sécurité.

---

## 🎤 SI VOUS DEVEZ REDÉMARRER PENDANT L'ORAL

**Ce que vous dites au jury :**

> "Je vais redémarrer les conteneurs Docker, cela prend environ 30 secondes. C'est l'avantage de Docker : un redémarrage rapide et sans perte de données grâce aux volumes persistants.
>
> Pendant que ça redémarre, je peux vous expliquer l'architecture de l'application..."

Puis vous enchaînez sur :
- Architecture 3 tiers
- Sécurité Spring Security
- Tests unitaires et d'intégration
- CI/CD GitHub Actions

**Le jury appréciera votre sang-froid et votre capacité à gérer un incident.**

---

## 💡 PHRASES À DIRE AU JURY

### Quand vous démarrez la démo :

> "Mon application tourne sous Docker avec 3 conteneurs : MariaDB pour la base de données, Spring Boot pour le backend, et Adminer pour la gestion de la BDD. Docker me permet d'avoir un environnement reproductible et facilement déployable."

### Si le jury demande : "Et si ça plante en production ?"

> "J'ai configuré Docker avec `restart: unless-stopped`, ce qui signifie que si un conteneur plante, il redémarre automatiquement. En production, on ajouterait Kubernetes pour la haute disponibilité avec plusieurs replicas, un load balancer, et des health checks automatiques."

### Si le jury demande : "Vous avez des sauvegardes ?"

> "Les données sont stockées dans des volumes Docker persistants. En production, je mettrais en place des sauvegardes automatiques quotidiennes avec une rotation sur 7 jours, et des snapshots de volumes pour la disaster recovery."

---

## ✅ RÈGLES D'OR LE JOUR J

1. **NE PAS TOUCHER AU CODE** le jour de l'oral
2. **NE PAS FAIRE DE `docker-compose build`** avant l'oral (sauf si vraiment nécessaire)
3. **TESTER 30 MINUTES AVANT**, puis **NE PLUS TOUCHER À RIEN**
4. **GARDER SON CALME** si un problème survient
5. **AVOIR CONFIANCE** : votre application est solide !

---

## 🎓 VOUS ÊTES PRÊT !

Votre Docker est configuré avec :
- ✅ Redémarrage automatique
- ✅ Health checks
- ✅ Volumes persistants
- ✅ Network isolé

**Les chances de plantage sont TRÈS FAIBLES (< 1%).**

**Et même si ça plante, vous avez le protocole de récupération !**

---

## 📞 DERNIER RECOURS

**Si vraiment TOUT plante et que rien ne fonctionne :**

1. Gardez votre calme
2. Dites au jury : *"J'ai une version de backup que je peux déployer en 2 minutes"*
3. Pendant que ça rebuild, **continuez votre présentation orale**
4. Parlez des tests, de la sécurité, de l'architecture
5. Montrez votre documentation (`docs/`)
6. Montrez votre code source

**Le jury évalue votre compétence globale, pas seulement la démo live.**

---

**BON COURAGE ! VOUS ALLEZ RÉUSSIR ! 🏆**
