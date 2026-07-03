#!/usr/bin/env bash
# EC2 표준 배포 스크립트 — GitHub main과 동기화 후 GHCR pull only (no build)
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
DEPLOY_DIR="$(cd "${SCRIPT_DIR}/.." && pwd)"
PROJECT_DIR="${PROJECT_DIR:-$HOME/my-portfolio}"

echo ">>> [1/5] Fetch latest source"
cd "${PROJECT_DIR}"
git fetch origin

echo ">>> [2/5] Reset working tree"
git reset --hard origin/main
git clean -fd

cd "${DEPLOY_DIR}"
if [[ -f .env ]]; then
  # shellcheck disable=SC1091
  set -a
  source .env
  set +a
fi

echo ">>> [3/5] Pull latest images"
if [[ -z "${GHCR_TOKEN:-}" ]]; then
  echo "ERROR: GHCR_TOKEN is required for docker compose pull"
  exit 1
fi
echo "${GHCR_TOKEN}" | docker login ghcr.io -u "${GHCR_USERNAME:?GHCR_USERNAME required}" --password-stdin
docker compose pull

echo ">>> [4/5] Restart containers"
docker compose up -d --remove-orphans

echo ">>> [5/5] Cleanup images"
docker image prune -f

echo ">>> deploy complete"
docker ps
docker compose ps
