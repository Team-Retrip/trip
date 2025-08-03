package com.retrip.trip.application.in.response;

import com.retrip.trip.domain.entity.Itineraries;
import com.retrip.trip.domain.entity.Trip;
import com.retrip.trip.domain.vo.TripStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Schema(description = "여행 확정 요청 수락 Response")
public record ConfirmationDemandAcceptResponse(
        @Schema(description = "여행 ID")
        UUID tripId,

        @Schema(description = "여행 종료 날짜")
        TripStatus status

) {
    public static ConfirmationDemandAcceptResponse of(Trip trip) {
        return new ConfirmationDemandAcceptResponse(trip.getId(), trip.getStatus());
    }
}