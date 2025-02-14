-- 유저 테이블
INSERT INTO `user` (
    alert_day, is_verified, created_at, deleted_at, id, updated_at, 
    nickname, role, provider_id, email, password, profile_url, 
    access_token, email_verification_token, refresh_token, 
    is_active, provider
) 
VALUES
(7, b'1', NOW(), NULL, NULL, NOW(), 'jun', 'ROLE_USER', NULL, 'jun@naver.com', '$2a$10$JIc80smPY9/iZZ1BeHifqulw.QePRRVyO3u1nHoP9ycMF0m/Hgou6', 'https://byeol-mypage.s3.ap-northeast-2.amazonaws.com/free-icon-user-9386837.PNG', NULL, NULL, NULL, 'ACTIVE', 'LOCAL'),
(7, b'1', NOW(), NULL, NULL, NOW(), 'hyeon', 'ROLE_USER', NULL, 'hyeon@naver.com', '$2a$10$JIc80smPY9/iZZ1BeHifqulw.QePRRVyO3u1nHoP9ycMF0m/Hgou6', 'https://byeol-mypage.s3.ap-northeast-2.amazonaws.com/free-icon-user-9386837.PNG', NULL, NULL, NULL, 'ACTIVE', 'LOCAL'),
(7, b'1', NOW(), NULL, NULL, NOW(), 'young', 'ROLE_USER', NULL, 'young@naver.com', '$2a$10$JIc80smPY9/iZZ1BeHifqulw.QePRRVyO3u1nHoP9ycMF0m/Hgou6', 'https://byeol-mypage.s3.ap-northeast-2.amazonaws.com/free-icon-user-9386837.PNG', NULL, NULL, NULL, 'ACTIVE', 'LOCAL'),
(7, b'1', NOW(), NULL, NULL, NOW(), 'hee', 'ROLE_USER', NULL, 'hee@naver.com', '$2a$10$JIc80smPY9/iZZ1BeHifqulw.QePRRVyO3u1nHoP9ycMF0m/Hgou6', 'https://byeol-mypage.s3.ap-northeast-2.amazonaws.com/free-icon-user-9386837.PNG', NULL, NULL, NULL, 'ACTIVE', 'LOCAL'),
(7, b'1', NOW(), NULL, NULL, NOW(), 'ji', 'ROLE_USER', NULL, 'ji@naver.com', '$2a$10$JIc80smPY9/iZZ1BeHifqulw.QePRRVyO3u1nHoP9ycMF0m/Hgou6', 'https://byeol-mypage.s3.ap-northeast-2.amazonaws.com/free-icon-user-9386837.PNG', NULL, NULL, NULL, 'ACTIVE', 'LOCAL'),
(7, b'1', NOW(), NULL, NULL, NOW(), 'test', 'ROLE_USER', NULL, 'test@naver.com', '$2a$10$JIc80smPY9/iZZ1BeHifqulw.QePRRVyO3u1nHoP9ycMF0m/Hgou6', 'https://byeol-mypage.s3.ap-northeast-2.amazonaws.com/free-icon-user-9386837.PNG', NULL, NULL, NULL, 'ACTIVE', 'LOCAL');

-- 태그 테이블
INSERT INTO `tag` (reference_count, bolder_color, color, name)
VALUES
(100, '#ea55cf', '#ed9fde', 'IT'),
(10, '#ff4500', '#ffa500', '요리'),
(20, '#1e90ff', '#87cefa', 'Spring'),
(30, '#32cd32', '#98fb98', '축구'),
(50, '#6a5acd', '#9370db', '영화'),
(22, '#ff6347', '#ff7f7f', '음악'),
(7, '#ffd700', '#fffacd', '게임'),
(19, '#ff69b4', '#ffb6c1', '여행'),
(21, '#4682b4', '#5f9ea0', '독서'),
(13, '#8b4513', '#d2b48c', '역사'),
(80, '#708090', '#778899', '과학'),
(20, '#00ced1', '#afeeee', '헬스');


