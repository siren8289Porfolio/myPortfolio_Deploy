# 다시, 골목 — Deployment Plan

> MVP v0.1.0 배포 계획서 (Next.js 풀스택 기준)

## 1. 배포 범위

| 항목 | 내용 |
| --- | --- |
| 배포 대상 | Frontend + Backend (Next.js Monolith), PostgreSQL, Image Storage |
| 배포 버전 | v0.1.0 |
| 배포 환경 | Local / Dev / Production |
| 배포 방식 | Docker Compose / GitHub Actions / Vercel(선택) |

## 2. 시스템 구성 (MVP 구현)

| 구성요소 | Deployment Plan | MVP 구현 |
| --- | --- | --- |
| Frontend + Backend | 분리 | Next.js 15 (단일 앱) |
| Database | PostgreSQL / RDS | PostgreSQL (Docker) |
| Storage | S3 | `/public/uploads` (볼륨 마운트) |
| CI/CD | GitHub Actions | `.github/workflows/deploy.yml` |
| Health Check | `/actuator/health` | `/api/v1/health` |
| API 문서 | Swagger | README API 표 |

## 3. 배포 아키텍처

```
Developer → GitHub → GitHub Actions (Build/Test)
                          ↓
                    Docker Image (dasi-golmok:v0.1.0)
                          ↓
              ┌───────────┴───────────┐
              │   docker-compose.yml   │
              │  ┌─────┐   ┌───────┐  │
              │  │ App │───│  DB   │  │
              │  │:3000│   │Postgres│ │
              │  └─────┘   └───────┘  │
              └───────────────────────┘
                          ↓
                       User
```

## 4. 환경변수

| 변수명 | 설명 | 필수 |
| --- | --- | --- |
| `DATABASE_URL` | PostgreSQL 연결 문자열 | Y |
| `NEXTAUTH_SECRET` | 인증 Secret (≈ JWT_SECRET) | Y |
| `NEXTAUTH_URL` | 서비스 URL (≈ CORS Origin) | Y |
| `DB_HOST` / `DB_PORT` / `DB_NAME` | DB 접속 정보 | Y (Docker) |
| `DB_USERNAME` / `DB_PASSWORD` | DB 인증 | Y (Docker) |
| `APP_VERSION` | 배포 버전 | N |
| `RUN_SEED` | 최초 배포 시 시드 실행 | N |

설정 파일:
- 로컬 개발: `.env.example`
- 프로덕션: `.env.production.example`

## 5. 배포 절차

### 5.1 로컬 개발 (DB만 Docker)

```bash
# 1. PostgreSQL 시작
docker compose -f docker-compose.dev.yml up -d

# 2. 환경변수 설정
cp .env.example .env

# 3. DB 초기화
npm run db:setup

# 4. 개발 서버
npm run dev
```

### 5.2 Docker 프로덕션 배포

```bash
# 1. 환경변수 준비
cp .env.production.example .env
# CHANGE_ME 값을 실제 값으로 교체

# 2. 배포 실행
chmod +x scripts/*.sh
npm run deploy

# 3. 배포 후 검증
npm run health-check
```

### 5.3 수동 Docker 배포

```bash
docker build -t dasi-golmok:v0.1.0 .
docker compose up -d
curl http://localhost:3000/api/v1/health
```

### 5.4 DB 배포

| Step | 작업 | 명령어 |
| --- | --- | --- |
| 1 | DB 백업 | `scripts/deploy.sh` 내 자동 수행 |
| 2 | Schema 적용 | 컨테이너 entrypoint에서 `prisma db push` |
| 3 | Seed | `RUN_SEED=true npm run deploy` |
| 4 | 검증 | `npm run health-check` |

## 6. CI/CD Pipeline

```
Code Push → Checkout → Install → Prisma Generate → DB Push
    → Lint → Build → Start Server → Health Check → API Smoke Test
    → Docker Build (main only)
```

워크플로: `.github/workflows/deploy.yml`

## 7. 배포 후 검증

```bash
npm run health-check http://localhost:3000
```

| 검증 항목 | Endpoint | 기대 결과 |
| --- | --- | --- |
| 서버 상태 | `GET /api/v1/health` | `status: UP` |
| 지역 조회 | `GET /api/v1/regions` | `success: true` |
| 지도 조회 | `GET /api/v1/stories/map` | `success: true` |
| 큐레이션 | `GET /api/v1/stories/curation` | `success: true` |

## 8. 롤백 계획

### 롤백 기준 (즉시 롤백)

- 서비스 접속 불가
- 로그인 / 스토리 등록 / 지도 조회 불가
- DB Migration 실패
- 이미지 업로드 전체 실패

### 롤백 절차

```bash
# 이전 Docker 이미지 태그 지정 후
ROLLBACK_VERSION=v0.0.9 ./scripts/rollback.sh
```

## 9. 배포 전 체크리스트

- [ ] P0 테스트케이스 Pass
- [ ] Critical Bug 0건
- [ ] `.env` Secret 값 설정 완료 (코드에 노출 없음)
- [ ] `NEXTAUTH_SECRET` 프로덕션용 랜덤 값 설정
- [ ] DB 백업 절차 확인
- [ ] 롤백 이미지 태그 확보
- [ ] `npm run health-check` 통과

## 10. 배포 이력

| 버전 | 배포일 | 배포자 | 변경 내용 | 결과 |
| --- | --- | --- | --- | --- |
| v0.1.0 | 2026-07-03 | 정예림 | MVP 최초 배포 인프라 | 예정 |

## 11. Vercel 배포 (선택)

Frontend만 Vercel에 배포할 경우:

1. Vercel에 GitHub 연동
2. Environment Variables 설정 (`DATABASE_URL`, `NEXTAUTH_*`)
3. Build Command: `prisma generate && next build`
4. 외부 PostgreSQL (Neon, Supabase, RDS) 연결 필요

> MVP 권장: Docker Compose로 App + DB 통합 배포
