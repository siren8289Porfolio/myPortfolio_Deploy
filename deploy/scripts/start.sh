#!/usr/bin/env bash
set -euo pipefail

cd "$(dirname "$0")/.."

if [[ -f .env ]]; then
  # shellcheck disable=SC1091
  set -a
  source .env
  set +a
fi

docker compose -f docker-compose.yml -f docker-compose.local.yml up -d --build
