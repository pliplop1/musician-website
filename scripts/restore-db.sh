#!/usr/bin/env bash
set -euo pipefail

# Restore a MariaDB/MySQL dump (.sql.gz or .sql) into the database.
# Usage: scripts/restore-db.sh backups/db-YYYYMMDD.sql.gz

if [[ $# -lt 1 ]]; then
  echo "Usage: $0 <dump.sql[.gz]>" >&2
  exit 1
fi

FILE=$1
if [[ ! -f "$FILE" ]]; then
  echo "[restore-db] ERROR: File not found: $FILE" >&2
  exit 1
fi

SCRIPT_DIR=$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)
ROOT_DIR=$(cd "$SCRIPT_DIR/.." && pwd)
cd "$ROOT_DIR"

if [[ -f .env ]]; then
  set -a; . ./.env || true; set +a
fi

NAME=${DB_NAME:-musician_db}
HOST=${DB_HOST:-}
PORT=${DB_PORT:-3306}

if docker ps --format '{{.Names}}' | grep -q '^musician-db$'; then
  echo "[restore-db] Restoring into container 'musician-db' database '$NAME'"
  if [[ "$FILE" == *.gz ]]; then
    gunzip -c "$FILE" | docker exec -i musician-db sh -lc "mysql -u\"$DATABASE_USERNAME\" -p\"$DATABASE_PASSWORD\" \"$NAME\""
  else
    docker exec -i musician-db sh -lc "mysql -u\"$DATABASE_USERNAME\" -p\"$DATABASE_PASSWORD\" \"$NAME\"" < "$FILE"
  fi
  echo "[restore-db] Restore complete"
  exit 0
fi

if [[ -z "$HOST" ]]; then
  if [[ -n "${DATABASE_URL:-}" ]]; then
    STRIPPED=$(echo "$DATABASE_URL" | sed -E 's|jdbc:(mysql|mariadb)://||')
    HOSTPORT=${STRIPPED%%/*}
    NAME=${STRIPPED#*/}; NAME=${NAME%%\?*}
    HOST=${HOSTPORT%%:*}
    PORT=${HOSTPORT##*:}; [[ "$PORT" == "$HOST" ]] && PORT=3306
  fi
fi

if [[ -z "${DATABASE_USERNAME:-}" || -z "${DATABASE_PASSWORD:-}" || -z "$HOST" || -z "$NAME" ]]; then
  echo "[restore-db] ERROR: Missing DB connection info. Set DB_HOST, DB_NAME, DATABASE_USERNAME, DATABASE_PASSWORD (or DATABASE_URL)." >&2
  exit 1
fi

echo "[restore-db] Restoring $FILE into $NAME@$HOST:$PORT"
if [[ "$FILE" == *.gz ]]; then
  gunzip -c "$FILE" | docker run --rm --network host -i mariadb:10.11 \
    sh -lc "mysql -h '$HOST' -P '$PORT' -u '$DATABASE_USERNAME' -p'${DATABASE_PASSWORD}' '$NAME'"
else
  docker run --rm --network host -i mariadb:10.11 \
    sh -lc "mysql -h '$HOST' -P '$PORT' -u '$DATABASE_USERNAME' -p'${DATABASE_PASSWORD}' '$NAME'" < "$FILE"
fi

echo "[restore-db] Restore complete"

