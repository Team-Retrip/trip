package com.retrip.trip.application.in.response;

import com.retrip.trip.domain.entity.TripInvitation;

import java.util.UUID;

public record MemberTripInvitationsResponse(
        UUID memberId,
        UUID tripInvitationId,
        UUID tripId,
        String status
) {
    public static MemberTripInvitationsResponse of(TripInvitation invitation) {
        return new MemberTripInvitationsResponse(
                invitation.getMemberId(),
                invitation.getId(),
                invitation.getTrip().getId(),
                invitation.getStatus().name()
        );
    }
}
