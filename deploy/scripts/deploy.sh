#!/usr/bin/env bash
# EC2 표준 배포 스크립트 — GHCR pull only (no build on EC2)
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
DEPLOY_DIR="$(cd "${SCRIPT_DIR}/.." && pwd)"
PROJECT_DIR="${PROJECT_DIR:-$HOME/my-portfolio}"

echo ">>> [1/4] git pull (compose / nginx 설정 동기화)"
cd "${PROJECT_DIR}"
git pull --ff-only

cd "${DEPLOY_DIR}"
if [[ -f .env ]]; then
  # shellcheck disable=SC1091
  set -a
  source .env
  set +a
fi

echo ">>> [2/4] GHCR login"
if [[ -z "${GHCR_TOKEN:-}" ]]; then
  echo "ERROR: GHCR_TOKEN is required for docker compose pull"
  exit 1
fi
echo "${GHCR_TOKEN}" | docker login ghcr.io -u "${GHCR_USERNAME:?GHCR_USERNAME required}" --password-stdin

echo ">>> [3/4] docker compose pull"
docker compose pull

echo ">>> [4/4] docker compose up -d"
docker compose up -d --remove-orphans
docker image prune -f

echo ">>> deploy complete"
docker compose ps
