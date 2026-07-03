# Deployment Guide

AWS EC2 + Docker Compose + Nginx + GitHub Actions 기준 배포 문서입니다.

## 아키텍처 개요

```
Internet
    ↓
[Nginx :80]  ← portfolio-nginx
    ↓ path routing
[mido-app | legacy-app | pivot-app | allohub-app | if-app | golmok-app | briefly-app]
```

- DB 컨테이너 없음 (Oracle 제거)
- 로컬 Mac과 EC2가 **동일한 `deploy/docker-compose.yml`** 사용

---

## Phase 1 — 로컬 (Mac)

### 사전 요구

- Docker Desktop
- Apple Silicon: 공식 이미지 멀티 아키텍처 지원

### 실행

```bash
cd deploy
cp .env.example .env
./scripts/start.sh          # docker compose up -d --build
```

### 확인

```bash
curl -I http://localhost/health
curl -I http://localhost/mido/
curl -I http://localhost/legacy/
```

### 중지

```bash
./scripts/stop.sh           # docker compose down
./scripts/restart.sh
```

### 앱 단독 빌드 (권장 순서)

Compose 전에 앱별 빌드로 오류를 분리합니다.

```bash
# Gradle 예: AlloHub
cd apps/AlloHub/back && ./gradlew clean build -x test

# Docker 예
cd apps/AlloHub && docker build -t allohub:test .
```

---

## Phase 2 — AWS EC2 초기 설정

### EC2 스펙 (권장)

- Ubuntu 22.04 LTS
- t3.medium 이상 (앱 7개 빌드·실행)
- Security Group: 22 (SSH), 80 (HTTP), 443 (HTTPS, Phase 5)

### SSH (Mac)

```bash
chmod 400 ~/Downloads/portfolio-key.pem
```

`deploy/.env`:

```env
EC2_PUBLIC_IP=your.public.ip
SSH_KEY_PATH=~/Downloads/portfolio-key.pem
EC2_USER=ubuntu
```

```bash
cd deploy
./scripts/ssh-connect.sh
```

### 서버 패키지 설치

EC2에서:

```bash
sudo bash deploy/scripts/server-init.sh
```

설치: `apt update`, `git`, `docker`, `docker compose`

---

## Phase 3 — EC2 배포

### 수동

```bash
# EC2
bash deploy/scripts/setup-project.sh
cd ~/my-portfolio/deploy
cp .env.example .env
./scripts/deploy.sh
```

Public IP 접속: `http://<EC2_PUBLIC_IP>/mido`

### Mac에서 원격 일괄 부트스트랩

```bash
cd deploy
./scripts/remote-bootstrap.sh
```

---

## Phase 4 — GitHub Actions (자동 배포)

`.github/workflows/deploy-ec2.yml` 참고.

흐름:

1. `main` push
2. SSH로 EC2 접속
3. `git pull` + `deploy/scripts/deploy.sh`

필요 GitHub Secrets:

- `EC2_HOST` — Public IP 또는 Elastic IP
- `EC2_USER` — `ubuntu`
- `EC2_SSH_KEY` — PEM private key 전체

---

## Phase 5 — Elastic IP / Route53 / HTTPS

- Elastic IP를 EC2에 연결
- Route53 A 레코드 → Elastic IP
- Certbot 또는 ACM + ALB로 HTTPS (Nginx `443` 설정 추가)

---

## Phase 6 — CloudWatch

- EC2에 CloudWatch Agent 설치
- Docker 로그 → `/aws/ec2/portfolio`
- 알람: CPU, 5xx, 디스크

---

## Phase 7 — k3s → Amazon EKS

- `infra/` 에 Helm / Terraform 마이그레이션
- Compose → Kubernetes 매니페스트 전환

---

## Spring Profile (local / prod)

각 Spring Boot 앱은 프로파일로 DB를 분리합니다.

| Profile | DB | 용도 |
|---------|-----|------|
| `local` | H2 / SQLite (in-memory) | Mac Docker Compose |
| `prod` | PostgreSQL (RDS) | AWS EC2 |

```
application.yml        # 공통 설정 (server, actuator 등)
application-local.yml  # local profile — H2/SQLite
application-prod.yml   # prod profile — PostgreSQL (env 변수)
```

로컬:

```env
SPRING_PROFILE=local
```

EC2 / RDS:

```env
SPRING_PROFILE=prod
SPRING_DATASOURCE_URL=jdbc:postgresql://your-rds:5432/dbname
SPRING_DATASOURCE_USERNAME=...
SPRING_DATASOURCE_PASSWORD=...
```

Briefly(Tomcat)는 `db.properties`(local) + 환경변수 `DB_URL`(prod)로 동일하게 분리합니다.

---

## 환경변수 (`deploy/.env`)

| 변수 | 설명 |
|------|------|
| `SPRING_PROFILE` | `local` (H2/SQLite) 또는 `prod` (PostgreSQL/RDS) |
| `SPRING_DATASOURCE_URL` | prod 전용 — RDS JDBC URL |
| `SPRING_DATASOURCE_USERNAME` | prod 전용 |
| `SPRING_DATASOURCE_PASSWORD` | prod 전용 |
| `TZ` | 타임존 |
| `NGINX_HTTP_PORT` | Nginx 호스트 포트 (기본 80) |
| `EC2_PUBLIC_IP` | EC2 공인 IP |
| `SSH_KEY_PATH` | SSH 키 경로 |
| `REPO_URL` | Git clone URL |
| `PROJECT_DIR` | EC2 프로젝트 경로 |

---

## 트러블슈팅

| 증상 | 확인 |
|------|------|
| 502 Bad Gateway | `docker logs <app>-app` — 앱 기동 대기 |
| 빌드 실패 | 앱 단독 `docker build` 먼저 |
| 포트 충돌 | `NGINX_HTTP_PORT` 변경 또는 80 점유 프로세스 종료 |

```bash
docker compose ps
docker compose logs -f nginx
docker compose logs -f mido-app
```
