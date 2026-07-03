#!/usr/bin/env bash
# EC2 표준 배포 스크립트 (GitHub Actions / 수동 배포 공용)
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
DEPLOY_DIR="$(cd "${SCRIPT_DIR}/.." && pwd)"
PROJECT_DIR="${PROJECT_DIR:-$HOME/my-portfolio}"

echo ">>> [1/4] git pull"
cd "${PROJECT_DIR}"
git pull --ff-only

echo ">>> [2/4] docker compose build"
cd "${DEPLOY_DIR}"
if [[ -f .env ]]; then
  # shellcheck disable=SC1091
  set -a
  source .env
  set +a
fi

export DOCKER_BUILDKIT=1

# 소형 EC2(t3.micro 등) OOM 방지: 앱 서비스별 순차 빌드
SERVICES=(mido-app legacy-app pivot-app allohub-app if-app golmok-app briefly-app)
for svc in "${SERVICES[@]}"; do
  echo "  building ${svc}..."
  docker compose build "${svc}"
done

echo ">>> [3/4] docker compose up -d"
docker compose up -d --remove-orphans

echo ">>> [4/4] docker image prune -f"
docker image prune -f

echo ">>> deploy complete"
docker compose ps
