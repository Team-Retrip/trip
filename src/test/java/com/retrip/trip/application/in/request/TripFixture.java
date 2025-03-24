package com.retrip.trip.application.in.request;

import com.retrip.trip.domain.vo.TripCategory;

import java.time.LocalDate;
import java.util.UUID;

public class TripFixture {
    public static TripCreateRequest createRequest(
            UUID memberId,
            UUID locationId,
            String title,
            String description,
            LocalDate start,
            LocalDate end,
            boolean open,
            int maxParticipants,
            TripCategory tripCategory
    ) {
        return new TripCreateRequest(
                memberId,
                locationId,
                title,
                description,
                start,
                end,
                open,
                maxParticipants,
                tripCategory);
    }
}
