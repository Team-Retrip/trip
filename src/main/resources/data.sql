-- =============================================
-- 테스트용 고정 Member UUID
-- UUID → H2 hex 변환 규칙: 대시 제거 후 x'...' 감싸기
--   예) 26691fa7-1da2-4b27-b703-d6b89899bed3
--     → x'26691fa71da24b27b703d6b89899bed3'
--
-- LEADER (나)  : 26691fa7-1da2-4b27-b703-d6b89899bed3
-- MEMBER1     : 99999999-9999-9999-9999-999999999992
-- MEMBER2     : 99999999-9999-9999-9999-999999999993
-- MEMBER3     : 99999999-9999-9999-9999-999999999994
-- =============================================

-- 1. Trip
INSERT INTO trip (id, title, description, category, status, max_participants, open, start_date, end_date, version, created_at)
VALUES (x'11111111111111111111111111111111', '도쿄 맛집 탐방', '도쿄 맛집 정복 여행', 'OVERSEAS', 'RECRUITING', 8, 1,
        '2026-05-01', '2026-05-03', 1, now());

INSERT INTO trip (id, title, description, category, status, max_participants, open, start_date, end_date, version, created_at)
VALUES (x'11111111111111111111111111111112', '서울 맛집 탐방', '강남 일대 맛집 정복 여행', 'DOMESTIC', 'RECRUITING', 4, 1,
        '2026-05-10', '2026-05-15', 1, now());

-- 2. TripParticipant
-- 도쿄 여행: 99..91 이 LEADER
INSERT INTO trip_participant (id, trip_id, member_id, status, role, created_at)
VALUES (random_uuid(), x'11111111111111111111111111111111', x'26691fa71da24b27b703d6b89899bed3', 'ACTIVE', 'LEADER', now());

-- 서울 여행: 99..91 이 LEADER, 99..92 가 PARTICIPANT
INSERT INTO trip_participant (id, trip_id, member_id, status, role, created_at)
VALUES (random_uuid(), x'11111111111111111111111111111112', x'26691fa71da24b27b703d6b89899bed3', 'ACTIVE', 'LEADER', now());

INSERT INTO trip_participant (id, trip_id, member_id, status, role, created_at)
VALUES (random_uuid(), x'11111111111111111111111111111112', x'99999999999999999999999999999992', 'ACTIVE', 'PARTICIPANT', now());

-- 3. Itinerary
INSERT INTO itinerary (id, trip_id, name, date, created_at)
VALUES (x'22222222222222222222222222222222', x'11111111111111111111111111111112', '1일차 강남역', '2026-05-10', now());

INSERT INTO itinerary (id, trip_id, name, date, created_at)
VALUES (x'22222222222222222222222222222223', x'11111111111111111111111111111112', '2일차 교대', '2026-05-11', now());

-- 4. ItineraryDetail
INSERT INTO itinerary_detail (id, itinerary_id, memo, time, sort_order, created_at)
VALUES (random_uuid(), x'22222222222222222222222222222222', '점심 식사 (마라탕)', '2026-05-10 12:00:00', 0, now());

-- 5. TripHashTag
INSERT INTO trip_hash_tag (id, trip_id, name, tag_order, created_at)
VALUES (random_uuid(), x'11111111111111111111111111111111', '도쿄여행', 1, now());
INSERT INTO trip_hash_tag (id, trip_id, name, tag_order, created_at)
VALUES (random_uuid(), x'11111111111111111111111111111111', '먹짱', 2, now());

INSERT INTO trip_hash_tag (id, trip_id, name, tag_order, created_at)
VALUES (random_uuid(), x'11111111111111111111111111111112', '서울여행', 1, now());
INSERT INTO trip_hash_tag (id, trip_id, name, tag_order, created_at)
VALUES (random_uuid(), x'11111111111111111111111111111112', '먹방', 2, now());

-- 6. Invitation (초대 테스트용)
-- 도쿄 여행에 99..93 초대 중 (INVITED)
INSERT INTO invitation (id, trip_id, member_id, status, version, expire_days, expires_at, invited_at, created_at)
VALUES (x'55555555555555555555555555555551', x'11111111111111111111111111111111', x'99999999999999999999999999999993',
        'INVITED', 1, 5, dateadd('DAY', 5, now()), now(), now());

-- 서울 여행에 99..94 초대 - 이미 거절됨 (REJECTED)
INSERT INTO invitation (id, trip_id, member_id, status, version, expire_days, expires_at, invited_at, created_at)
VALUES (x'55555555555555555555555555555552', x'11111111111111111111111111111112', x'99999999999999999999999999999994',
        'REJECTED', 1, 5, dateadd('DAY', 5, now()), now(), now());

-- 7. Demand
INSERT INTO demand (id, trip_id, member_id, status, message, created_at)
VALUES (random_uuid(), x'11111111111111111111111111111111', x'99999999999999999999999999999994',
        'PENDING', '꼭 같이 가고 싶어요!', now());

-- 8. Vote
INSERT INTO vote (id, trip_id, title, description, status, version, max_selections, anonymous, allow_add_option, created_by)
VALUES (x'33333333333333333333333333333333', x'11111111111111111111111111111112', '점심 메뉴 투표', '뭐 먹을까요?',
        'START', 1, 1, 0, 1, x'99999999999999999999999999999091');

INSERT INTO vote_option (id, vote_id, content) VALUES (random_uuid(), x'33333333333333333333333333333333', '돈까스');
INSERT INTO vote_option (id, vote_id, content) VALUES (random_uuid(), x'33333333333333333333333333333333', '초밥');

-- 9. TripConfirmationDemand
INSERT INTO trip_confirmation_demand (id, trip_id, confirm_start_date, confirm_end_date, expired, created_at)
VALUES (x'44444444444444444444444444444444', x'11111111111111111111111111111112', '2026-05-10', '2026-05-12', 0, now());

INSERT INTO trip_confirmation_reply (id, trip_confirmation_demand_id, member_id, status, created_at)
VALUES (random_uuid(), x'44444444444444444444444444444444', x'99999999999999999999999999999092', 'ACCEPTED', now());
