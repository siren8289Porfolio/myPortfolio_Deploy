#!/usr/bin/env bash
# Public IP로 7개 앱 HTTP 200 확인 (Mac 또는 EC2에서 실행)
set -euo pipefail

HOST="${1:?Usage: $0 <EC2_PUBLIC_IP>}"
PATHS=(mido legacy pivot allohub if golmok briefly)

echo ">>> http://${HOST} 헬스 체크"
failed=0
for p in "${PATHS[@]}"; do
  url="http://${HOST}/${p}/"
  code=$(curl -s -o /dev/null -w '%{http_code}' --connect-timeout 10 "${url}" || echo "000")
  if [[ "${code}" == "200" ]]; then
    echo "  OK  ${url} -> ${code}"
  else
    echo "  FAIL ${url} -> ${code}"
    failed=$((failed + 1))
  fi
done

if [[ "${failed}" -gt 0 ]]; then
  echo ">>> ${failed}개 URL 실패. docker logs / docker compose logs 순으로 확인하세요."
  exit 1
fi
echo ">>> 모든 URL HTTP 200 — Phase 3 완료 조건 충족."
