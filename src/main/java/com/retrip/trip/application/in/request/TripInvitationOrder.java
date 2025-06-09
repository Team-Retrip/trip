package com.retrip.trip.application.in.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TripInvitationOrder {
    DATE("createdAt");

    private final String field;
}
