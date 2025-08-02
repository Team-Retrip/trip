package com.retrip.trip.application.in.response;

import com.retrip.trip.domain.entity.Trip;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "여행 공개 여부 변경 response")
public record TripUpdateVisibilityResponse(
        @Schema(description = "여행 ID")
        UUID tripId,

        @Schema(description = "여행 공개 여부")
        boolean open,

        @Schema(description = "여행 참여 비밀번호")
        String password
) {
    public static TripUpdateVisibilityResponse of(Trip trip, String password) {
        return new TripUpdateVisibilityResponse(trip.getId(), trip.isOpen(), password);
    }
}
