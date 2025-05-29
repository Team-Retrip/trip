package com.retrip.trip.application.in.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

public record TripInvitationsCreateRequest(
        @NotNull
        UUID tripId,

        @NotNull
        UUID leaderId,

        @Size(min = 1)
        List<UUID> memberIds
) {
}
