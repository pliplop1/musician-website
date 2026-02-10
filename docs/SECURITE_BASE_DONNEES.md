# Sécurité de la Base de Données - Analyse et Recommandations

## ⚠️ Problèmes de Sécurité Identifiés

### 1. **Port MariaDB Exposé Publiquement** 🔴 CRITIQUE
**Problème :**
```yaml
ports:
  - "3306:3306"  # ❌ DANGEREUX : Port MySQL accessible depuis l'extérieur
```

**Risque :** N'importe qui sur le réseau peut tenter de se connecter à votre base de données.

**Solution :**
```yaml
# ✅ NE PAS exposer le port en production
# Retirer complètement cette section en production
# L'application Spring Boot se connecte via le réseau Docker interne
```

---

### 2. **Adminer Accessible Sans Authentification** 🔴 CRITIQUE
**Problème :**
```yaml
adminer:
  ports:
    - "8081:8080"  # ❌ Interface d'admin DB accessible publiquement
```

**Risque :** Interface d'administration de la base de données accessible à tous.

**Solutions :**

#### Option A : Désactiver Adminer en production
```yaml
# Commenter complètement le service adminer en production
# adminer:
#   image: adminer:4.8.1
#   ...
```

#### Option B : Protéger par proxy avec authentification
```yaml
# Utiliser un reverse proxy avec basic auth (Caddy, Nginx, Traefik)
adminer:
  # Ne pas exposer le port directement
  # ports:
  #   - "8081:8080"  # Retirer cette ligne
  networks:
    - musician-network
```

---

### 3. **Mots de Passe Faibles Par Défaut** 🟠 IMPORTANT
**Problème :**
```yaml
MYSQL_ROOT_PASSWORD: ${DB_ROOT_PASSWORD:-root_password_change_me}
MYSQL_PASSWORD: ${DB_PASSWORD:-musician_password}
```

**Risque :** Mots de passe prédictibles et faciles à deviner.

**Solution :** Créer un fichier `.env` avec des mots de passe forts :
```bash
# .env (À NE JAMAIS COMMITER SUR GIT)
DB_ROOT_PASSWORD=X7k#9mP@qR2$vN4&wL8^yT6*uJ3!hG5
DB_PASSWORD=bZ9$rQ4@xN7#pM2&vK8^wT5*yL3!fH6
```

**Génération de mots de passe forts :**
```bash
# Sur Windows PowerShell
-join (1..32 | ForEach-Object { [char]((33..126) | Get-Random) })

# Sur Linux/Mac
openssl rand -base64 32
```

---

### 4. **Connexion Non Chiffrée (useSSL=false)** 🟠 IMPORTANT
**Problème :**
```yaml
DATABASE_URL: jdbc:mariadb://database:3306/${DB_NAME}?useSSL=false
```

**Risque :** Les données transitent en clair sur le réseau Docker.

**Solution :**
```yaml
DATABASE_URL: jdbc:mariadb://database:3306/${DB_NAME}?useSSL=true&trustServerCertificate=true
```

**Note :** En production avec certificat valide :
```yaml
DATABASE_URL: jdbc:mariadb://database:3306/${DB_NAME}?useSSL=true&serverSslCert=/path/to/cert.pem
```

---

### 5. **Privilèges Utilisateur Trop Larges** 🟡 MOYEN
**Problème actuel :** L'utilisateur `musician_user` a probablement tous les privilèges sur la base.

**Solution :** Créer un utilisateur avec privilèges limités :

```sql
-- Connexion en tant que root
CREATE USER 'musician_app'@'%' IDENTIFIED BY 'MOT_DE_PASSE_FORT';

-- Accorder uniquement les privilèges nécessaires
GRANT SELECT, INSERT, UPDATE, DELETE ON musician_db.* TO 'musician_app'@'%';

-- NE PAS accorder :
-- DROP, CREATE, ALTER, INDEX (gérés par Hibernate en dev, scripts en prod)
-- GRANT OPTION (permet de donner des privilèges à d'autres)

FLUSH PRIVILEGES;
```

---

