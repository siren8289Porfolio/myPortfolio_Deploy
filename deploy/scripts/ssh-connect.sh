#!/usr/bin/env bash
# Mac에서 EC2 SSH 접속
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

if [[ ! -f "${SSH_KEY}" ]]; then
  echo "SSH key not found: ${SSH_KEY}"
  exit 1
fi

chmod 400 "${SSH_KEY}"
exec ssh -i "${SSH_KEY}" -o StrictHostKeyChecking=accept-new "${EC2_USER}@${EC2_HOST}"
