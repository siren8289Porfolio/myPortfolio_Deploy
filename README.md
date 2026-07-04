# my-portfolio

AWS EC2 기반 실무형 배포를 목표로 하는 포트폴리오 모노레포입니다.  
로컬과 EC2 모두 **Docker Compose + Nginx + Spring Boot** 동일 스택으로 실행합니다.

## 아키텍처

```
Local (Mac)
    ↓ Docker Compose + local build
    ↓
GitHub (git push main)
    ↓
GitHub Actions — Build & Push (7 images)
    ↓
GHCR (ghcr.io/<actor>/portfolio-*)
    ↓
AWS EC2 — docker compose pull / up -d (no build)
    ↓
Docker Compose
    ↓
Nginx (:80) — / → Portfolio Home · /mido … → Reverse Proxy
    ↓
Spring Boot × 7 (+ Briefly Tomcat)
```

| URL | 설명 |
|-----|------|
| `http://<host>/` | Portfolio 메인 (정적 `index.html`) |
| `http://<host>/mido` … | 각 프로젝트 Reverse Proxy |
| `http://<host>/health` | Nginx 헬스체크 `200 ok` |

## 구조

```
my-portfolio/
├── apps/       # Mido, LegacyPoc, pivotSeoul, AlloHub, IF, golmok, Briefly
├── deploy/     # docker-compose.yml, nginx, scripts, .env
├── docs/       # 배포·운영 문서
└── infra/      # Terraform / k8s / AWS (Phase 6+)
```

## Phase 1 — 로컬 실행

```bash
cd deploy
cp .env.example .env   # 최초 1회
./scripts/start.sh
```

| 프로젝트 | URL |
|---------|-----|
| Mido | http://localhost/mido |
| Legacy | http://localhost/legacy |
| Pivot | http://localhost/pivot |
| AlloHub | http://localhost/allohub |
| IF | http://localhost/if |
| Golmok | http://localhost/golmok |
| Briefly | http://localhost/briefly |

메인: `http://localhost/` (Portfolio Home)

헬스체크: `http://localhost/health`

```bash
./scripts/stop.sh
./scripts/restart.sh
```

## 배포 로드맵

| Phase | 내용 |
|-------|------|
| 1 | Docker Desktop, Compose, Nginx, Spring Boot, localhost |
| 2 | AWS EC2, Ubuntu, Docker, Git, Compose |
| 3 | EC2 Docker Compose 배포, Public IP 접속 |
| 4 | **GitHub Actions + GHCR** (`main` push → Build → GHCR → EC2 pull) |
| 5 | Elastic IP, Route53, HTTPS |
| 6 | CloudWatch |
| 7 | k3s → Amazon EKS |

## Phase 4 — CI/CD (GitHub Actions + GHCR)

```
Local → GitHub → GitHub Actions → GHCR → EC2 → Docker Compose → Nginx → Spring Boot × 7
```

- Actions: `docker/login-action` + `docker/build-push-action` → `ghcr.io/${{ github.actor }}/portfolio-*`
- EC2: `deploy.sh` — `git pull` → `compose pull` → `up -d` (build 없음)
- 루트 `/` : Portfolio Home (`portfolio/index.html`)

### Secrets (GitHub Repository Settings)

| Secret | 설명 |
|--------|------|
| `GHCR_USERNAME` | GitHub 사용자명 (EC2 pull 로그인) |
| `GHCR_TOKEN` | PAT (`read:packages`) — EC2 pull 전용 |
| `EC2_HOST` | Public IP |
| `EC2_USER` | `ubuntu` |
| `EC2_SSH_KEY` | PEM private key 전체 |
| `EC2_PORT` | (선택) `22` |

```bash
git push origin main   # Actions 자동 실행
```

로컬 개발은 `./scripts/start.sh` (`docker-compose.local.yml`로 build).

상세: [docs/DEPLOYMENT.md](docs/DEPLOYMENT.md) · [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md)

## 스택

- **Reverse Proxy:** Nginx
- **Backend:** Spring Boot (Java 17/21), Briefly(Tomcat WAR)
- **DB:** `local` profile → H2/SQLite · `prod` profile → PostgreSQL (RDS)
- **배포:** Docker Compose (로컬·EC2 동일)
