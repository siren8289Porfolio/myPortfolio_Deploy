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
if ! docker compose up -d --remove-orphans; then
  echo ">>> ERROR: compose up failed — mido-app logs:"
  docker logs mido-app 2>&1 | tail -40 || true
  exit 1
fi

# nginx는 default.conf를 단일 파일로 bind mount한다. git reset --hard가
# 이 파일을 치환(inode 변경)해도 nginx 컨테이너가 재생성되지 않으면
# 예전 inode를 계속 참조해 최신 설정이 반영되지 않는다(stale bind mount).
# 매 배포마다 nginx를 강제로 재생성해 항상 최신 설정을 물게 한다.
echo ">>> [4b/5] Force-recreate nginx to avoid stale config bind mount"
docker compose up -d --force-recreate nginx

echo ">>> [5/5] Cleanup images"
docker image prune -f

echo ">>> Health check"
sleep 5
for app in mido legacy pivot allohub if golmok briefly; do
  code=$(docker exec portfolio-nginx curl -s -o /dev/null -w "%{http_code}" "http://127.0.0.1/${app}/" || echo "000")
  if [[ "$code" == "200" ]]; then
    echo "  ✓ ${app} OK (${code})"
  else
    echo "  ✗ ${app} FAIL (${code})"
  fi
done

echo ">>> deploy complete"
docker ps
docker compose ps
