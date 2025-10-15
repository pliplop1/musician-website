Déploiement automatique (CI → VM) — Performance, SEO, Accessibilité, Best Practices

Objectif
- Publier l’image Docker en CI (GHCR), puis déployer automatiquement sur une VM via SSH et docker compose.

Prérequis côté VM
- Ubuntu 22.04+, accès SSH (clé), Docker et Docker Compose installés.
- Répertoire de déploiement, par ex.: `~/musician-website` avec:
  - `deploy/docker-compose.prod.yml` (depuis ce repo)
  - un fichier `.env` basé sur `.env.example` (ne pas commiter).
  - `deploy/Caddyfile` si vous activez le proxy HTTPS Caddy

Secrets GitHub (Repository → Settings → Secrets and variables → Actions)
- `DEPLOY_HOST` : IP ou domaine de la VM
- `DEPLOY_USER` : utilisateur SSH (ex. ubuntu)
- `SSH_PRIVATE_KEY` : clé privée SSH (format PEM) du compte ayant accès à la VM
- `ENABLE_DOCKER_PUSH` : mettre à `true` pour autoriser le push d'image GHCR depuis la CI (garde-fou)

Flux CI/CD
1. Push sur `main/master`
2. CI exécute: lint, audits, tests (unitaires + E2E)
3. CI build & push image → `ghcr.io/ORG/REPO:latest` + tag `sha`
4. Job deploy (SSH) sur la VM:
   - `docker login ghcr.io -u ${{ github.actor }} -p ${{ secrets.GITHUB_TOKEN }}`
   - `docker compose -f deploy/docker-compose.prod.yml pull && docker compose -f deploy/docker-compose.prod.yml up -d`
   - `docker image prune -f` (nettoyage)

Mise en place sur la VM (une fois)
```bash
mkdir -p ~/musician-website
cd ~/musician-website
# Copiez les fichiers depuis le repo
# - deploy/docker-compose.prod.yml
# - deploy/Caddyfile (si proxy HTTPS)
# - .env (basé sur .env.example)
```

DNS & HTTPS (Caddy)
- Pointez un enregistrement A/AAAA de votre domaine (`DOMAIN`) vers l’IP de votre serveur.
- Renseignez `DOMAIN` et `ACME_EMAIL` dans `.env`.
- Le service `caddy` (ports 80/443) gère le HTTPS automatique (Let’s Encrypt), la compression (zstd/gzip),
  les en-têtes sécurité et le cache des assets statiques (perf/SEO/best practices).
  L’application n’expose pas le port 8080 à l’hôte en production (proxy only).

Content Security Policy (CSP)
- Une CSP stricte est activée par Caddy pour bloquer les sources non autorisées et renforcer la sécurité.
- Domaines autorisés par défaut: `self`, `cdnjs.cloudflare.com` (Font Awesome), YouTube/SoundCloud/Spotify pour les iframes.
- Si vous ajoutez d’autres CDNs (polices, scripts, images), adaptez `deploy/Caddyfile` (directive `Content-Security-Policy`).
- Évitez `unsafe-inline` sur `script-src`; pour les styles, `unsafe-inline` est toléré ici (inline styles existants). Préférez migrer les styles inline vers des classes CSS.

Bonnes pratiques respectées
- Performance: image unique, ressourcement conteneur (CPU/RAM), healthcheck, pas de rebuild en prod.
- SEO: meta/robots/sitemap au build front, inchangés au déploiement.
- Accessibilité: bannière cookies, skip-link, tests axe-core/E2E.
- Best Practices: CI complète (lint, audit, tests, e2e), secrets en dehors du code, rollback simple.

Rollback rapide
```bash
# sur la VM
docker compose -f deploy/docker-compose.prod.yml up -d --rollback # si votre compose le supporte
# Ou bien redéployer un tag précis:
APP_IMAGE=ghcr.io/ORG/REPO:sha-XXXX \
  docker compose -f deploy/docker-compose.prod.yml up -d
```

Sauvegardes (Backups)
- Base de données:
  - Dump: `bash scripts/backup-db.sh` → `backups/db-YYYYMMDD-HHMMSS.sql.gz`
  - Restaure: `bash scripts/restore-db.sh backups/db-*.sql.gz`
  - Utilise le conteneur `musician-db` s’il est présent, sinon les variables `.env` (`DB_HOST`, `DB_PORT`, `DB_NAME`, `DATABASE_USERNAME`, `DATABASE_PASSWORD`).
- Fichiers uploadés:
  - Archive: `bash scripts/backup-uploads.sh` → `backups/uploads-YYYYMMDD-HHMMSS.tar.gz`
  - Restaure: `bash scripts/restore-uploads.sh backups/uploads-*.tar.gz`
- Bonnes pratiques:
  - Planifier quotidiennement (cron/Task Scheduler), rétention 7/4/6 (quotidien/hebdo/mensuel).
  - Stocker hors de la machine (S3/Backblaze/Drive chiffré) et tester la restauration mensuellement.
  - Les sauvegardes sont ignorées par git (`.gitignore`).
