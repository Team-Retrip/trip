package com.retrip.trip.application.in.response;

import com.retrip.trip.domain.entity.invitation.Invitation;
import com.retrip.trip.domain.vo.InvitationStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Schema(description = "여행 초대 생성 응답")
public record InvitationsCreateResponse(
        @Schema(description = "여행 ID")
        UUID tripId,
        @Schema(description = "생성된 초대장 목록")
        List<InvitationCreateResponse> invitations
) {
    public static InvitationsCreateResponse of(UUID tripId, List<Invitation> invitations) {
        return new InvitationsCreateResponse(
                tripId,
                invitations.stream()
                        .map(invitation -> new InvitationCreateResponse(
                                invitation.getMemberId(),
                                invitation.getStatus(),
                                invitation.getInvitedAt(),
                                invitation.getExpireDays(),
                                invitation.getExpiresAt()
                        )).toList()
        );
    }

    @Schema(description = "개별 초대장 정보")
    public record InvitationCreateResponse(
            @Schema(description = "초대받은 회원 ID")
            UUID memberId,
            @Schema(description = "초대 상태", example = "INVITED")
            InvitationStatus status,
            @Schema(description = "초대 일시", example = "2026-03-30T12:00:00")
            LocalDateTime invitedAt,
            @Schema(description = "초대 유효 기간 (일)", example = "7")
            long expireDays,
            @Schema(description = "초대 만료 일시", example = "2026-04-06T12:00:00")
            LocalDateTime expiresAt
    ) {
    }
}
