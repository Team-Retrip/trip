package com.retrip.trip.application.in.response;

import com.retrip.trip.domain.entity.invitation.Invitation;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "초대 거절 응답")
public record MemberInvitationRejectResponse(
        @Schema(description = "회원 ID")
        UUID memberId,
        @Schema(description = "초대장 ID")
        UUID tripInvitationId,
        @Schema(description = "여행 ID")
        UUID tripId,
        @Schema(description = "초대 상태 코드", example = "REJECTED")
        String status,
        @Schema(description = "초대 일시", example = "2026-03-30T12:00:00")
        LocalDateTime invitedAt,
        @Schema(description = "초대 유효 기간 (일)", example = "7")
        long expireDays,
        @Schema(description = "초대 만료 일시", example = "2026-04-06T12:00:00")
        LocalDateTime expiresAt
) {
    public static MemberInvitationRejectResponse of(Invitation invitation) {
        return new MemberInvitationRejectResponse(
                invitation.getMemberId(),
                invitation.getId(),
                invitation.getTripId(),
                invitation.getStatus().name(),
                invitation.getInvitedAt(),
                invitation.getExpireDays(),
                invitation.getExpiresAt()
        );
    }
}
