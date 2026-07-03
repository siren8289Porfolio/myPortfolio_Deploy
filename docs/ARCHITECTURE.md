# Architecture

## 목표 스택

```
Developer
    ↓ Git Push (main)
    ↓ GitHub Actions — Build & Push (7 images)
    ↓ GitHub Container Registry (GHCR)
    ↓ AWS EC2 — docker compose pull / up -d
    ↓ Nginx :80
    ↓ Spring Boot × 7 (+ Briefly Tomcat)
    ↓ (Phase 5+) HTTPS, CloudWatch, k3s → EKS
```

## CI/CD 배포 흐름 (Phase 4+)

```
┌─────────────┐     ┌──────────────────┐     ┌─────────────┐
│ Git Push    │────▶│ GitHub Actions   │────▶│ GHCR        │
│ main        │     │ build-push-action│     │ ghcr.io/... │
└─────────────┘     └────────┬─────────┘     └──────┬──────┘
                             │ SSH                 │ pull
                             ▼                     ▼
                      ┌──────────────────────────────────┐
                      │ EC2 — deploy/scripts/deploy.sh   │
                      │  git pull → GHCR login → pull    │
                      │  → compose up -d (no build)      │
                      └──────────────────────────────────┘
```

- **EC2에서는 Docker build를 수행하지 않음** — 이미지는 Actions runner에서 빌드
- 로컬 Mac(Phase 1)은 `docker-compose.local.yml`로 소스 빌드 유지

## GHCR 이미지

| 서비스 | 이미지 |
|--------|--------|
| mido-app | `ghcr.io/<owner>/portfolio-mido:latest` |
| legacy-app | `ghcr.io/<owner>/portfolio-legacy:latest` |
| pivot-app | `ghcr.io/<owner>/portfolio-pivot:latest` |
| allohub-app | `ghcr.io/<owner>/portfolio-allohub:latest` |
| if-app | `ghcr.io/<owner>/portfolio-if:latest` |
| golmok-app | `ghcr.io/<owner>/portfolio-golmok:latest` |
| briefly-app | `ghcr.io/<owner>/portfolio-briefly:latest` |

`<owner>` = GitHub 사용자/조직명 소문자 (예: `siren8289porfolio`)

## Compose 서비스 (8개)

| 서비스 | 역할 | 외부 포트 |
|--------|------|-----------|
| `portfolio-nginx` | Reverse Proxy | 80 |
| `mido-app` | Mido API | 내부 8080 |
| `legacy-app` | LegacyPoc API | 내부 8080 |
| `pivot-app` | pivotSeoul API | 내부 8080 |
| `allohub-app` | AlloHub API | 내부 8080 |
| `if-app` | IF API | 내부 8080 |
| `golmok-app` | golmok API | 내부 8080 |
| `briefly-app` | Briefly (Tomcat WAR) | 내부 8080 |

## Nginx 라우팅

| Path | Upstream |
|------|----------|
| `/mido/` | `mido-app:8080/mido/` |
| `/legacy/` | `legacy-app:8080/legacy/` |
| `/pivot/` | `pivot-app:8080/pivot/` |
| `/allohub/` | `allohub-app:8080/allohub/` |
| `/if/` | `if-app:8080/if/` |
| `/golmok/` | `golmok-app:8080/golmok/` |
| `/briefly/` | `briefly-app:8080/briefly/` |
| `/health` | Nginx 직접 응답 `200 ok` |

## 네트워크

- 단일 bridge: `portfolio-network`
- 앱 포트는 `expose`만 사용 (외부는 Nginx 80만 개방)

## DB 전략

- **local profile:** H2 / SQLite in-memory — Docker Compose 로컬 실행
- **prod profile:** PostgreSQL — AWS RDS (`SPRING_DATASOURCE_*` 환경변수)
- **Briefly:** `db.properties`(local) + `DB_URL` 환경변수(prod)

## 디렉터리 책임

| 경로 | 역할 |
|------|------|
| `apps/` | 애플리케이션 소스 + Dockerfile |
| `deploy/` | Compose, Nginx, scripts, .env |
| `.github/workflows/` | CI/CD (GHCR build + EC2 deploy) |
| `docs/` | 문서 |
| `infra/` | Terraform, Helm, EKS (미래) |
