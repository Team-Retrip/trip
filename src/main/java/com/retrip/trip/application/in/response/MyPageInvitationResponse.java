package com.retrip.trip.application.in.response;

import com.retrip.trip.domain.entity.Trip;
import com.retrip.trip.domain.entity.invitation.Invitation;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Schema(description = "마이페이지 초대함 응답")
public record MyPageInvitationResponse(
        @Schema(description = "초대장 ID")
        UUID invitationId,
        @Schema(description = "여행 ID")
        UUID tripId,
        @Schema(description = "초대 상태 코드", example = "INVITED")
        String invitationStatus,
        @Schema(description = "초대 상태 표시명", example = "초대완료")
        String invitationStatusName,
        @Schema(description = "초대 일시", example = "2026-01-23T00:00:00")
        LocalDateTime invitedAt,
        @Schema(description = "여행 제목")
        String tripTitle,
        @Schema(description = "여행 대표 이미지 URL")
        String tripImageUrl,
        @Schema(description = "현재 참가자 수", example = "4")
        int currentParticipants,
        @Schema(description = "최대 참가자 수", example = "6")
        int maxParticipants,
        @Schema(description = "여행 상태 코드", example = "RECRUITING")
        String tripStatus,
        @Schema(description = "여행 카테고리 코드", example = "DOMESTIC")
        String tripCategory,
        @Schema(description = "여행지 목록")
        List<DestinationResponse> destinations
) {
    public static MyPageInvitationResponse of(Invitation invitation, Trip trip) {
        return new MyPageInvitationResponse(
                invitation.getId(),
                invitation.getTripId(),
                invitation.getStatus().name(),
                invitation.getStatus().getViewName(),
                invitation.getInvitedAt(),
                trip != null ? trip.getTitle().getValue() : null,
                trip != null ? trip.getImageUrl() : null,
                trip != null ? trip.getTripParticipants().getCurrentCount() : 0,
                trip != null ? trip.getTripParticipants().getMaxParticipants() : 0,
                trip != null ? trip.getStatus().name() : null,
                trip != null ? trip.getCategory().name() : null,
                trip != null ? DestinationResponse.ofIds(trip.getDestinations().getDestinationIds()) : List.of()
        );
    }
}