-- User와 Tag를 연결한 복합테이블
INSERT into `user_tag` (tag_id, user_id)
VALUES
-- jun: 1개
(1, 1),
(4, 1),
(5, 1),
(6, 1),
(7, 1),
(8, 1),
(10, 1),
-- hyeon: 7개
(2, 2),
(4, 2),
(5, 2),
(6, 2),
(7, 2),
(8, 2),
(10, 2),
-- young: 4개
(1, 3),
(3, 3),
(5, 3),
(9, 3),
-- hee: 6개
(2, 4),
(4, 4),
(6, 4),
(7, 4),
(8, 4),
(11, 4),
-- ji: 10개
(1, 5),
(2, 5),
(3, 5),
(4, 5),
(5, 5),
(6, 5),
(7, 5),
(8, 5),
(9, 5),
(10, 5);

-- 개인 컬렉션 유지
INSERT INTO `personal_collection` (`created_at`, `updated_at`, `user_id`, `name`)
VALUES
    -- user_id = 1 (기존 2개 유지)
    (NOW(), NOW(), 1, '즐거운하루'),
	(NOW(), NOW(), 1, '개발자의삶'),
	(NOW(), NOW(), 1, '여행가고싶다'),
	(NOW(), NOW(), 1, '맛집탐방기록'),
	(NOW(), NOW(), 1, '운동하고건강하자'),
	(NOW(), NOW(), 1, '책읽는시간'),
	(NOW(), NOW(), 1, '자기계발노트'),
    -- user_id = 2 (기존 3개 유지)
    (NOW(), NOW(), 2, '맛집탐방기'),
    (NOW(), NOW(), 2, '비밀금고'),
    (NOW(), NOW(), 2, '절약의미학'),
    -- user_id = 3 (총 7개로 확장)
    (NOW(), NOW(), 3, '슬기로운생'),
    (NOW(), NOW(), 3, '웃음저장소'),
    (NOW(), NOW(), 3, '나혼자산다'),
    (NOW(), NOW(), 3, '아무말대잔치'),
    (NOW(), NOW(), 3, '힐링타임'),
    (NOW(), NOW(), 3, '혼밥성공기'),
    (NOW(), NOW(), 3, '꿀맛여행'),
    -- user_id = 4 (기존 2개 유지)
    (NOW(), NOW(), 4, '세상유머'),
    (NOW(), NOW(), 4, '여행덕후'),
    -- user_id = 5 (1개만 남김)
    (NOW(), NOW(), 5, '아재개그모음'),
    (NOW(), NOW(), 5, '운동하는사람'),
    (NOW(), NOW(), 5, '취미천국'),
    (NOW(), NOW(), 5, '근육저장소'),
    (NOW(), NOW(), 5, '다이어트도전'),
    (NOW(), NOW(), 5, '런닝머신의비밀'),
    (NOW(), NOW(), 5, '헬스장고수');


INSERT INTO `shared_collection` (created_at, updated_at, name)
VALUES
(NOW(), NOW(), '개발자 모임 자료'),
(NOW(), NOW(), '요리 동호회 레시피'),
(NOW(), NOW(), '여행 플랜 공유'),
(NOW(), NOW(), '영화 팬클럽 추천작'),
(NOW(), NOW(), '독서 토론 그룹'),
(NOW(), NOW(), '운동 동호회 계획'),
(NOW(), NOW(), '스터디 공유 자료'),
(NOW(), NOW(), '음악 취향 공유'),
(NOW(), NOW(), '취미 생활 아이디어'),
(NOW(), NOW(), '헬스 챌린지 공유'),
(NOW(), NOW(), '디자인 팀 프로젝트'),
(NOW(), NOW(), '사진 동호회 갤러리');


