#!/usr/bin/env bash
set -euo pipefail

BASE_URL="${1:-http://localhost:3000}"
BACK_URL="${2:-http://localhost:8080}"
FAILED=0

check() {
  local name="$1"
  local url="$2"
  local expect="$3"
  echo -n "  [$name] $url ... "
  RESPONSE=$(curl -sf "$url" 2>/dev/null) || { echo "FAIL"; FAILED=1; return; }
  if echo "$RESPONSE" | grep -q "$expect"; then
    echo "OK"
  else
    echo "FAIL"
    FAILED=1
  fi
}

echo "=========================================="
echo " 배포 후 검증"
echo "  Front: $BASE_URL"
echo "  Back:  $BACK_URL"
echo "=========================================="

check "Back Health" "$BACK_URL/api/v1/health" '"status":"UP"'
check "Front Proxy Regions" "$BASE_URL/api/v1/regions" '"success":true'
check "Front Home" "$BASE_URL" "다시"

echo "=========================================="
if [ "$FAILED" -eq 0 ]; then echo " 모든 검증 통과"; exit 0; else echo " 일부 검증 실패"; exit 1; fi
