-- EC2 등 기존 DB에 flyway baseline(version=1)만 적용된 경우 V1이 스킵되므로
-- 동일한 변경을 V2로 재적용한다. 모든 문장은 idempotent.
-- baseline-version: 0 적용 후 신규 환경에서는 V1만 실행되고 V2는 no-op에 가깝다.

ALTER TABLE verification_data
    ADD COLUMN IF NOT EXISTS status VARCHAR(32) NOT NULL DEFAULT 'DRAFT';

ALTER TABLE work_context
    ADD COLUMN IF NOT EXISTS display_input_type VARCHAR(32);

CREATE INDEX IF NOT EXISTS idx_verification_status_created
    ON verification_data (status, created_at DESC);

CREATE INDEX IF NOT EXISTS idx_verification_input_type_created
    ON verification_data (input_type, created_at DESC);

-- 1:1 제약 전 중복 행 제거(레거시 데이터 호환)
DELETE FROM work_context a
    USING work_context b
WHERE a.verification_data_id = b.verification_data_id
  AND a.ctid > b.ctid;

CREATE UNIQUE INDEX IF NOT EXISTS uq_work_context_verification
    ON work_context (verification_data_id);

CREATE INDEX IF NOT EXISTS idx_uploaded_file_verification_uploaded
    ON uploaded_file (verification_data_id, uploaded_at DESC);

CREATE INDEX IF NOT EXISTS idx_manual_input_verification
    ON manual_input (verification_data_id);

CREATE INDEX IF NOT EXISTS idx_verification_draft_created
    ON verification_data (created_at DESC)
    WHERE status = 'DRAFT';