### 6. **Pas de Limitation des Connexions** 🟡 MOYEN
**Solution :** Limiter le nombre de connexions par utilisateur :

```sql
ALTER USER 'musician_user'@'%' WITH MAX_USER_CONNECTIONS 50;
FLUSH PRIVILEGES;
```

---

### 7. **Pas de Backup Automatisé** 🟠 IMPORTANT
**Risque :** Perte de données en cas de problème.

**Solution :** Script de backup automatique quotidien (déjà fourni) :
```bash
# Cron job Linux (tous les jours à 2h du matin)
0 2 * * * /path/to/backup-db.sh

# Windows Task Scheduler
# Créer une tâche planifiée quotidienne
```

---

## ✅ Configuration Sécurisée Recommandée

### Fichier `.env` (À CRÉER - NE JAMAIS COMMITER)
```env
# Base de données
DB_ROOT_PASSWORD=votreMotDePasseRoot_TresSecurise123!@#
DB_NAME=musician_db
DB_USER=musician_app
DB_PASSWORD=votreMotDePasseApp_TresSecurise456!@#
DB_PORT=3306  # Ne pas exposer en production

# Application
APP_PORT=8080
SPRING_PROFILES_ACTIVE=prod

# Email
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=plipplop887@gmail.com
MAIL_PASSWORD=votre_app_password
MAIL_FROM=noreply@duoblackandwhite.com
ADMIN_EMAIL=dumoulin.marilyne@gmail.com

# Admin initial
ADMIN_INITIAL_PASSWORD=MotDePasseAdmin_TresSecurise789!@#

# Adminer (dev uniquement)
ADMINER_PORT=8081
```

### Fichier `.env.production` (Pour la production)
```env
# Base de données (ne pas exposer le port)
DB_ROOT_PASSWORD=GENEREZ_UN_MOT_DE_PASSE_UNIQUE
DB_NAME=musician_db
DB_USER=musician_app
DB_PASSWORD=GENEREZ_UN_MOT_DE_PASSE_UNIQUE
# DB_PORT non défini = port non exposé

# Application
APP_PORT=8080
SPRING_PROFILES_ACTIVE=prod

# NE PAS ACTIVER ADMINER EN PRODUCTION
# ADMINER_PORT non défini
```

### `docker-compose.prod.yml` (Pour la production)
```yaml
version: '3.8'

services:
  database:
    image: mariadb:10.11
    container_name: musician-db
    restart: unless-stopped
    environment:
      MYSQL_ROOT_PASSWORD: ${DB_ROOT_PASSWORD}
      MYSQL_DATABASE: ${DB_NAME}
      MYSQL_USER: ${DB_USER}
      MYSQL_PASSWORD: ${DB_PASSWORD}
      TZ: Europe/Paris
    # ✅ PAS DE PORTS EXPOSÉS EN PRODUCTION
    volumes:
      - db-data:/var/lib/mysql
      - ./docker/mariadb/my.cnf:/etc/mysql/conf.d/custom.cnf:ro
    networks:
      - musician-network
    healthcheck:
      test: ["CMD", "healthcheck.sh", "--connect", "--innodb_initialized"]
      interval: 10s
      timeout: 5s
      retries: 5

  app:
    build:
      context: .
      dockerfile: Dockerfile
    container_name: musician-app
    restart: unless-stopped
    depends_on:
      database:
        condition: service_healthy
    environment:
      SPRING_PROFILES_ACTIVE: prod
      DATABASE_URL: jdbc:mariadb://database:3306/${DB_NAME}?useSSL=true&trustServerCertificate=true
      DATABASE_USERNAME: ${DB_USER}
      DATABASE_PASSWORD: ${DB_PASSWORD}
      # ... autres variables
    ports:
      - "127.0.0.1:8080:8080"  # ✅ Bind uniquement sur localhost si derrière proxy
    volumes:
      - app-uploads:/app/uploads
      - app-logs:/app/logs
    networks:
      - musician-network
    healthcheck:
      test: ["CMD", "wget", "--quiet", "--tries=1", "--spider", "http://localhost:8080/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 60s

  # ❌ PAS D'ADMINER EN PRODUCTION
  # Si besoin d'accéder à la DB en production, utiliser SSH tunnel:
  # ssh -L 3306:localhost:3306 user@serveur
  # puis connexion locale avec DBeaver/MySQL Workbench

volumes:
  db-data:
    driver: local
  app-uploads:
    driver: local
  app-logs:
    driver: local

networks:
  musician-network:
    driver: bridge
```

