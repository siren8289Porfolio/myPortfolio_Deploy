-- 다시, 골목 초기 스키마
-- PostgreSQL 16 + Flyway

CREATE TABLE users (
    id              VARCHAR(36)  PRIMARY KEY,
    email           VARCHAR(255) NOT NULL UNIQUE,
    password_hash   VARCHAR(255) NOT NULL,
    nickname        VARCHAR(255) NOT NULL,
    role            VARCHAR(50)  NOT NULL,
    created_at      TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE regions (
    id              VARCHAR(36)  PRIMARY KEY,
    name            VARCHAR(255) NOT NULL,
    slug            VARCHAR(255) NOT NULL,
    description     TEXT,
    created_at      TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE places (
    id              VARCHAR(36)  PRIMARY KEY,
    region_id       VARCHAR(36)  NOT NULL REFERENCES regions(id),
    name            VARCHAR(255) NOT NULL,
    address         VARCHAR(255) NOT NULL,
    latitude        DOUBLE PRECISION,
    longitude       DOUBLE PRECISION,
    created_at      TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE tags (
    id              VARCHAR(36)  PRIMARY KEY,
    name            VARCHAR(255) NOT NULL UNIQUE,
    type            VARCHAR(50)  NOT NULL,
    created_at      TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE stories (
    id              VARCHAR(36)  PRIMARY KEY,
    user_id         VARCHAR(36)  NOT NULL REFERENCES users(id),
    region_id       VARCHAR(36)  NOT NULL REFERENCES regions(id),
    place_id        VARCHAR(36)  REFERENCES places(id),
    title           VARCHAR(255) NOT NULL,
    content         TEXT         NOT NULL,
    year            INTEGER      NOT NULL,
    status          VARCHAR(50)  NOT NULL DEFAULT 'PENDING',
    like_count      INTEGER      NOT NULL DEFAULT 0,
    deleted_at      TIMESTAMP,
    created_at      TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE story_images (
    id              VARCHAR(36)  PRIMARY KEY,
    story_id        VARCHAR(36)  NOT NULL REFERENCES stories(id) ON DELETE CASCADE,
    image_url       VARCHAR(500) NOT NULL,
    thumbnail_url   VARCHAR(500),
    sort_order      INTEGER      NOT NULL DEFAULT 0,
    created_at      TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE story_tags (
    story_id        VARCHAR(36) NOT NULL REFERENCES stories(id) ON DELETE CASCADE,
    tag_id          VARCHAR(36) NOT NULL REFERENCES tags(id) ON DELETE CASCADE,
    PRIMARY KEY (story_id, tag_id)
);

CREATE TABLE reactions (
    id              VARCHAR(36)  PRIMARY KEY,
    user_id         VARCHAR(36)  NOT NULL REFERENCES users(id),
    story_id        VARCHAR(36)  NOT NULL REFERENCES stories(id),
    type            VARCHAR(50)  NOT NULL,
    created_at      TIMESTAMP    NOT NULL DEFAULT NOW(),
    UNIQUE (user_id, story_id, type)
);

CREATE TABLE reports (
    id              VARCHAR(36)  PRIMARY KEY,
    story_id        VARCHAR(36)  NOT NULL REFERENCES stories(id),
    reporter_id     VARCHAR(36)  NOT NULL REFERENCES users(id),
    reason          TEXT         NOT NULL,
    status          VARCHAR(50)  NOT NULL DEFAULT 'RECEIVED',
    created_at      TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE review_logs (
    id              VARCHAR(36)  PRIMARY KEY,
    story_id        VARCHAR(36)  NOT NULL REFERENCES stories(id),
    reviewer_id     VARCHAR(36)  NOT NULL REFERENCES users(id),
    before_status   VARCHAR(50)  NOT NULL,
    after_status    VARCHAR(50)  NOT NULL,
    note            TEXT,
    created_at      TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE audit_logs (
    id              VARCHAR(36)  PRIMARY KEY,
    user_id         VARCHAR(36)  REFERENCES users(id),
    action          VARCHAR(50)  NOT NULL,
    entity_type     VARCHAR(255) NOT NULL,
    entity_id       VARCHAR(255) NOT NULL,
    detail          TEXT,
    created_at      TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- 조회 성능용 인덱스
CREATE INDEX idx_stories_status      ON stories(status) WHERE deleted_at IS NULL;
CREATE INDEX idx_stories_region      ON stories(region_id);
CREATE INDEX idx_stories_user        ON stories(user_id);
CREATE INDEX idx_places_region       ON places(region_id);
CREATE INDEX idx_reports_status      ON reports(status);
CREATE INDEX idx_audit_logs_entity   ON audit_logs(entity_type, entity_id);
