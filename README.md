# my-portfolio

AWS EC2 기반 실무형 배포를 목표로 하는 포트폴리오 모노레포입니다.  
로컬과 EC2 모두 **Docker Compose + Nginx + Spring Boot** 동일 스택으로 실행합니다.

## 아키텍처

```
Mac (Local)
  → Docker Desktop
  → Docker Compose
  → Nginx + Spring Boot × 7
  → GitHub
  → AWS EC2 (동일 Compose 스택)
  → GitHub Actions → HTTPS → CloudWatch → k3s → EKS
```

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
| 3 | GitHub Clone → `docker compose up -d` → Public IP |
| 4 | GitHub Actions 자동 배포 |
| 5 | Elastic IP, Route53, HTTPS |
| 6 | CloudWatch |
| 7 | k3s → Amazon EKS |

상세: [docs/DEPLOYMENT.md](docs/DEPLOYMENT.md)

## 스택

- **Reverse Proxy:** Nginx
- **Backend:** Spring Boot (Java 17/21), Briefly(Tomcat WAR)
- **DB:** 컨테이너 없음 — 앱 내장 DB / AWS RDS 등 Phase 5+ 에서 연결
- **배포:** Docker Compose (로컬·EC2 동일)
