-- Hibernate @Lob + String 은 PostgreSQL에서 oid(Large Object)로 생성될 수 있다.
-- Entity는 TEXT를 기대하므로(ddl-auto=validate) 컬럼을 TEXT로 맞춘다.
-- 포트폴리오 DB이므로 기존 LOB 참조 값은 버리고 컬럼을 재생성한다.

ALTER TABLE uploaded_file
    DROP COLUMN IF EXISTS file_content;

ALTER TABLE uploaded_file
    ADD COLUMN file_content TEXT;

ALTER TABLE verification_data
    DROP COLUMN IF EXISTS code;

ALTER TABLE verification_data
    ADD COLUMN code TEXT;
