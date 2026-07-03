-- 확장 목업 데이터 (검수·신고·다양한 상태·추가 사용자)

-- 추가 사용자 (비밀번호: user1234)
INSERT INTO users (id, email, password_hash, nickname, role, created_at, updated_at) VALUES
('usr-user-002', 'archive@example.com', '$2a$10$vgUQLVlcmPcXpTSfD6XBQ..sAYPH9PhlKCxjY076HRw66LVe3i54i', '아카이버', 'USER', NOW() - INTERVAL '30 days', NOW()),
('usr-user-003', 'retro@example.com',  '$2a$10$vgUQLVlcmPcXpTSfD6XBQ..sAYPH9PhlKCxjY076HRw66LVe3i54i', '레트로러', 'USER', NOW() - INTERVAL '14 days', NOW());

-- 추가 장소
INSERT INTO places (id, region_id, name, address, latitude, longitude, created_at, updated_at) VALUES
('plc-007', 'reg-001', '오목대 골목',       '전주 오목대 골목',       35.8128, 127.1502, NOW(), NOW()),
('plc-008', 'reg-002', '익선동 한옥 카페거리', '서울 종로 익선동',       37.5738, 126.9885, NOW(), NOW()),
('plc-009', 'reg-003', '성수 카페거리',     '서울 성동구 성수동',     37.5435, 127.0543, NOW(), NOW()),
('plc-010', 'reg-004', '을지로 입구 골목',  '서울 중구 을지로',       37.5655, 126.9905, NOW(), NOW());

-- 추가 태그
INSERT INTO tags (id, name, type, created_at) VALUES
('tag-theme-school',  '학교',     'THEME', NOW()),
('tag-theme-park',    '공원',     'THEME', NOW()),
('tag-custom-oral',   '구술',     'CUSTOM', NOW()),
('tag-custom-archive','개인소장', 'CUSTOM', NOW());

-- 승인 스토리 추가
INSERT INTO stories (id, user_id, region_id, place_id, title, content, year, status, like_count, created_at, updated_at) VALUES
('story-007', 'usr-user-002', 'reg-001', 'plc-007', '오목대 앞 골목의 겨울',
 '눈 내린 오목대 골목에서 놀던 어린 시절의 기억. 한옥 지붕 위로 내리는 눈송이가 아직도 선명하다.', 1970, 'APPROVED', 8,
 NOW() - INTERVAL '20 days', NOW() - INTERVAL '18 days'),
('story-008', 'usr-user-003', 'reg-002', 'plc-008', '익선동 카페 골목의 주말',
 '주말마다 친구들과 걷던 익선동 골목. 레트로 간판과 한옥이 어우러진 풍경이 인상적이었다.', 2000, 'APPROVED', 12,
 NOW() - INTERVAL '10 days', NOW() - INTERVAL '8 days'),
('story-009', 'usr-user-002', 'reg-003', 'plc-009', '성수동 벽화 골목',
 '공장 벽에 그려진 벽화가 골목 전체를 갤러리처럼 바꿔놓았다. 문화공간으로 변하는 동네의 상징.', 1990, 'APPROVED', 7,
 NOW() - INTERVAL '7 days', NOW() - INTERVAL '5 days'),
('story-010', 'usr-user-003', 'reg-004', 'plc-010', '을지로 네온 아래 공구상가',
 '밤이 되면 네온사인이 켜지는 을지로 골목. 공구상가 사장님들의 이야기가 골목마다 스며 있다.', 1980, 'APPROVED', 9,
 NOW() - INTERVAL '5 days', NOW() - INTERVAL '3 days');

-- 검수 대기(PENDING) 스토리
INSERT INTO stories (id, user_id, region_id, place_id, title, content, year, status, like_count, created_at, updated_at) VALUES
('story-011', 'usr-user-002', 'reg-005', 'plc-005', '연남동 골목의 새벽 산책',
 '이른 새벽 연남동 골목을 산책하며 찍은 사진입니다. 조용한 골목의 공기가 좋았습니다.', 2000, 'PENDING', 0,
 NOW() - INTERVAL '2 days', NOW() - INTERVAL '2 days'),
('story-012', 'usr-user-003', 'reg-006', 'plc-006', '부평시장 통닭 골목',
 '시장 뒤편 통닭 골목의 풍경. 어릴 때 부모님과 함께 걸었던 기억이 납니다.', 1990, 'PENDING', 0,
 NOW() - INTERVAL '1 day', NOW() - INTERVAL '1 day'),
('story-013', 'usr-user-001', 'reg-001', 'plc-001', '경기전 야경 (검수 테스트)',
 '테스트용 검수 대기 스토리입니다.', 1985, 'PENDING', 0, NOW(), NOW());

-- 숨김 스토리
INSERT INTO stories (id, user_id, region_id, place_id, title, content, year, status, like_count, created_at, updated_at) VALUES
('story-014', 'usr-user-001', 'reg-003', 'plc-003', '성수동 옛 공장 (숨김 처리됨)',
 '관리자 검수 후 숨김 처리된 테스트 스토리입니다.', 1975, 'HIDDEN', 1,
 NOW() - INTERVAL '30 days', NOW() - INTERVAL '25 days');

