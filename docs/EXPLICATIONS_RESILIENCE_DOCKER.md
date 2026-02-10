# 🛡️ EXPLICATIONS RÉSILIENCE DOCKER - Pour l'Oral

**⚠️ ANTI-SÈCHE À AVOIR SOUS LES YEUX PENDANT L'ORAL**

---

## 🎯 QUESTION PROBABLE DU JURY

> "Comment assurez-vous la disponibilité et la résilience de votre application ?"

---

## 1️⃣ RESTART POLICY

### Ce que vous dites

> "J'ai configuré la restart policy à `unless-stopped` pour que Docker redémarre automatiquement les conteneurs qui plantent, sans intervention manuelle."

### Configuration

```yaml
app:
  restart: unless-stopped
```

### Ce que ça fait

- Si l'application Java plante → **redémarre en 5-10 secondes**
- Si la base de données crash → **redémarre en 5 secondes**
- Si vous redémarrez Docker Desktop → **tous les conteneurs redémarrent automatiquement**

### Question piège

**Q:** *"Et si l'application plante en boucle ?"*

**R:** "Docker applique un backoff exponentiel : 1s, 2s, 4s, 8s... jusqu'à 1 minute entre chaque redémarrage. Cela évite de surcharger le système. En production, on ajouterait des alertes Prometheus pour détecter ce type de situation."

---

## 2️⃣ HEALTHCHECK

### Ce que vous dites

> "J'ai implémenté des healthchecks qui vérifient automatiquement que les services sont opérationnels. Si un service devient 'unhealthy', Docker le redémarre."

### Configuration BDD

```yaml
healthcheck:
  test: ["CMD", "healthcheck.sh", "--connect", "--innodb_initialized"]
  interval: 10s        # Vérifie toutes les 10 secondes
  timeout: 5s
  retries: 5           # 5 échecs consécutifs → restart
```

### Configuration App

```yaml
healthcheck:
  test: ["CMD", "wget", "--quiet", "--tries=1", "--spider",
         "http://localhost:8080/actuator/health"]
  interval: 30s        # Vérifie toutes les 30 secondes
  timeout: 10s
  retries: 3           # 3 échecs consécutifs → restart
  start_period: 60s    # Laisse 60s au démarrage
```

### Ce que ça fait

- Docker exécute une commande de test **régulièrement**
- Si la commande échoue plusieurs fois → conteneur devient "unhealthy"
- Docker redémarre automatiquement le conteneur

### Démonstration live

```bash
docker-compose ps
```

Vous verrez : `Up 13 minutes (healthy)` ✅

### Question piège

**Q:** *"Qu'est-ce que Spring Boot Actuator ?"*

**R:** "C'est un module Spring Boot qui expose des endpoints de monitoring : `/actuator/health` pour vérifier l'état de l'application, `/actuator/metrics` pour les métriques, `/actuator/info` pour les informations système. C'est standard en production avec Prometheus et Grafana."

---

## 3️⃣ DEPENDS_ON + SERVICE_HEALTHY

### Ce que vous dites

> "J'utilise `depends_on` avec la condition `service_healthy` pour garantir que la base de données est complètement démarrée et opérationnelle avant que l'application ne se lance. Cela évite les erreurs de connexion."

### Configuration

```yaml
app:
  depends_on:
    database:
      condition: service_healthy
```

### Séquence de démarrage

```
1. Docker démarre "database" (MariaDB)
2. Healthcheck vérifie que MariaDB est prêt → "healthy"
3. Docker démarre "app" (Spring Boot)
4. L'application se connecte à la BDD (déjà prête) → SUCCESS
```

### Ce que ça évite

❌ Sans `service_healthy` :
- L'app démarre en même temps que la BDD
- L'app essaie de se connecter alors que la BDD n'est pas prête
- Erreur : "Connection refused"

✅ Avec `service_healthy` :
- L'app attend que la BDD soit healthy
- Pas d'erreur de connexion possible

### Question piège

