#!/usr/bin/env bash
set -euo pipefail

# Restore uploads archive into the 'app-uploads' volume.
# Usage: scripts/restore-uploads.sh backups/uploads-YYYYMMDD.tar.gz

if [[ $# -lt 1 ]]; then
  echo "Usage: $0 <uploads.tar.gz>" >&2
  exit 1
fi

FILE=$1
if [[ ! -f "$FILE" ]]; then
  echo "[restore-uploads] ERROR: File not found: $FILE" >&2
  exit 1
fi

echo "[restore-uploads] Restoring $FILE into volume app-uploads"
docker run --rm \
  -v app-uploads:/data \
  -v "$PWD":/in \
  alpine:3.20 sh -lc "cd /data && rm -rf ./* && tar xzf /in/$FILE"

echo "[restore-uploads] Restore complete"

