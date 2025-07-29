package com.retrip.trip.application.in.response;

import com.retrip.trip.domain.entity.Trip;
import com.retrip.trip.domain.entity.participant.Participant;
import com.retrip.trip.domain.vo.ParticipantRole;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(description = "여행 리더 위임 Response")
public record DelegateLeaderResponse(
        @Schema(description = "새로운 리더 ID") UUID id,
        @Schema(description = "여행 ID") UUID tripId,
        @Schema(description = "새로운 리더 멤버 ID") UUID memberId,
        @Schema(description = "새로운 리더 멤버 Role") String role) {
    public static DelegateLeaderResponse of(Trip trip, Participant leader) {
        return new DelegateLeaderResponse(
                leader.getId(), trip.getId(), leader.getMemberId(), leader.getRole().getViewName());
    }
}
