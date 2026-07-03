#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
docker compose up -d
echo "PostgreSQL ready at localhost:${DB_PORT:-5432}/${DB_NAME:-dasi_golmok}"