---

## 🔧 Configuration MariaDB Renforcée

Créer `docker/mariadb/my.cnf` :
```ini
[mysqld]
# Sécurité
bind-address = 0.0.0.0  # Dans Docker, OK car isolé
skip-name-resolve = 1
local-infile = 0

# Performance et sécurité
max_connections = 100
connect_timeout = 10
wait_timeout = 600
max_allowed_packet = 64M

# Logs de sécurité
log_error = /var/log/mysql/error.log
log_warnings = 2

# Désactiver les fonctionnalités non utilisées
skip-symbolic-links

[client]
default-character-set = utf8mb4

[mysql]
default-character-set = utf8mb4
```

---

## 🛡️ Checklist de Sécurité

### Développement Local
- [x] Mots de passe différents de ceux de production
- [x] Adminer accessible uniquement sur localhost
- [ ] Fichier `.env` créé et ajouté à `.gitignore`
- [ ] Port 3306 exposé uniquement si nécessaire

### Production
- [ ] Fichier `.env.production` avec mots de passe forts (32+ caractères)
- [ ] Port 3306 MariaDB NON exposé publiquement
- [ ] Adminer complètement désactivé
- [ ] SSL/TLS activé pour connexion DB
- [ ] Certificats SSL/TLS valides configurés
- [ ] Backups automatisés quotidiens
- [ ] Logs de sécurité activés et monitorés
- [ ] Firewall configuré (uniquement ports 80/443 ouverts)
- [ ] Fail2ban installé et configuré
- [ ] Updates régulières de MariaDB

---

## 📊 Niveaux de Sécurité

| Aspect | Développement | Staging | Production |
|--------|--------------|---------|------------|
| Port DB exposé | ✅ OK (local) | ⚠️ VPN uniquement | ❌ Jamais |
| Adminer | ✅ OK | ⚠️ Basic Auth | ❌ Désactivé |
| SSL DB | ⚠️ Optionnel | ✅ Requis | ✅ Requis + Cert |
| Mot de passe | 🟡 Moyen | 🟢 Fort | 🟢 Très fort (32+) |
| Backups | 📅 Manuel | 📅 Quotidien | 📅 Multi (quotidien + incrémental) |

---

## 🚀 Actions Immédiates Recommandées

1. **Créer `.env` avec mots de passe forts**
2. **Ajouter `.env` et `.env.*` à `.gitignore`**
3. **Ne pas exposer le port 3306 en production**
4. **Désactiver Adminer en production**
5. **Activer SSL pour la connexion DB**
6. **Configurer les backups automatiques**
7. **Limiter les privilèges de l'utilisateur DB**

---

## 📝 Commandes Utiles

### Vérifier les utilisateurs et privilèges
```bash
docker exec musician-db mysql -uroot -p -e "SELECT user, host FROM mysql.user;"
docker exec musician-db mysql -uroot -p -e "SHOW GRANTS FOR 'musician_user'@'%';"
```

### Backup manuel
```bash
# Voir scripts fournis : backup-db.sh, restore-db.sh
```

### Changer les mots de passe
```bash
docker exec musician-db mysql -uroot -p -e "ALTER USER 'musician_user'@'%' IDENTIFIED BY 'NOUVEAU_MOT_DE_PASSE_FORT';"
```

### Tester la connexion SSL
```bash
docker exec musician-db mysql -umusician_user -p -e "SHOW STATUS LIKE 'Ssl_cipher';"
```

---

**Date d'analyse :** 2025-10-31
**Analyste :** Claude Code
**Niveau de risque actuel :** 🟠 MOYEN (Dev) / 🔴 ÉLEVÉ (si déployé en production tel quel)
