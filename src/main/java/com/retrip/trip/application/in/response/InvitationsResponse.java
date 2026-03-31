package com.retrip.trip.application.in.response;

import com.retrip.trip.application.out.gateway.MemberGateway;
import com.retrip.trip.domain.entity.invitation.Invitation;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "보낸 초대장 응답")
public record InvitationsResponse(
        @Schema(description = "여행 ID")
        UUID tripId,

        @Schema(description = "초대장 ID")
        UUID tripInvitationId,

        @Schema(description = "초대받은 회원 ID")
        UUID memberId,

        @Schema(description = "초대 상태 코드", example = "INVITED")
        String status,

        @Schema(description = "초대 상태 표시명", example = "초대")
        String statusName,

        @Schema(description = "초대 일시", example = "2026-03-30T12:00:00")
        LocalDateTime invitedAt,

        @Schema(description = "초대 유효 기간 (일)", example = "7")
        long expireDays,

        @Schema(description = "초대 만료 일시", example = "2026-04-06T12:00:00")
        LocalDateTime expiresAt,

        @Schema(description = "초대된 회원 닉네임")
        String memberName,

        @Schema(description = "초대된 회원 프로필 이미지 URL")
        String memberProfileImageUrl,

        @Schema(description = "초대된 회원 한줄소개")
        String memberBio
) {
    public static InvitationsResponse of(Invitation invitation, MemberGateway.MemberInfo memberInfo) {
        return new InvitationsResponse(
                invitation.getTripId(),
                invitation.getId(),
                invitation.getMemberId(),
                invitation.getStatus().name(),
                invitation.getStatus().getViewName(),
                invitation.getInvitedAt(),
                invitation.getExpireDays(),
                invitation.getExpiresAt(),
                memberInfo != null ? memberInfo.name() : null,
                memberInfo != null ? memberInfo.profileImageUrl() : null,
                memberInfo != null ? memberInfo.bio() : null
        );
    }
}
