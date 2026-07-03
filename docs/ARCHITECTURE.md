# Architecture

## 목표 스택

```
Mac (Local)
    ↓ Docker Desktop
    ↓ Docker Compose
    ↓ Nginx + Spring Boot × 7
    ↓ GitHub
    ↓ AWS EC2 (동일 Compose)
    ↓ GitHub Actions
    ↓ HTTPS (Route53 + ACM/Certbot)
    ↓ CloudWatch
    ↓ k3s → Amazon EKS
```

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
| `apps/` | 애플리케이션 소스만 |
| `deploy/` | Compose, Nginx, scripts, .env |
| `docs/` | 문서 |
| `infra/` | Terraform, Helm, EKS (미래) |
