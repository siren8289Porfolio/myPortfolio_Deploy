# 다시, 골목 — 데이터베이스

## 기술 스택 선정 이유

| 구성 | 선택 | 이유 |
|------|------|------|
| **DBMS** | PostgreSQL 16 | 사용자·스토리·지역·태그·신고·감사로그 등 **관계형 데이터**가 핵심이며, FK·트랜잭션·동시성이 필요함. Spring Data JPA와 운영 환경에서 검증된 조합. |
| **마이그레이션** | Flyway | 스키마를 **버전 관리된 SQL**로 관리해 팀 협업·배포 시 `ddl-auto: update`의 비가역 변경 위험을 제거. |
| **ORM** | Spring Data JPA | 백엔드 레이어드 아키텍처와 맞고, 엔티티 기반 도메인 모델 유지에 적합. |
| **로컬 실행** | Docker Compose | OS에 PostgreSQL을 직접 설치하지 않고 **동일한 DB 버전**으로 개발 가능. |

## 디렉터리 구조

```
back/
├── docker-compose.yml          # 로컬 PostgreSQL
├── db/
│   └── README.md               # 이 문서
├── scripts/
│   ├── db-up.sh                # DB 컨테이너 기동
│   └── db-down.sh              # DB 컨테이너 중지
└── src/main/resources/db/migration/
    ├── V1__init_schema.sql              # 테이블·인덱스 생성
    ├── V2__seed_data.sql                # 기본 시드
    ├── V3__efficiency_optimization.sql  # 제약·인덱스·Materialized View
    └── V4__mock_data_extended.sql       # 확장 목업 데이터
```

## 빠른 시작

```bash
# 1. DB 컨테이너 실행
cd back
docker compose up -d

# 2. 환경변수 (선택)
cp .env.example .env

# 3. 백엔드 실행 — Flyway가 마이그레이션 자동 적용
./gradlew bootRun
```

루트에서 실행:

```bash
npm run db:up      # back/docker-compose.yml 사용
npm run dev:back   # 마이그레이션 후 API 기동
```

## 연결 정보 (기본값)

| 항목 | 값 |
|------|-----|
| Host | `localhost` |
| Port | `5432` |
| Database | `dasi_golmok` |
| User | `app_user` |
| Password | `app_password` |
| JDBC URL | `jdbc:postgresql://localhost:5432/dasi_golmok` |

## 시드 계정

| 역할 | 이메일 | 비밀번호 |
|------|--------|----------|
| 관리자 | admin@dasi-golmok.kr | admin1234 |
| 사용자 | user@example.com | user1234 |

## 마이그레이션 규칙

- 파일명: `V{버전}__{설명}.sql` (예: `V3__add_story_view_count.sql`)
- 스키마 변경은 **반드시 새 마이그레이션 파일**로 추가
- `ddl-auto`는 `validate` — Hibernate가 Flyway 스키마와 일치하는지 검증만 수행

## 데이터 효율화 (적용 내역)

| 단계 | 적용 내용 |
|------|-----------|
| 1. 모델·무결성 | CHECK 제약(상태값·역할·연도), `regions.slug` UNIQUE |
| 2. 인덱스 | 부분 인덱스(`PENDING`/`APPROVED`/`RECEIVED`), 복합 인덱스 |
| 3. 집계 뷰 | `mv_region_story_stats`, `mv_story_curation_summary` |
| 4. 쿼리 | 지역 필터 SQL 이관, `@EntityGraph` + `@BatchSize`로 N+1 완화 |
| 5. 목록 API | 큐레이션 MV 조회, 관리자 목록 `Pageable` 상한(100건) |
| 6. 갱신 | 스토리 등록·승인·좋아요 시 MV `CONCURRENTLY` 리프레시 |

### 실행 계획 확인

```sql
EXPLAIN ANALYZE
SELECT id, title, like_count
FROM mv_story_curation_summary
ORDER BY like_count DESC
LIMIT 12;
```

### 지역 통계 API

`GET /api/v1/analytics/regions` — 지역별 승인/대기/좋아요 집계

### 확장 목업 (V4)

- 추가 사용자 2명, 승인 스토리 4건
- 검수 대기(PENDING) 3건 — 관리자 페이지 테스트용
- 신고 3건, 좋아요 반응, 숨김 스토리 1건

```bash
npm run db:reset   # 볼륨 삭제 후 재기동 → V1~V4 전체 적용
```
