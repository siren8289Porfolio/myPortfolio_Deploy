#!/usr/bin/env bash
# Phase 3 — EC2에서 Docker Compose 스택 빌드·기동·검증
# t3.micro(1GB RAM) OOM 방지: swap + 순차 빌드
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
DEPLOY_DIR="$(cd "${SCRIPT_DIR}/.." && pwd)"
PROJECT_DIR="${PROJECT_DIR:-$HOME/my-portfolio}"

echo ">>> [1/6] 프로젝트 최신화"
cd "${PROJECT_DIR}"
git fetch origin
git pull --ff-only origin main || git pull --ff-only

cd "${DEPLOY_DIR}"
if [[ -f .env ]]; then
  # shellcheck disable=SC1091
  set -a
  source .env
  set +a
fi

echo ">>> [2/6] t3.micro 메모리 보조 (swap 2GB, 없을 때만)"
if [[ $(free -m | awk '/^Mem:/{print $2}') -lt 2048 ]] && ! swapon --show | grep -q '/swapfile'; then
  if [[ ! -f /swapfile ]]; then
    sudo fallocate -l 2G /swapfile || sudo dd if=/dev/zero of=/swapfile bs=1M count=2048
    sudo chmod 600 /swapfile
    sudo mkswap /swapfile
    sudo swapon /swapfile
    grep -q '/swapfile' /etc/fstab || echo '/swapfile none swap sw 0 0' | sudo tee -a /etc/fstab
    echo "swap 2GB enabled"
  fi
fi

echo ">>> [3/6] docker compose build (서비스별 순차, OOM·네트워크 오류 방지)"
export DOCKER_BUILDKIT=1
SERVICES=(mido-app legacy-app pivot-app allohub-app if-app golmok-app briefly-app)
for svc in "${SERVICES[@]}"; do
  echo "  building ${svc}..."
  docker compose build "${svc}"
done
docker compose pull nginx 2>/dev/null || true

echo ">>> [4/6] docker compose up -d"
docker compose up -d --remove-orphans

echo ">>> [5/6] 컨테이너 상태"
docker compose ps
docker ps --format 'table {{.Names}}\t{{.Status}}\t{{.Ports}}'

echo ">>> [6/6] localhost 헬스 체크 (Nginx 경유)"
PATHS=(mido legacy pivot allohub if golmok briefly)
for p in "${PATHS[@]}"; do
  code=$(curl -s -o /dev/null -w '%{http_code}' "http://127.0.0.1/${p}/" || true)
  echo "  /${p}/ -> HTTP ${code}"
done

echo ""
echo ">>> Phase 3 EC2 스택 기동 완료."
echo ">>> Security Group에 HTTP 80(Anywhere) 추가 후 브라우저에서 Public IP로 확인하세요."
