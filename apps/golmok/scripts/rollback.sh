#!/usr/bin/env bash
set -euo pipefail

VERSION="${ROLLBACK_VERSION:-v0.1.0}"
COMPOSE_FILE="${COMPOSE_FILE:-docker-compose.yml}"

echo "=========================================="
echo " 롤백 시작 → ${VERSION}"
echo "=========================================="

docker compose -f "$COMPOSE_FILE" down

docker tag "dasi-golmok:${VERSION}" dasi-golmok:current 2>/dev/null || {
  echo "[ERROR] 롤백 이미지 dasi-golmok:${VERSION} 을 찾을 수 없습니다."
  exit 1
}

docker compose -f "$COMPOSE_FILE" up -d

./scripts/health-check.sh "${NEXTAUTH_URL:-http://localhost:3000}"

echo "=========================================="
echo " 롤백 완료"
echo "=========================================="