-- 스토리 이미지
INSERT INTO story_images (id, story_id, image_url, thumbnail_url, sort_order, created_at) VALUES
('img-007', 'story-007', 'https://picsum.photos/seed/omokdae/800/600',    'https://picsum.photos/seed/omokdae/400/300',    0, NOW()),
('img-008', 'story-008', 'https://picsum.photos/seed/ikseon-cafe/800/600', 'https://picsum.photos/seed/ikseon-cafe/400/300', 0, NOW()),
('img-009', 'story-009', 'https://picsum.photos/seed/seongsu-art/800/600', 'https://picsum.photos/seed/seongsu-art/400/300', 0, NOW()),
('img-010', 'story-010', 'https://picsum.photos/seed/euljiro-neon/800/600','https://picsum.photos/seed/euljiro-neon/400/300',0, NOW()),
('img-011', 'story-011', 'https://picsum.photos/seed/yeonnam-dawn/800/600', 'https://picsum.photos/seed/yeonnam-dawn/400/300', 0, NOW()),
('img-012', 'story-012', 'https://picsum.photos/seed/bupyeong-chicken/800/600','https://picsum.photos/seed/bupyeong-chicken/400/300',0, NOW()),
('img-013', 'story-013', 'https://picsum.photos/seed/gyeonggijeon-night/800/600','https://picsum.photos/seed/gyeonggijeon-night/400/300',0, NOW()),
('img-014', 'story-014', 'https://picsum.photos/seed/seongsu-factory/800/600','https://picsum.photos/seed/seongsu-factory/400/300',0, NOW());

-- 스토리-태그
INSERT INTO story_tags (story_id, tag_id) VALUES
('story-007', 'tag-period-1970s'), ('story-007', 'tag-theme-park'),
('story-008', 'tag-period-2000s'), ('story-008', 'tag-theme-shop'),
('story-009', 'tag-period-1990s'), ('story-009', 'tag-theme-heritage'),
('story-010', 'tag-period-1980s'), ('story-010', 'tag-theme-street'),
('story-011', 'tag-period-2000s'), ('story-011', 'tag-theme-street'),
('story-012', 'tag-period-1990s'), ('story-012', 'tag-theme-market'),
('story-013', 'tag-period-1980s'), ('story-013', 'tag-theme-heritage'),
('story-014', 'tag-period-1970s'), ('story-014', 'tag-theme-heritage');

-- 좋아요 반응
INSERT INTO reactions (id, user_id, story_id, type, created_at) VALUES
('react-001', 'usr-user-002', 'story-001', 'LIKE', NOW() - INTERVAL '15 days'),
('react-002', 'usr-user-003', 'story-001', 'LIKE', NOW() - INTERVAL '14 days'),
('react-003', 'usr-admin-001', 'story-002', 'LIKE', NOW() - INTERVAL '12 days'),
('react-004', 'usr-user-002', 'story-008', 'LIKE', NOW() - INTERVAL '8 days'),
('react-005', 'usr-user-003', 'story-008', 'LIKE', NOW() - INTERVAL '7 days'),
('react-006', 'usr-user-001', 'story-010', 'LIKE', NOW() - INTERVAL '3 days');

-- 신고 (관리자 검수용)
INSERT INTO reports (id, story_id, reporter_id, reason, status, created_at, updated_at) VALUES
('report-001', 'story-014', 'usr-user-002', '개인정보가 포함된 것 같습니다.', 'RECEIVED', NOW() - INTERVAL '3 days', NOW() - INTERVAL '3 days'),
('report-002', 'story-004', 'usr-user-003', '저작권 관련 문의가 필요합니다.', 'RECEIVED', NOW() - INTERVAL '1 day', NOW() - INTERVAL '1 day'),
('report-003', 'story-003', 'usr-user-001', '이미 처리 완료된 신고 샘플', 'RESOLVED', NOW() - INTERVAL '10 days', NOW() - INTERVAL '5 days');

-- 검수 이력 샘플
INSERT INTO review_logs (id, story_id, reviewer_id, before_status, after_status, note, created_at) VALUES
('review-001', 'story-014', 'usr-admin-001', 'APPROVED', 'HIDDEN', '신고 접수로 임시 숨김', NOW() - INTERVAL '25 days');

-- 감사 로그 샘플
INSERT INTO audit_logs (id, user_id, action, entity_type, entity_id, detail, created_at) VALUES
('audit-001', 'usr-admin-001', 'HIDE', 'Story', 'story-014', '신고 검토 후 숨김', NOW() - INTERVAL '25 days'),
('audit-002', 'usr-user-002', 'CREATE', 'Story', 'story-011', '스토리 등록', NOW() - INTERVAL '2 days');

-- Materialized View 갱신
REFRESH MATERIALIZED VIEW CONCURRENTLY mv_region_story_stats;
REFRESH MATERIALIZED VIEW CONCURRENTLY mv_story_curation_summary;