INSERT INTO `shared_user` (`created_at`, `id`, `shared_collection_id`, `updated_at`, `user_id`, `role`) VALUES
-- 1번 방 (5명, 1명 OWNER, 나머지 MEMBER)
(NOW(), NULL, 1, NOW(), 1, 'OWNER'),
(NOW(), NULL, 1, NOW(), 2, 'MEMBER'),
(NOW(), NULL, 1, NOW(), 3, 'MEMBER'),
(NOW(), NULL, 1, NOW(), 4, 'MEMBER'),
(NOW(), NULL, 1, NOW(), 5, 'MEMBER'),
-- 2번 방 (4명, 1명 OWNER, 나머지 MEMBER)
(NOW(), NULL, 2, NOW(), 1, 'OWNER'),
(NOW(), NULL, 2, NOW(), 2, 'MEMBER'),
(NOW(), NULL, 2, NOW(), 3, 'MEMBER'),
(NOW(), NULL, 2, NOW(), 4, 'MEMBER'),
-- 3번 방 (2명, 1명 OWNER, 1명 MEMBER)
(NOW(), NULL, 3, NOW(), 5, 'OWNER'),
(NOW(), NULL, 3, NOW(), 1, 'MEMBER');


INSERT INTO `bookmark_url` (`reading_time`, `id`, `reference_count`, `url`) VALUES
(FLOOR(RAND() * 11) + 5, NULL, FLOOR(RAND() * 100) + 1, 'https://www.naver.com'),
(FLOOR(RAND() * 11) + 5, NULL, FLOOR(RAND() * 100) + 1, 'https://www.google.com'),
(FLOOR(RAND() * 11) + 5, NULL, FLOOR(RAND() * 100) + 1, 'https://www.tistory.com'),
(FLOOR(RAND() * 11) + 5, NULL, FLOOR(RAND() * 100) + 1, 'https://www.github.com'),
(FLOOR(RAND() * 11) + 5, NULL, FLOOR(RAND() * 100) + 1, 'https://www.medium.com'),
(FLOOR(RAND() * 11) + 5, NULL, FLOOR(RAND() * 100) + 1, 'https://www.reddit.com'),
(FLOOR(RAND() * 11) + 5, NULL, FLOOR(RAND() * 100) + 1, 'https://www.wikipedia.org'),
(FLOOR(RAND() * 11) + 5, NULL, FLOOR(RAND() * 100) + 1, 'https://www.stackoverflow.com'),
(FLOOR(RAND() * 11) + 5, NULL, FLOOR(RAND() * 100) + 1, 'https://www.youtube.com'),
(FLOOR(RAND() * 11) + 5, NULL, FLOOR(RAND() * 100) + 1, 'https://www.facebook.com'),
(FLOOR(RAND() * 11) + 5, NULL, FLOOR(RAND() * 100) + 1, 'https://www.amazon.com'),
(FLOOR(RAND() * 11) + 5, NULL, FLOOR(RAND() * 100) + 1, 'https://www.apple.com'),
(FLOOR(RAND() * 11) + 5, NULL, FLOOR(RAND() * 100) + 1, 'https://www.bbc.com'),
(FLOOR(RAND() * 11) + 5, NULL, FLOOR(RAND() * 100) + 1, 'https://www.cnn.com'),
(FLOOR(RAND() * 11) + 5, NULL, FLOOR(RAND() * 100) + 1, 'https://www.nytimes.com'),
(FLOOR(RAND() * 11) + 5, NULL, FLOOR(RAND() * 100) + 1, 'https://www.ted.com'),
(FLOOR(RAND() * 11) + 5, NULL, FLOOR(RAND() * 100) + 1, 'https://www.quora.com'),
(FLOOR(RAND() * 11) + 5, NULL, FLOOR(RAND() * 100) + 1, 'https://www.netflix.com'),
(FLOOR(RAND() * 11) + 5, NULL, FLOOR(RAND() * 100) + 1, 'https://www.tumblr.com'),
(FLOOR(RAND() * 11) + 5, NULL, FLOOR(RAND() * 100) + 1, 'https://www.instagram.com');


