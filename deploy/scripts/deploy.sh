#!/usr/bin/env bash
# EC2에서 앱 배포 (git pull + compose up)
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
DEPLOY_DIR="$(cd "${SCRIPT_DIR}/.." && pwd)"
PROJECT_DIR="${PROJECT_DIR:-$HOME/my-portfolio}"

cd "${PROJECT_DIR}"
git pull --ff-only

cd deploy
if [[ -f .env ]]; then
  # shellcheck disable=SC1091
  set -a
  source .env
  set +a
fi

docker compose up -d --build --remove-orphans
docker compose ps
