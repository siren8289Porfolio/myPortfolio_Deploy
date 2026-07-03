#!/usr/bin/env bash
# Mac에서 EC2 원격 부트스트랩: server-init + clone + compose up
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
DEPLOY_DIR="$(cd "${SCRIPT_DIR}/.." && pwd)"

if [[ -f "${DEPLOY_DIR}/.env" ]]; then
  # shellcheck disable=SC1091
  set -a
  source "${DEPLOY_DIR}/.env"
  set +a
fi

SSH_KEY="${SSH_KEY_PATH:-$HOME/Downloads/portfolio-key.pem}"
EC2_HOST="${EC2_PUBLIC_IP:?Set EC2_PUBLIC_IP in deploy/.env}"
EC2_USER="${EC2_USER:-ubuntu}"
REPO_URL="${REPO_URL:-https://github.com/siren8289Porfolio/myPortfolio_Deploy.git}"
PROJECT_DIR="${PROJECT_DIR:-/home/ubuntu/my-portfolio}"

chmod 400 "${SSH_KEY}"

echo ">>> [1/3] server-init (apt, git, docker, docker compose)"
ssh -i "${SSH_KEY}" -o StrictHostKeyChecking=accept-new "${EC2_USER}@${EC2_HOST}" \
  "sudo bash -s" < "${SCRIPT_DIR}/server-init.sh"

echo ">>> [2/3] setup-project (git clone)"
ssh -i "${SSH_KEY}" "${EC2_USER}@${EC2_HOST}" \
  "REPO_URL='${REPO_URL}' TARGET_DIR='${PROJECT_DIR}' bash -s" < "${SCRIPT_DIR}/setup-project.sh"

echo ">>> [3/3] deploy (docker compose up -d --build)"
ssh -i "${SSH_KEY}" "${EC2_USER}@${EC2_HOST}" \
  "PROJECT_DIR='${PROJECT_DIR}' bash -s" < "${SCRIPT_DIR}/deploy.sh"

echo ">>> bootstrap complete: http://${EC2_HOST}/mido"