INSERT INTO `bookmarks` (
    is_read, priority, created_at, updated_at, 
    personal_collection_id, shared_collection_id, 
    url_id, user_id
)
VALUES
-- personal_collection_id: 1 (6개)
(b'1', b'1', DATE_ADD(NOW(), INTERVAL -FLOOR(RAND()*50) DAY), NOW(), 1, NULL, 1, 1),
(b'0', b'1', DATE_ADD(NOW(), INTERVAL -FLOOR(RAND()*50) DAY), NOW(), 1, NULL, 2, 1),
(b'1', b'0', DATE_ADD(NOW(), INTERVAL -FLOOR(RAND()*50) DAY), NOW(), 1, NULL, 3, 1),
(b'0', b'0', DATE_ADD(NOW(), INTERVAL -FLOOR(RAND()*50) DAY), NOW(), 1, NULL, 13, 1),
(b'1', b'0', DATE_ADD(NOW(), INTERVAL -FLOOR(RAND()*50) DAY), NOW(), 1, NULL, 14, 1),
(b'0', b'1', DATE_ADD(NOW(), INTERVAL -FLOOR(RAND()*50) DAY), NOW(), 1, NULL, 15, 1),
-- personal_collection_id: 2 (5개)
(b'0', b'0', DATE_ADD(NOW(), INTERVAL -FLOOR(RAND()*50) DAY), NOW(), 2, NULL, 4, 1),
(b'1', b'0', DATE_ADD(NOW(), INTERVAL -FLOOR(RAND()*50) DAY), NOW(), 2, NULL, 5, 1),
(b'1', b'1', DATE_ADD(NOW(), INTERVAL -FLOOR(RAND()*50) DAY), NOW(), 2, NULL, 6, 1),
(b'0', b'1', DATE_ADD(NOW(), INTERVAL -FLOOR(RAND()*50) DAY), NOW(), 2, NULL, 7, 1),
(b'1', b'0', DATE_ADD(NOW(), INTERVAL -FLOOR(RAND()*50) DAY), NOW(), 2, NULL, 8, 1),
-- personal_collection_id: 3 (7개)
(b'1', b'1', DATE_ADD(NOW(), INTERVAL -FLOOR(RAND()*50) DAY), NOW(), 3, NULL, 9, 1),
(b'0', b'1', DATE_ADD(NOW(), INTERVAL -FLOOR(RAND()*50) DAY), NOW(), 3, NULL, 10, 1),
(b'0', b'0', DATE_ADD(NOW(), INTERVAL -FLOOR(RAND()*50) DAY), NOW(), 3, NULL, 11, 1),
(b'1', b'1', DATE_ADD(NOW(), INTERVAL -FLOOR(RAND()*50) DAY), NOW(), 3, NULL, 12, 1),
(b'0', b'0', DATE_ADD(NOW(), INTERVAL -FLOOR(RAND()*50) DAY), NOW(), 3, NULL, 13, 1),
(b'1', b'0', DATE_ADD(NOW(), INTERVAL -FLOOR(RAND()*50) DAY), NOW(), 3, NULL, 14, 1),
(b'0', b'1', DATE_ADD(NOW(), INTERVAL -FLOOR(RAND()*50) DAY), NOW(), 3, NULL, 15, 1),

