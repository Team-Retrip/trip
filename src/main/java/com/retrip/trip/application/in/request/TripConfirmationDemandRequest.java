package com.retrip.trip.application.in.request;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

@Schema(description = "여행 확정 Request")
public record TripConfirmationDemandRequest(

        LocalDate startDate,
        LocalDate endDate
) {
}