**Q:** *"C'est quoi la différence avec `depends_on` simple ?"*

**R:** "`depends_on` simple lance juste les conteneurs dans l'ordre, mais ne vérifie pas qu'ils sont prêts. Avec `service_healthy`, Docker attend que le healthcheck valide que le service est opérationnel. C'est beaucoup plus fiable."

---

## 4️⃣ VOLUMES PERSISTANTS

### Ce que vous dites

> "J'utilise des volumes Docker persistants pour sauvegarder les données critiques : base de données, fichiers uploadés, et logs. Ces volumes sont stockés sur le disque de l'hôte, indépendamment des conteneurs. Même si je supprime et rebuild les conteneurs, les données restent intactes."

### Configuration

```yaml
volumes:
  db-data:
    driver: local
    name: musician-db-data      # Base de données

  app-uploads:
    driver: local
    name: musician-uploads      # Photos/Music/Videos uploadées

  app-logs:
    driver: local
    name: musician-logs         # Logs de l'application
```

### Ce que ça protège

| Scénario | Résultat |
|----------|----------|
| Conteneur redémarre | ✅ Données conservées |
| Conteneur supprimé | ✅ Données conservées |
| Docker Desktop redémarre | ✅ Données conservées |
| Mise à jour de l'image | ✅ Données conservées |
| `docker-compose down` | ✅ Données conservées |
| **Seulement si `docker volume rm`** | ❌ Données perdues |

### Démonstration live

```bash
docker volume ls
```

Vous verrez vos 3 volumes :
- `musician-db-data`
- `musician-uploads`
- `musician-logs`

### Question piège

**Q:** *"Comment vous faites pour sauvegarder ces volumes ?"*

**R:** "En production, je mettrais en place un cron job qui exporte quotidiennement les volumes vers un stockage externe (AWS S3, Azure Blob Storage, ou un NAS). Pour la base de données :
```bash
docker exec musician-db mysqldump -u root -p musician_db > backup.sql
aws s3 cp backup.sql s3://backups/musician/$(date +%Y%m%d).sql
```
Avec une rotation sur 7 jours pour les backups complets, et des backups incrémentaux entre les deux."

---

## 5️⃣ BONUS : NETWORK ISOLÉ

### Ce que vous dites

> "J'ai créé un réseau Docker isolé pour que mes conteneurs communiquent de manière sécurisée entre eux, tout en étant isolés du reste du système."

### Configuration

```yaml
networks:
  musician-network:
    driver: bridge
    name: musician-network
```

### Avantages

- ✅ Les conteneurs se voient entre eux par leur nom (ex: "database")
- ✅ Isolés du reste du système Docker
- ✅ Contrôle des communications inter-conteneurs
- ✅ Sécurité renforcée

---

## 📊 TABLEAU RÉCAPITULATIF

| Mécanisme | Commande pour tester | Résultat attendu |
|-----------|---------------------|------------------|
| Restart policy | `docker-compose ps` | `Up X minutes` |
| Healthcheck | `docker-compose ps` | `(healthy)` |
| Depends_on | `docker logs musician-app` | Pas d'erreur de connexion BDD |
| Volumes | `docker volume ls` | 3 volumes listés |

---

## 🎬 DÉMONSTRATION COMPLÈTE POUR LE JURY

### Étape 1 : Montrer l'état actuel

```bash
docker-compose ps
```

**Ce que vous dites :**
> "Vous voyez que tous les conteneurs sont 'Up' et 'healthy'. C'est le healthcheck qui fonctionne et qui garantit que les services sont opérationnels."

### Étape 2 : Montrer les volumes

```bash
docker volume ls
```

**Ce que vous dites :**
> "Voici les 3 volumes persistants qui sauvegardent mes données : la base de données, les fichiers uploadés, et les logs."

### Étape 3 : (Optionnel) Simuler un plantage

**⚠️ SEULEMENT SI LE JURY DEMANDE !**

```bash
# Tuer l'application
docker kill musician-app

# Attendre 10 secondes
timeout /t 10

# Vérifier le redémarrage automatique
docker-compose ps
```

