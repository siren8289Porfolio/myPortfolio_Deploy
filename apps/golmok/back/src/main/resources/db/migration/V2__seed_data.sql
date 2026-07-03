-- 개발용 시드 데이터 (Flyway 1회 적용)

-- 사용자 (BCrypt: admin1234 / user1234)
INSERT INTO users (id, email, password_hash, nickname, role, created_at, updated_at) VALUES
('usr-admin-001', 'admin@dasi-golmok.kr', '$2a$10$V902HAvLTDApK5ylOdYskeaXeh4m0kSTrXQKRCkuADS0rFqBvERTq', '관리자', 'ADMIN', NOW(), NOW()),
('usr-user-001',  'user@example.com',     '$2a$10$vgUQLVlcmPcXpTSfD6XBQ..sAYPH9PhlKCxjY076HRw66LVe3i54i', '기록자', 'USER',  NOW(), NOW());

-- 지역
INSERT INTO regions (id, name, slug, description, created_at, updated_at) VALUES
('reg-001', '전주 한옥마을', 'jeonju-hanok',   '전통 한옥과 골목길이 어우러진 뉴트로 명소', NOW(), NOW()),
('reg-002', '익선동',       'ikseon-dong',    '레트로 감성 카페와 한옥이 공존하는 서울의 골목', NOW(), NOW()),
('reg-003', '성수동',       'seongsu-dong',   '공장지대에서 문화공간으로 변모한 동네', NOW(), NOW()),
('reg-004', '을지로',       'euljiro',        '네온사인과 공구상가가 살아있는 서울의 옛 거리', NOW(), NOW()),
('reg-005', '연남동',       'yeonnam-dong',   '홍대 뒤편 골목의 감성적인 공간', NOW(), NOW()),
('reg-006', '부평시장',     'bupyeong-market','인천의 전통시장과 골목 상권', NOW(), NOW());

-- 장소
INSERT INTO places (id, region_id, name, address, latitude, longitude, created_at, updated_at) VALUES
('plc-001', 'reg-001', '경기전 골목',     '경기전 골목',     35.8154, 127.1530, NOW(), NOW()),
('plc-002', 'reg-002', '익선동 골목',     '익선동 골목',     37.5745, 126.9892, NOW(), NOW()),
('plc-003', 'reg-003', '성수동 수제화거리','성수동 수제화거리',37.5447, 127.0557, NOW(), NOW()),
('plc-004', 'reg-004', '을지로3가 골목',  '을지로3가 골목',  37.5662, 126.9910, NOW(), NOW()),
('plc-005', 'reg-005', '연남동 골목길',   '연남동 골목길',   37.5658, 126.9245, NOW(), NOW()),
('plc-006', 'reg-006', '부평시장 골목',   '부평시장 골목',   37.4918, 126.7258, NOW(), NOW());

-- 태그
INSERT INTO tags (id, name, type, created_at) VALUES
('tag-period-1970s', '1970s', 'PERIOD', NOW()),
('tag-period-1980s', '1980s', 'PERIOD', NOW()),
('tag-period-1990s', '1990s', 'PERIOD', NOW()),
('tag-period-2000s', '2000s', 'PERIOD', NOW()),
('tag-theme-street', '거리',     'THEME', NOW()),
('tag-theme-shop',   '상점',     'THEME', NOW()),
('tag-theme-market', '시장',     'THEME', NOW()),
('tag-theme-heritage','문화유산','THEME', NOW());

-- 스토리
INSERT INTO stories (id, user_id, region_id, place_id, title, content, year, status, like_count, created_at, updated_at) VALUES
('story-001', 'usr-user-001', 'reg-001', 'plc-001', '1980년대 전주 한옥마을의 오후',
 '1980년대 전주 한옥마을의 오후에 대한 지역 주민의 기억입니다. 전통 한옥과 골목길이 어우러진 뉴트로 명소', 1980, 'APPROVED', 3, NOW(), NOW()),
('story-002', 'usr-user-001', 'reg-002', 'plc-002', '익선동 골목의 봄날',
 '익선동 골목의 봄날에 대한 지역 주민의 기억입니다. 레트로 감성 카페와 한옥이 공존하는 서울의 골목', 1990, 'APPROVED', 5, NOW(), NOW()),
('story-003', 'usr-user-001', 'reg-003', 'plc-003', '성수동 공장의 흔적',
 '성수동 공장의 흔적에 대한 지역 주민의 기억입니다. 공장지대에서 문화공간으로 변모한 동네', 1970, 'APPROVED', 2, NOW(), NOW()),
('story-004', 'usr-user-001', 'reg-004', 'plc-004', '을지로 네온 골목의 밤',
 '을지로 네온 골목의 밤에 대한 지역 주민의 기억입니다. 네온사인과 공구상가가 살아있는 서울의 옛 거리', 1980, 'APPROVED', 4, NOW(), NOW()),
('story-005', 'usr-user-001', 'reg-005', 'plc-005', '연남동 골목의 오후',
 '연남동 골목의 오후에 대한 지역 주민의 기억입니다. 홍대 뒤편 골목의 감성적인 공간', 2000, 'APPROVED', 1, NOW(), NOW()),
('story-006', 'usr-user-001', 'reg-006', 'plc-006', '부평시장의 아침',
 '부평시장의 아침에 대한 지역 주민의 기억입니다. 인천의 전통시장과 골목 상권', 1990, 'APPROVED', 6, NOW(), NOW());

-- 스토리 이미지
INSERT INTO story_images (id, story_id, image_url, thumbnail_url, sort_order, created_at) VALUES
('img-001', 'story-001', 'https://picsum.photos/seed/jeonju-hanok/800/600',   'https://picsum.photos/seed/jeonju-hanok/400/300',   0, NOW()),
('img-002', 'story-002', 'https://picsum.photos/seed/ikseon-dong/800/600',    'https://picsum.photos/seed/ikseon-dong/400/300',    0, NOW()),
('img-003', 'story-003', 'https://picsum.photos/seed/seongsu-dong/800/600',   'https://picsum.photos/seed/seongsu-dong/400/300',   0, NOW()),
('img-004', 'story-004', 'https://picsum.photos/seed/euljiro/800/600',        'https://picsum.photos/seed/euljiro/400/300',        0, NOW()),
('img-005', 'story-005', 'https://picsum.photos/seed/yeonnam-dong/800/600',  'https://picsum.photos/seed/yeonnam-dong/400/300',  0, NOW()),
('img-006', 'story-006', 'https://picsum.photos/seed/bupyeong-market/800/600','https://picsum.photos/seed/bupyeong-market/400/300',0, NOW());

-- 스토리-태그 연결
INSERT INTO story_tags (story_id, tag_id) VALUES
('story-001', 'tag-period-1980s'), ('story-001', 'tag-theme-street'),
('story-002', 'tag-period-1990s'), ('story-002', 'tag-theme-shop'),
('story-003', 'tag-period-1970s'), ('story-003', 'tag-theme-heritage'),
('story-004', 'tag-period-1980s'), ('story-004', 'tag-theme-street'),
('story-005', 'tag-period-2000s'), ('story-005', 'tag-theme-shop'),
('story-006', 'tag-period-1990s'), ('story-006', 'tag-theme-market');
