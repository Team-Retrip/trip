package com.retrip.trip.application.in.response;

import com.retrip.trip.domain.entity.invitation.Invitation;

import java.util.UUID;

public record MemberInvitationsResponse(
        UUID memberId,
        UUID tripInvitationId,
        UUID tripId,
        String status
) {
    public static MemberInvitationsResponse of(Invitation invitation) {
        return new MemberInvitationsResponse(
                invitation.getMemberId(),
                invitation.getId(),
                invitation.getTripId(),
                invitation.getStatus().name()
        );
    }
}