**Ce que vous dites :**
> "J'ai simulé un plantage en tuant le conteneur. Grâce à la restart policy, Docker l'a automatiquement redémarré. C'est un mécanisme de base de haute disponibilité."

---

## 🎓 QUESTIONS PIÈGES ET RÉPONSES

### Q1: "Pourquoi pas Kubernetes ?"

**R:** "Kubernetes est excellent pour la production avec orchestration multi-serveurs, load balancing, et auto-scaling. Pour ce projet de démo, Docker Compose suffit amplement et est beaucoup plus simple à maintenir. En production pour un grand site, j'adopterais Kubernetes avec plusieurs replicas de l'application, un load balancer, et des health checks automatiques."

### Q2: "C'est quoi la différence entre un conteneur et une image ?"

**R:** "Une image est un template immuable qui contient le code, les dépendances, et la configuration. Un conteneur est une instance en cours d'exécution de cette image. On peut avoir plusieurs conteneurs basés sur la même image. Analogie : l'image est la classe, le conteneur est l'instance."

### Q3: "Vous avez combien de replicas ?"

**R:** "Actuellement 1 replica de chaque service, ce qui est suffisant pour une démo. En production, j'aurais 3 replicas de l'application avec un load balancer (nginx ou Traefik), et une base de données en cluster avec réplication master-slave pour la haute disponibilité."

### Q4: "Comment vous gérez les mises à jour ?"

**R:** "Je rebuild l'image Docker avec `docker-compose build`, puis je fais un `docker-compose up -d`. Docker remplace les conteneurs un par un en préservant les volumes. En production, j'utiliserais le blue-green deployment ou le rolling update de Kubernetes pour avoir zero downtime."

### Q5: "Les logs, ils vont où ?"

**R:** "Les logs de l'application Spring Boot sont stockés dans le volume `musician-logs` qui est persistant. En production, j'utiliserais un stack ELK (Elasticsearch, Logstash, Kibana) ou Grafana Loki pour centraliser les logs de tous les conteneurs et avoir des dashboards de monitoring."

---

## ✅ CHECKLIST AVANT DE RÉPONDRE

Quand le jury pose une question sur la résilience :

1. ✅ Expliquer le mécanisme technique
2. ✅ Montrer la configuration dans docker-compose.yml
3. ✅ Dire ce que ça protège
4. ✅ Donner un exemple concret de scénario
5. ✅ Mentionner ce que vous feriez en production

**Formule magique :**
> "Dans ce projet, j'ai utilisé [MÉCANISME] parce que [RAISON]. En production, j'ajouterais [AMÉLIORATION PRODUCTION]."

---

## 🎯 PHRASES CLÉS À RETENIR

**Sur la résilience générale :**
> "J'ai mis en place 4 mécanismes de résilience : restart automatique, healthchecks, démarrage ordonné, et volumes persistants. Ces mécanismes garantissent que l'application peut se remettre automatiquement de la plupart des incidents sans intervention manuelle."

**Sur Docker vs Kubernetes :**
> "Docker Compose est parfait pour ce projet de démo. En production avec beaucoup de trafic, j'adopterais Kubernetes pour l'orchestration avancée, l'auto-scaling, et la haute disponibilité multi-serveurs."

**Sur les backups :**
> "Les volumes Docker protègent contre la perte de données lors des redémarrages. En production, j'ajouterais des backups automatiques quotidiens vers un stockage externe avec rotation sur 7 jours."

---

## 🚀 VOUS ÊTES PRÊT !

Avec ces explications, vous pouvez répondre à TOUTES les questions du jury sur la résilience Docker.

**Stratégie pendant l'oral :**
1. Montrez `docker-compose ps` → conteneurs healthy
2. Montrez `docker volume ls` → volumes persistants
3. Expliquez les 4 mécanismes
4. Restez calme et confiant

**Le jury sera impressionné par votre maîtrise de Docker ! 🏆**
