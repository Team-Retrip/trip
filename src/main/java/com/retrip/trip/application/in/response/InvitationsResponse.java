package com.retrip.trip.application.in.response;

import com.retrip.trip.domain.entity.invitation.Invitation;

import java.time.LocalDateTime;
import java.util.UUID;

public record InvitationsResponse(
        UUID tripId,
        UUID tripInvitationId,
        UUID memberId,
        String status,
        LocalDateTime invitedAt,
        long expireDays,
        LocalDateTime expiresAt
) {
    public static InvitationsResponse of(Invitation invitation) {
        return new InvitationsResponse(
                invitation.getTripId(),
                invitation.getId(),
                invitation.getMemberId(),
                invitation.getStatus().name(),
                invitation.getInvitedAt(),
                invitation.getExpireDays(),
                invitation.getExpiresAt()
        );
    }
}
