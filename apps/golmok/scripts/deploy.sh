#!/usr/bin/env bash
set -euo pipefail

VERSION="${APP_VERSION:-0.1.0}"
COMPOSE_FILE="${COMPOSE_FILE:-docker-compose.yml}"

echo "=========================================="
echo " 다시, 골목 배포 v${VERSION}"
echo "=========================================="

# 1. 환경변수 확인
if [ ! -f .env ]; then
  echo "[ERROR] .env 파일이 없습니다. .env.production.example을 참고하세요."
  exit 1
fi

if grep -q "CHANGE_ME" .env; then
  echo "[WARN] .env에 CHANGE_ME 값이 남아있습니다. 배포 전 확인하세요."
fi

# 2. DB 백업 (기존 컨테이너 실행 중일 때)
if docker ps --format '{{.Names}}' | grep -q dasi-golmok-db; then
  echo "==> DB 백업..."
  BACKUP_FILE="backup_$(date +%Y%m%d_%H%M%S).sql"
  docker exec dasi-golmok-db pg_dump -U "${DB_USERNAME:-app_user}" "${DB_NAME:-dasi_golmok}" > "$BACKUP_FILE" 2>/dev/null || true
  echo "    백업 파일: ${BACKUP_FILE}"
fi

# 3. 빌드 및 배포
echo "==> Docker 이미지 빌드..."
docker compose -f "$COMPOSE_FILE" build

echo "==> 서비스 시작..."
docker compose -f "$COMPOSE_FILE" up -d

# 4. 헬스체크 대기
echo "==> 헬스체크 대기..."
MAX_RETRIES=30
RETRY=0
HEALTH_URL="${NEXTAUTH_URL:-http://localhost:3000}"
BACK_HEALTH="${BACKEND_URL:-http://localhost:8080}/api/v1/health"

until curl -sf "$BACK_HEALTH" | grep -q '"status":"UP"'; do
  RETRY=$((RETRY + 1))
  if [ "$RETRY" -ge "$MAX_RETRIES" ]; then
    echo "[ERROR] 헬스체크 실패"
    docker compose -f "$COMPOSE_FILE" logs app --tail 50
    exit 1
  fi
  sleep 2
done

echo "==> 헬스체크 성공!"

# 5. 시드 (최초 배포 시 — 호스트에서 DB에 직접 연결)
if [ "${RUN_SEED:-false}" = "true" ]; then
  echo "==> 시드 데이터 적용..."
  set -a && source .env && set +a
  npm run db:seed -w back || echo "    시드 스킵"
fi

echo "=========================================="
echo " 배포 완료"
echo "  Front: ${NEXTAUTH_URL:-http://localhost:3000}"
echo "  Back:  ${BACKEND_URL:-http://localhost:8080}"
echo "=========================================="
