package com.retrip.trip.application.in.request;

import com.retrip.trip.domain.entity.Trip;
import com.retrip.trip.domain.vo.TripCategory;
import com.retrip.trip.domain.vo.TripDescription;
import com.retrip.trip.domain.vo.TripPeriod;
import com.retrip.trip.domain.vo.TripTitle;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.UUID;

public record TripCreateRequest(
        @NotNull
        UUID memberId,
        @NotNull
        UUID locationId,
        String title,
        String description,
        @FutureOrPresent
        LocalDate start,
        @FutureOrPresent
        LocalDate end,
        boolean open,
        int maxParticipants,
        TripCategory category

) {
    public Trip to() {
        return Trip.create(
                memberId,
                locationId,
                new TripTitle(title),
                new TripDescription(description),
                new TripPeriod(start, end),
                open,
                maxParticipants,
                category
        );
    }

    public Trip toWithItineraries() {
        return Trip.createWithItineraries(
                memberId,
                locationId,
                new TripTitle(title),
                new TripDescription(description),
                new TripPeriod(start, end),
                open,
                maxParticipants,
                category
        );
    }
}
