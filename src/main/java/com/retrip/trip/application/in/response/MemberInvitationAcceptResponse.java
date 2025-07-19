package com.retrip.trip.application.in.response;

import com.retrip.trip.domain.entity.invitation.Invitation;

import java.time.LocalDateTime;
import java.util.UUID;

public record MemberInvitationAcceptResponse(
        UUID memberId,
        UUID tripInvitationId,
        UUID tripId,
        String status,
        LocalDateTime invitedAt,
        long expireDays,
        LocalDateTime expiresAt
) {
    public static MemberInvitationAcceptResponse of(Invitation invitation) {
        return new MemberInvitationAcceptResponse(
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
