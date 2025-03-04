package com.retrip.trip.application.in.request;

import com.retrip.trip.domain.entity.Trip;
import com.retrip.trip.domain.entity.TripParticipant;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record TripJoinRequest(
        @NotNull UUID tripId,
        @NotNull UUID memberId,
        String message // 선택적 요청 메시지
) {
    public TripParticipant toParticipant(Trip trip) {
        // Trip은 컨트롤러나 서비스에서 미리 조회한 객체를 전달한다고 가정
        return TripParticipant.createTripParticipant(memberId, trip);
    }
}
