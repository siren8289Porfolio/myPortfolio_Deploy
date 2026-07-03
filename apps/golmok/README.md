# 다시, 골목 (Dasi Golmok)

> 지역 기억 기반 뉴트로 아카이빙 플랫폼

## 프로젝트 구조

```
다시, 골목/
├── front/          # 프론트엔드 (Next.js, port 3000)
│   ├── src/app/    # 페이지 UI
│   └── src/components/
├── back/           # 백엔드 API (Next.js, port 8080)
│   ├── src/app/api/v1/   # REST API
│   ├── src/domain/       # Service / Repository
│   ├── src/global/       # Auth, DB, Storage
│   └── prisma/           # DB Schema
├── docker-compose.yml
└── package.json    # Monorepo workspace
```

## 시작하기

### 1. PostgreSQL 실행
```bash
docker compose -f docker-compose.dev.yml up -d
```

### 2. 환경변수 설정
```bash
cp .env.example .env
cp back/.env.example back/.env
cp front/.env.example front/.env.local
```

### 3. 설치 및 DB 초기화
```bash
npm install
npm run db:setup
```

### 4. 개발 서버 (프론트 + 백 동시 실행)
```bash
npm run dev
```

- 프론트: http://localhost:3000
- 백엔드 API: http://localhost:8080
- 프론트는 `/api/v1/*` 요청을 백엔드로 프록시합니다

## 테스트 계정

| 역할 | 이메일 | 비밀번호 |
| --- | --- | --- |
| 관리자 | admin@dasi-golmok.kr | admin1234 |
| 일반 사용자 | user@example.com | user1234 |

## Docker 프로덕션 배포

```bash
cp .env.production.example .env
npm run deploy
npm run health-check
```

자세한 내용: [DEPLOYMENT.md](./DEPLOYMENT.md)

## 라이선스

Private — 정예림