-- shared_collection_id: 1 (4개)
(b'0', b'0', DATE_ADD(NOW(), INTERVAL -FLOOR(RAND()*40) DAY), NOW(), NULL, 1, 16, 1),
(b'1', b'1', DATE_ADD(NOW(), INTERVAL -FLOOR(RAND()*40) DAY), NOW(), NULL, 1, 17, 2),
(b'0', b'1', DATE_ADD(NOW(), INTERVAL -FLOOR(RAND()*40) DAY), NOW(), NULL, 1, 18, 3),
(b'1', b'0', DATE_ADD(NOW(), INTERVAL -FLOOR(RAND()*40) DAY), NOW(), NULL, 1, 19, 4),
-- shared_collection_id: 2 (10개)
(b'1', b'0', NOW(), NOW(), NULL, 2, 1, 1),
(b'0', b'0', NOW(), NOW(), NULL, 2, 2, 1),
(b'1', b'1', NOW(), NOW(), NULL, 2, 3, 1),
(b'0', b'1', NOW(), NOW(), NULL, 2, 4, 1),
(b'1', b'0', NOW(), NOW(), NULL, 2, 5, 1),
(b'0', b'1', NOW(), NOW(), NULL, 2, 6, 1),
(b'1', b'0', NOW(), NOW(), NULL, 2, 7, 1),
(b'0', b'1', NOW(), NOW(), NULL, 2, 8, 1),
(b'1', b'1', NOW(), NOW(), NULL, 2, 9, 1),
(b'0', b'0', NOW(), NOW(), NULL, 2, 10, 1),
-- shared_collection_id: 3 (5개)
(b'1', b'1', DATE_ADD(NOW(), INTERVAL -FLOOR(RAND()*40) DAY), NOW(), NULL, 3, 11, 5),
(b'0', b'1', DATE_ADD(NOW(), INTERVAL -FLOOR(RAND()*40) DAY), NOW(), NULL, 3, 12, 5),
(b'1', b'0', DATE_ADD(NOW(), INTERVAL -FLOOR(RAND()*40) DAY), NOW(), NULL, 3, 13, 5),
(b'0', b'0', DATE_ADD(NOW(), INTERVAL -FLOOR(RAND()*40) DAY), NOW(), NULL, 3, 14, 5),
(b'1', b'1', DATE_ADD(NOW(), INTERVAL -FLOOR(RAND()*40) DAY), NOW(), NULL, 3, 15, 5),
-- shared_collection_id: 12 (5개)
(b'0', b'1', NOW(), NOW(), NULL, 12, 16, 5),
(b'1', b'0', NOW(), NOW(), NULL, 12, 17, 5),
(b'0', b'0', NOW(), NOW(), NULL, 12, 18, 5),
(b'1', b'1', NOW(), NOW(), NULL, 12, 19, 5),
(b'0', b'1', NOW(), NOW(), NULL, 12, 20, 5);


INSERT INTO `bookmark_tag` (bookmark_id, tag_id)
VALUES
-- personal_collection_id: 1에 있는 것들
(1, 1), (1, 2),
(2, 1), (2, 3), 
(3, 1), (3, 4), (3, 5), (3, 6),
-- personal_collection_id: 2
(4, 1), (4, 2), (4, 7),
(5, 1), (5, 8), (5, 9),
(6, 1), (6, 10),
(7, 1), (7, 11), (7, 12),
(8, 1), (8, 1),
-- personal_collection_id: 3
(9, 1), (9, 3), (9, 4),
(10, 1), (10, 6),
(11, 1), (11, 2), (11, 7), (11, 8),
(12, 1), (12, 5),
(13, 1), (13, 9),
(14, 10), (14, 12),
(15, 11),
-- shared_collection_id: 1
(16, 1),
(17, 3), (17, 4),
(18, 7),
(19, 8), (19, 11),
-- shared_collection_id: 2
(20, 6), (20, 10),
(21, 1), (21, 2),
(22, 9), (22, 12),
(23, 3),
(24, 4), (24, 5), (24, 7),
(25, 6),
(26, 8),
-- shared_collection_id: 3
(27, 2), (27, 11),
(28, 1),
(29, 3), (29, 5),
(30, 10),
-- shared_collection_id: 12
(31, 4), (31, 8),
(32, 7), (32, 9),
(33, 12),
(34, 10), (34, 11),
(35, 2);


INSERT INTO `bookmark_url_tag` (`bookmark_url_id`, `tag_id`) VALUES
-- IT 태그
(1, 1),
(2, 1),
(3, 1),
(4, 1),
(5, 1),
(6, 1),
(7, 1),
(8, 1),
(9, 1),
(10, 1),
(11, 1),
(12, 1),
(13, 1),
(14, 1),
(15, 1),
(16, 1),

