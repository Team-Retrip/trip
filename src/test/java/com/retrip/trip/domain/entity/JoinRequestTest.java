package com.retrip.trip.domain.entity;

import com.retrip.trip.domain.vo.ParticipantStatus;
import com.retrip.trip.domain.vo.TripCategory;
import com.retrip.trip.domain.vo.TripDescription;
import com.retrip.trip.domain.vo.TripPeriod;
import com.retrip.trip.domain.vo.TripStatus;
import com.retrip.trip.domain.vo.TripTitle;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class JoinRequestTest {

    // 테스트용 사용자, 여행 ID 생성
    UUID userId = UUID.fromString("11111111-2222-3333-4444-555555555555");
    UUID tripId = UUID.fromString("22222222-2222-2222-2222-222222222222");

    // 테스트에 사용할 더미 Trip 엔티티 생성 (최소한의 값만 설정)
    private Trip createDummyTrip() {
        return Trip.builder()
                .id(tripId)
                .destinationId(UUID.randomUUID())
                .title(new TripTitle("테스트 여행"))
                .description(new TripDescription("테스트 설명"))
                .period(new TripPeriod(LocalDate.now().plusDays(1), LocalDate.now().plusDays(5)))
                .open(true)
                .maxParticipants(4)
                .status(TripStatus.RECRUITING)
                .category(TripCategory.DOMESTIC)
                .build();
    }

    @DisplayName("JoinRequest 생성 시 초기 상태는 '대기'여야 한다.")
    @Test
    void joinRequestCreationTest() {
        Trip dummyTrip = createDummyTrip();
        String message = "참여 요청 메시지";

        JoinRequest joinRequest = JoinRequest.create(userId, dummyTrip, message);
        assertThat(joinRequest.getStatus()).isEqualTo(ParticipantStatus.PENDING);
    }

    @DisplayName("JoinRequest 상태 변경이 정상적으로 동작한다.")
    @Test
    void joinRequestStatusChangeTest() {
        Trip dummyTrip = createDummyTrip();
        JoinRequest joinRequest = JoinRequest.create(userId, dummyTrip, "참여 요청 메시지");

        joinRequest.setStatus(ParticipantStatus.APPROVED);
        assertThat(joinRequest.getStatus()).isEqualTo(ParticipantStatus.APPROVED);

        joinRequest.setStatus(ParticipantStatus.REJECTED);
        assertThat(joinRequest.getStatus()).isEqualTo(ParticipantStatus.REJECTED);
    }
}
