#!/usr/bin/env bash
set -euo pipefail

# Simple MariaDB/MySQL backup helper
# - Tries to dump from a running database container named 'musician-db'
# - Falls back to using env vars to connect over TCP
# Env vars (read from .env if present):
#   DATABASE_URL (jdbc:..., optional)
#   DATABASE_USERNAME
#   DATABASE_PASSWORD
#   DB_HOST, DB_PORT, DB_NAME (preferred for TCP)
# Output: backups/db-YYYYMMDD-HHMMSS.sql.gz

SCRIPT_DIR=$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)
ROOT_DIR=$(cd "$SCRIPT_DIR/.." && pwd)
cd "$ROOT_DIR"

# Load .env if present
if [[ -f .env ]]; then
  set -a
  # shellcheck source=/dev/null
  . ./.env || true
  set +a
fi

mkdir -p backups
TS=$(date +%Y%m%d-%H%M%S)
OUT="backups/db-$TS.sql.gz"

echo "[backup-db] Starting backup at $TS"

# Try container exec first (compose service mariadb name is 'musician-db')
if docker ps --format '{{.Names}}' | grep -q '^musician-db$'; then
  echo "[backup-db] Found container 'musician-db' — dumping from container"
  docker exec musician-db sh -c "mysqldump -u\"$DATABASE_USERNAME\" -p\"$DATABASE_PASSWORD\" \"${DB_NAME:-musician_db}\"" | gzip -9 > "$OUT"
  echo "[backup-db] Backup written to $OUT"
  exit 0
fi

echo "[backup-db] No container 'musician-db' running, trying TCP connection"

# Prefer explicit DB_HOST/DB_PORT/DB_NAME from env
HOST=${DB_HOST:-}
PORT=${DB_PORT:-3306}
NAME=${DB_NAME:-}

# If not set, try to parse DATABASE_URL (jdbc:mariadb://host:port/name?...)
if [[ -z "$HOST" || -z "$NAME" ]]; then
  if [[ -n "${DATABASE_URL:-}" ]]; then
    # naive parse for jdbc URL
    # shellcheck disable=SC2001
    STRIPPED=$(echo "$DATABASE_URL" | sed -E 's|jdbc:(mysql|mariadb)://||')
    HOSTPORT=${STRIPPED%%/*}
    NAME=${STRIPPED#*/}
    NAME=${NAME%%\?*}
    HOST=${HOSTPORT%%:*}
    PORT=${HOSTPORT##*:}
    [[ "$PORT" == "$HOST" ]] && PORT=3306
  fi
fi

if [[ -z "${DATABASE_USERNAME:-}" || -z "${DATABASE_PASSWORD:-}" || -z "$HOST" || -z "$NAME" ]]; then
  echo "[backup-db] ERROR: Missing DB connection info. Set DB_HOST, DB_NAME, DATABASE_USERNAME, DATABASE_PASSWORD (or DATABASE_URL)." >&2
  exit 1
fi

echo "[backup-db] Dumping $NAME@$HOST:$PORT"
docker run --rm --network host mariadb:10.11 \
  sh -lc "mysqldump -h '$HOST' -P '$PORT' -u '$DATABASE_USERNAME' -p'${DATABASE_PASSWORD}' '$NAME'" | gzip -9 > "$OUT"

echo "[backup-db] Backup written to $OUT"

