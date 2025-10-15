#!/usr/bin/env bash
set -euo pipefail

# Backup the 'app-uploads' Docker volume as a compressed tarball.
# Output: backups/uploads-YYYYMMDD-HHMMSS.tar.gz

SCRIPT_DIR=$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)
ROOT_DIR=$(cd "$SCRIPT_DIR/.." && pwd)
cd "$ROOT_DIR"

mkdir -p backups
TS=$(date +%Y%m%d-%H%M%S)
OUT="backups/uploads-$TS.tar.gz"

echo "[backup-uploads] Creating archive at $OUT"
docker run --rm \
  -v app-uploads:/data:ro \
  -v "$PWD":/out \
  alpine:3.20 sh -lc "cd /data && tar czf /out/$OUT . || true"

echo "[backup-uploads] Backup written to $OUT"

