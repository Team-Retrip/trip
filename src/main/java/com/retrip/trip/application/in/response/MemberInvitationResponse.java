package com.retrip.trip.application.in.response;

import com.retrip.trip.domain.entity.Trip;
import com.retrip.trip.domain.entity.invitation.Invitation;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Schema(description = "받은 초대장 응답")
public record MemberInvitationResponse(
        @Schema(description = "회원 ID")
        UUID memberId,
        @Schema(description = "초대장 ID")
        UUID tripInvitationId,
        @Schema(description = "여행 ID")
        UUID tripId,
        @Schema(description = "초대 상태 코드", example = "INVITED")
        String status,
        @Schema(description = "초대 일시", example = "2026-03-30T12:00:00")
        LocalDateTime invitedAt,
        @Schema(description = "초대 유효 기간 (일)", example = "7")
        long expireDays,
        @Schema(description = "초대 만료 일시", example = "2026-04-06T12:00:00")
        LocalDateTime expiresAt,
        @Schema(description = "여행 제목")
        String tripTitle,
        @Schema(description = "여행 대표 이미지 URL")
        String tripImageUrl,
        @Schema(description = "현재 참가자 수", example = "3")
        int currentParticipants,
        @Schema(description = "최대 참가자 수", example = "6")
        int maxParticipants,
        @Schema(description = "여행 상태 코드", example = "ONGOING")
        String tripStatus,
        @Schema(description = "여행지 ID 목록")
        List<UUID> destinationIds
) {
    public static MemberInvitationResponse of(Invitation invitation, Trip trip) {
        return new MemberInvitationResponse(
                invitation.getMemberId(),
                invitation.getId(),
                invitation.getTripId(),
                invitation.getStatus().name(),
                invitation.getInvitedAt(),
                invitation.getExpireDays(),
                invitation.getExpiresAt(),
                trip != null ? trip.getTitle().getValue() : null,
                trip != null ? trip.getImageUrl() : null,
                trip != null ? trip.getTripParticipants().getCurrentCount() : 0,
                trip != null ? trip.getTripParticipants().getMaxParticipants() : 0,
                trip != null ? trip.getStatus().name() : null,
                trip != null ? trip.getDestinations().getDestinationIds() : List.of()
        );
    }
}
