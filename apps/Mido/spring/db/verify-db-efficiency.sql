-- MIDO DB 효율화 검증
-- 사용: PGPASSWORD=... psql -h localhost -U postgres -d mido -f db/verify-db-efficiency.sql

\echo '=== 1) 목록: status + created_at DESC LIMIT 20 ==='
EXPLAIN (ANALYZE, BUFFERS)
SELECT id, input_type, status, created_at
FROM verification_data
ORDER BY created_at DESC
LIMIT 20;

\echo ''
\echo '=== 2) DRAFT partial index ==='
EXPLAIN (ANALYZE, BUFFERS)
SELECT id, input_type, status, created_at
FROM verification_data
WHERE status = 'DRAFT'
ORDER BY created_at DESC
LIMIT 20;

\echo ''
\echo '=== 3) work_context FK lookup ==='
EXPLAIN (ANALYZE, BUFFERS)
SELECT id, display_input_type
FROM work_context
WHERE verification_data_id = (SELECT id FROM verification_data LIMIT 1);
