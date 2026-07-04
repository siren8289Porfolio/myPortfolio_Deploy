-- MIDO DB 효율화: status 컬럼 + 조회 패턴별 인덱스
-- prod(prod profile, ddl-auto=validate)에서 Flyway로 적용

ALTER TABLE verification_data
    ADD COLUMN IF NOT EXISTS status VARCHAR(32) NOT NULL DEFAULT 'DRAFT';

ALTER TABLE work_context
    ADD COLUMN IF NOT EXISTS display_input_type VARCHAR(32);

CREATE INDEX IF NOT EXISTS idx_verification_status_created
    ON verification_data (status, created_at DESC);

CREATE INDEX IF NOT EXISTS idx_verification_input_type_created
    ON verification_data (input_type, created_at DESC);

CREATE UNIQUE INDEX IF NOT EXISTS uq_work_context_verification
    ON work_context (verification_data_id);

CREATE INDEX IF NOT EXISTS idx_uploaded_file_verification_uploaded
    ON uploaded_file (verification_data_id, uploaded_at DESC);

CREATE INDEX IF NOT EXISTS idx_manual_input_verification
    ON manual_input (verification_data_id);

CREATE INDEX IF NOT EXISTS idx_verification_draft_created
    ON verification_data (created_at DESC)
    WHERE status = 'DRAFT';
