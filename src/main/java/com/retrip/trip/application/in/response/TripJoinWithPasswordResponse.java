package com.retrip.trip.application.in.response;

import com.retrip.trip.domain.entity.Trip;
import com.retrip.trip.domain.entity.participant.Participant;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "여행 공개 여부 변경 response")
public record TripJoinWithPasswordResponse(
        @Schema(description = "여행 ID")
        UUID tripId,

        @Schema(description = "참가자 ID")
        UUID participantId
) {
    public static TripJoinWithPasswordResponse of(Trip trip, Participant participant) {
        return new TripJoinWithPasswordResponse(trip.getId(), participant.getMemberId());
    }
}
