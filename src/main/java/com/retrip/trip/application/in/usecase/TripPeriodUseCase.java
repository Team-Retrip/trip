package com.retrip.trip.application.in.usecase;

import com.retrip.trip.application.in.request.PeriodUpdateRequest;
import com.retrip.trip.application.in.response.PeriodUpdateResponse;

import java.util.UUID;

public interface TripPeriodUseCase {
    PeriodUpdateResponse updatePeriod(UUID memberId, UUID tripId, PeriodUpdateRequest request);
}