-- 요리
(1, 2),
(2, 2),
(3, 2),
(4, 2),
-- Spring
(1, 3),
(2, 3),
(3, 3),
(4, 3),
(5, 3),
(6, 3),
(7, 3),
(8, 3),
-- 축구
(10, 4),
(11, 4),
(12, 4),
(13, 4),
(14, 4),
(15, 4),
(16, 4),
(17, 4),
(18, 4);

INSERT INTO `rss` (`created_at`, `updated_at`, `name`, `rss_url`) VALUES
(NOW(), NOW(), '연합뉴스', 'https://www.yna.co.kr/rss/news.xml'),
(NOW(), NOW(), '김밥상의 먹는 일상', 'https://rss.blog.naver.com/ss0110010.xml'),
(NOW(), NOW(), '구글 뉴스', 'https://news.google.com/rss?hl=ko&gl=KR&ceid=KR:ko'),
(NOW(), NOW(), 'TechCrunch', 'https://techcrunch.com/feed/'),
(NOW(), NOW(), 'Slashdot', 'https://rss.slashdot.org/Slashdot/slashdot');

INSERT INTO `user_rss` (`is_read`, `id`, `rss_id`, `user_id`, `latest_title`, `previous_title`) VALUES
(b'0', NULL, 1, 1, NULL, NULL),
(b'1', NULL, 2, 1, NULL, NULL),
(b'0', NULL, 3, 1, NULL, NULL),
(b'1', NULL, 4, 1, NULL, NULL),
(b'0', NULL, 5, 1, NULL, NULL),
(b'1', NULL, 1, 1, NULL, NULL),
(b'0', NULL, 2, 1, NULL, NULL),
(b'1', NULL, 3, 2, NULL, NULL),
(b'0', NULL, 4, 2, NULL, NULL),
(b'1', NULL, 5, 3, NULL, NULL);

INSERT INTO `notification` (`bookmark_id`, `created_at`, `updated_at`, `shared_collection_id`, `user_id`, `type`, `message`, `nickname`)
VALUES
-- 북마크 알림 (BOOKMARK)
(1, NOW(), NOW(), NULL, 1, 'BOOKMARK', '📌 북마크한 글이 7일이 지나갔습니다. 확인해보세요!', NULL),
(2, NOW(), NOW(), NULL, 2, 'BOOKMARK', '📌 북마크한 글이 15일이 지나갔습니다. 확인해보세요!', NULL),
(3, NOW(), NOW(), NULL, 3, 'BOOKMARK', '📌 북마크한 글이 7일이 지나갔습니다. 확인해보세요!', NULL),
(4, NOW(), NOW(), NULL, 4, 'BOOKMARK', '📌 북마크한 글이 7일이 지나갔습니다. 확인해보세요!', NULL),
(5, NOW(), NOW(), NULL, 5, 'BOOKMARK', '📌 북마크한 글이 7일이 지나갔습니다. 확인해보세요!', NULL),

-- 초대 알림 (INVITE)
(NULL, NOW(), NOW(), 1, 1, 'INVITE', '개발자 모임 자료 공유 컬렉션에 초대되었습니다.', 'jun'),
(NULL, NOW(), NOW(), 2, 2, 'INVITE', '요리 동호회 레시피 공유 컬렉션에 초대되었습니다.', 'hyeon'),
(NULL, NOW(), NOW(), 3, 3, 'INVITE', '여행 플랜 공유 컬렉션에 초대되었습니다.', 'young'),
(NULL, NOW(), NOW(), 4, 4, 'INVITE', '영화 팬클럽 추천작 공유 컬렉션에 초대되었습니다.', 'hee'),
(NULL, NOW(), NOW(), 5, 5, 'INVITE', '독서 토론 그룹 공유 컬렉션에 초대되었습니다.', 'ji');



-- 데이터 담은 테이블들
-- select * from bookmarks;
-- select * from bookmark_tag;
-- select * from user; 
-- select * from tag;
-- select * from user_tag;
-- select * from personal_collection;
-- select count(*) from bookmarks where user_id=1 and priority=1;
-- select * from shared_collection;
-- select * from shared_user;
-- select * from bookmark_url;
-- select * from bookmark_tag;




