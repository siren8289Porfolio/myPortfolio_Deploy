-- EC2에 V2가 이미 적용된 뒤에는 V2 파일을 수정할 수 없으므로(Flyway checksum),
-- 레거시 work_context 중복 정리는 V3에서 idempotent하게 수행한다.

DELETE FROM work_context a
    USING work_context b
WHERE a.verification_data_id = b.verification_data_id
  AND a.ctid > b.ctid;

CREATE UNIQUE INDEX IF NOT EXISTS uq_work_context_verification
    ON work_context (verification_data_id);
