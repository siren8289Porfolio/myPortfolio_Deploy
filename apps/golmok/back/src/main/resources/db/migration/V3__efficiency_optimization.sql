-- 데이터 효율화: 무결성 제약 → 인덱스 최적화 → 집계 Materialized View

-- 1. 데이터 품질 CHECK 제약
ALTER TABLE users
    ADD CONSTRAINT chk_users_role CHECK (role IN ('USER', 'ADMIN'));

ALTER TABLE stories
    ADD CONSTRAINT chk_stories_status CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED', 'HIDDEN', 'DELETED')),
    ADD CONSTRAINT chk_stories_like_count CHECK (like_count >= 0),
    ADD CONSTRAINT chk_stories_year CHECK (year BETWEEN 1900 AND 2100);

ALTER TABLE tags
    ADD CONSTRAINT chk_tags_type CHECK (type IN ('PERIOD', 'THEME', 'CUSTOM'));

ALTER TABLE reports
    ADD CONSTRAINT chk_reports_status CHECK (status IN ('RECEIVED', 'REVIEWED', 'RESOLVED'));

ALTER TABLE reactions
    ADD CONSTRAINT chk_reactions_type CHECK (type IN ('LIKE'));

-- 2. 중복 방지·조회 키
CREATE UNIQUE INDEX IF NOT EXISTS idx_regions_slug ON regions (slug);

-- 3. 기존 범용 인덱스 → 부분·복합 인덱스로 교체
DROP INDEX IF EXISTS idx_stories_status;

CREATE INDEX idx_stories_approved_region
    ON stories (region_id, like_count DESC)
    WHERE deleted_at IS NULL AND status = 'APPROVED';

CREATE INDEX idx_stories_approved_latest
    ON stories (created_at DESC)
    WHERE deleted_at IS NULL AND status = 'APPROVED';

CREATE INDEX idx_stories_approved_popular
    ON stories (like_count DESC, created_at DESC)
    WHERE deleted_at IS NULL AND status = 'APPROVED';

CREATE INDEX idx_stories_pending_admin
    ON stories (created_at DESC)
    WHERE deleted_at IS NULL AND status = 'PENDING';

CREATE INDEX idx_reports_received
    ON reports (created_at DESC)
    WHERE status = 'RECEIVED';

CREATE INDEX idx_story_images_story_sort
    ON story_images (story_id, sort_order);

CREATE INDEX idx_story_tags_tag_story
    ON story_tags (tag_id, story_id);

CREATE INDEX idx_reactions_story
    ON reactions (story_id);

CREATE INDEX idx_audit_logs_created
    ON audit_logs (created_at DESC);

-- 4. 지역별 집계 (Dimension + Fact 요약)
CREATE MATERIALIZED VIEW mv_region_story_stats AS
SELECT
    r.id   AS region_id,
    r.name AS region_name,
    r.slug AS region_slug,
    COUNT(s.id) FILTER (WHERE s.status = 'APPROVED' AND s.deleted_at IS NULL) AS approved_count,
    COUNT(s.id) FILTER (WHERE s.status = 'PENDING' AND s.deleted_at IS NULL)   AS pending_count,
    COALESCE(SUM(s.like_count) FILTER (WHERE s.status = 'APPROVED' AND s.deleted_at IS NULL), 0) AS total_likes,
    MAX(s.updated_at) FILTER (WHERE s.deleted_at IS NULL) AS last_story_updated_at
FROM regions r
LEFT JOIN stories s ON s.region_id = r.id
GROUP BY r.id, r.name, r.slug;

CREATE UNIQUE INDEX idx_mv_region_story_stats_region ON mv_region_story_stats (region_id);

-- 5. 큐레이션용 사전 조인 집계 뷰 (목록 API I/O 절감)
CREATE MATERIALIZED VIEW mv_story_curation_summary AS
SELECT
    s.id,
    s.title,
    s.content,
    s.like_count,
    s.created_at,
    s.year,
    u.nickname  AS author_nickname,
    u.email     AS author_email,
    r.id        AS region_id,
    r.name      AS region_name,
    p.name      AS place_name,
    (SELECT si.image_url
     FROM story_images si
     WHERE si.story_id = s.id
     ORDER BY si.sort_order ASC
     LIMIT 1) AS image_url,
    (SELECT t.name
     FROM story_tags st
     JOIN tags t ON t.id = st.tag_id
     WHERE st.story_id = s.id AND t.type = 'THEME'
     LIMIT 1) AS category,
    (SELECT t.name
     FROM story_tags st
     JOIN tags t ON t.id = st.tag_id
     WHERE st.story_id = s.id AND t.type = 'PERIOD'
     LIMIT 1) AS period_tag
FROM stories s
JOIN users u ON u.id = s.user_id
JOIN regions r ON r.id = s.region_id
LEFT JOIN places p ON p.id = s.place_id
WHERE s.deleted_at IS NULL AND s.status = 'APPROVED';

CREATE UNIQUE INDEX idx_mv_story_curation_summary_id ON mv_story_curation_summary (id);
CREATE INDEX idx_mv_story_curation_likes ON mv_story_curation_summary (like_count DESC);
CREATE INDEX idx_mv_story_curation_latest ON mv_story_curation_summary (created_at DESC);
CREATE INDEX idx_mv_story_curation_region ON mv_story_curation_summary (region_id);
CREATE INDEX idx_mv_story_curation_category ON mv_story_curation_summary (category);
CREATE INDEX idx_mv_story_curation_period ON mv_story_curation_summary (period_tag);

REFRESH MATERIALIZED VIEW mv_region_story_stats;
REFRESH MATERIALIZED VIEW mv_story_curation_summary;
