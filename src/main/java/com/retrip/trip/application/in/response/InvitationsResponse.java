package com.retrip.trip.application.in.response;

import com.retrip.trip.domain.entity.invitation.Invitation;

import java.util.UUID;

public record InvitationsResponse(
        UUID tripId,
        UUID tripInvitationId,
        UUID memberId,
        String status
) {
    public static InvitationsResponse of(Invitation invitation) {
        return new InvitationsResponse(
                invitation.getTripId(),
                invitation.getId(),
                invitation.getMemberId(),
                invitation.getStatus().name()
        );
    }
}
