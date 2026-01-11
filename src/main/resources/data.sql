-- 2. Trip (여행 메인)
INSERT INTO trip (id, title, description, category, status, max_participants, open, start_date, end_date, version,
                  created_at)
VALUES (x'11111111111111111111111111111111', '도쿄 맛집 탐방', '도쿄 맛집 정복 여행', 'OVERSEAS', 'RECRUITING', 8, 1, '2026-03-01',
        '2026-03-03', 1, now());

INSERT INTO trip (id, title, description, category, status, max_participants, open, start_date, end_date, version,
                  created_at)
VALUES (x'11111111111111111111111111111112', '서울 맛집 탐방', '강남 일대 맛집 정복 여행', 'DOMESTIC', 'RECRUITING', 4, 1, '2026-03-01',
        '2026-03-08', 1, now());

-- 3. Itinerary (일정 날짜)
INSERT INTO itinerary (id, trip_id, name, date, created_at)
VALUES (x'22222222222222222222222222222222', x'11111111111111111111111111111112', '1일차 강남역 정복', '2026-03-01', now());

INSERT INTO itinerary (id, trip_id, name, date, created_at)
VALUES (x'22222222222222222222222222222223', x'11111111111111111111111111111112', '2일차 교대 정복', '2026-03-02', now());

INSERT INTO itinerary (id, trip_id, name, date, created_at)
VALUES (x'22222222222222222222222222222224', x'11111111111111111111111111111112', '3일차 서초 정복', '2026-03-03', now());

-- 4. Itinerary Detail (상세 일정)
INSERT INTO itinerary_detail (id, itinerary_id, description, time, price, created_at)
VALUES (random_uuid(), x'22222222222222222222222222222222', '점심 식사 (마라탕)', '2026-03-01 12:00:00', 15000, now());

-- 5. Trip Hash Tag (해시태그)
INSERT INTO trip_hash_tag (id, trip_id, name, created_at)
VALUES (random_uuid(), x'11111111111111111111111111111111', '도쿄여행', now());
INSERT INTO trip_hash_tag (id, trip_id, name, created_at)
VALUES (random_uuid(), x'11111111111111111111111111111111', '먹짱', now());

INSERT INTO trip_hash_tag (id, trip_id, name, created_at)
VALUES (random_uuid(), x'11111111111111111111111111111112', '서울여행', now());
INSERT INTO trip_hash_tag (id, trip_id, name, created_at)
VALUES (random_uuid(), x'11111111111111111111111111111112', '먹방', now());

-- 6. Participant (참가자 - Member ID는 임의 지정)
INSERT INTO participant (id, trip_id, member_id, created_at)
VALUES (random_uuid(), x'11111111111111111111111111111112', x'99999999999999999999999999999991', now());
INSERT INTO participant (id, trip_id, member_id, created_at)
VALUES (random_uuid(), x'11111111111111111111111111111112', x'99999999999999999999999999999992', now());
INSERT INTO participant (id, trip_id, member_id, created_at)
VALUES (random_uuid(), x'11111111111111111111111111111111', x'99999999999999999999999999999993', now());

-- 방장(HOST) 데이터
INSERT INTO trip_participant (id, trip_id, member_id, status, role, created_at)
VALUES (random_uuid(), x'11111111111111111111111111111111', x'99999999999999999999999999999993', 1, 'HOST', now());

-- 일반 참여자(MEMBER) 데이터 (Member ID를 다르게 설정)
INSERT INTO trip_participant (id, trip_id, member_id, status, role, created_at)
VALUES (random_uuid(), x'11111111111111111111111111111112', x'99999999999999999999999999999991', 1, 'HOST', now());
INSERT INTO trip_participant (id, trip_id, member_id, status, role, created_at)
VALUES (random_uuid(), x'11111111111111111111111111111112', x'99999999999999999999999999999992', 1, 'MEMBER', now());

-- 7. Vote (투표 생성)
INSERT INTO vote (id, trip_id, title, description, status, version, max_selections, anonymous, allow_add_option,
                  created_by)
VALUES (x'33333333333333333333333333333333', x'11111111111111111111111111111112', '점심 메뉴 투표', '뭐 먹을까요?', 'START', 1, 1,
        0, 1, x'99999999999999999999999999999992');

-- 8. Vote Option (투표 선택지)
INSERT INTO vote_option (id, vote_id, content)
VALUES (random_uuid(), x'33333333333333333333333333333333', '돈까스');
INSERT INTO vote_option (id, vote_id, content)
VALUES (random_uuid(), x'33333333333333333333333333333333', '초밥');

-- 9. Trip Confirmation Demand (확정 요청)
INSERT INTO trip_confirmation_demand (id, trip_id, confirm_start_date, confirm_end_date, expired, created_at)
VALUES (x'44444444444444444444444444444444', x'11111111111111111111111111111112', '2026-03-01', '2026-03-03', 0, now());

-- 10. Trip Confirmation Reply (확정 응답)
INSERT INTO trip_confirmation_reply (id, trip_confirmation_demand_id, member_id, status, created_at)
VALUES (random_uuid(), x'44444444444444444444444444444444', x'99999999999999999999999999999992', 'ACCEPTED', now());


-- 12. Demand (참가 신청: 특정 사용자가 여행에 참여하고 싶어함)
-- Status: 'PENDING', 'ACCEPTED', 'REJECTED' 등 (varchar 50)
-- Trip ID: x'11111111111111111111111111111111'

-- 대기 중인 신청
INSERT INTO demand (id, trip_id, member_id, status, message, created_at)
VALUES (random_uuid(), x'11111111111111111111111111111112', x'99999999999999999999999999999994', 'PENDING',
        '꼭 같이 가고 싶어요!', now());

-- 거절된 신청
INSERT INTO demand (id, trip_id, member_id, status, message, created_at)
VALUES (random_uuid(), x'11111111111111111111111111111112', x'99999999999999999999999999999995', 'REJECTED', '가고 싶습니다.',
        now());


-- 13. Invitation (초대: 방장이 특정 사용자를 초대함)
-- Status: 'SENT', 'ACCEPTED', 'EXPIRED' 등 (varchar 255)

-- 수락된 초대
INSERT INTO invitation (id, trip_id, member_id, status, version, expire_days, expires_at, invited_at, created_at)
VALUES (random_uuid(), x'11111111111111111111111111111112', x'99999999999999999999999999999996', 'ACCEPTED', 1, 7,
        dateadd('DAY', 7, now()), now(), now());

-- 만료된 초대
INSERT INTO invitation (id, trip_id, member_id, status, version, expire_days, expires_at, invited_at, created_at)
VALUES (random_uuid(), x'11111111111111111111111111111112', x'99999999999999999999999999999997', 'EXPIRED', 1, 3,
        dateadd('DAY', -1, now()), dateadd('DAY', -4, now()), dateadd('DAY', -4, now()));
