package com.retrip.trip.application.in.response;

import com.retrip.trip.domain.entity.Trip;
import com.retrip.trip.domain.vo.TripInvitationStatus;

import java.util.List;
import java.util.UUID;

public record TripInvitationsCreateResponse(
        UUID tripId,
        List<TripInvitationCreateResponse> invitations
) {
    public static TripInvitationsCreateResponse of(Trip trip) {
        return new TripInvitationsCreateResponse(
                trip.getId(),
                trip.getInvitations().getValues().stream()
                        .map(v -> new TripInvitationCreateResponse(v.getMemberId(), v.getStatus()))
                        .toList()
        );
    }

    public record TripInvitationCreateResponse(
            UUID memberId,
            TripInvitationStatus status
    ) {
    }
}
