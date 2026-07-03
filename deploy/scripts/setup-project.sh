#!/usr/bin/env bash
# EC2에서 my-portfolio 클론 및 구조 검증
set -euo pipefail

REPO_URL="${REPO_URL:-https://github.com/siren8289Porfolio/myPortfolio_Deploy.git}"
TARGET_DIR="${TARGET_DIR:-$HOME/my-portfolio}"

if [[ -d "${TARGET_DIR}/.git" ]]; then
  echo "Already exists: ${TARGET_DIR}"
  cd "${TARGET_DIR}"
  git pull --ff-only
else
  git clone "${REPO_URL}" "${TARGET_DIR}"
  cd "${TARGET_DIR}"
fi

for dir in apps deploy docs infra; do
  if [[ ! -d "${TARGET_DIR}/${dir}" ]]; then
    echo "ERROR: missing ${dir}/"
    exit 1
  fi
done

if [[ ! -f "${TARGET_DIR}/deploy/docker-compose.yml" ]]; then
  echo "ERROR: missing deploy/docker-compose.yml"
  exit 1
fi

cp -n "${TARGET_DIR}/deploy/.env.example" "${TARGET_DIR}/deploy/.env" 2>/dev/null || true

echo "=== project structure OK ==="
ls -la "${TARGET_DIR}"
