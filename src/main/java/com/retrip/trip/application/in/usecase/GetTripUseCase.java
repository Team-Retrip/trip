package com.retrip.trip.application.in.usecase;

import com.retrip.trip.application.in.response.MyTripResponse;
import com.retrip.trip.application.in.response.TripResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface GetTripUseCase {
    Page<TripResponse> getTrips(Pageable page);

    Page<MyTripResponse> getMyTrips(UUID memberId, Pageable page);
}
