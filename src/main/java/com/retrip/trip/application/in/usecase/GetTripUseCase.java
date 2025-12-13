package com.retrip.trip.application.in.usecase;

import com.retrip.trip.application.in.response.MyTripResponse;
import com.retrip.trip.application.in.response.TripDetailResponse;
import com.retrip.trip.application.in.response.TripResponse;
import com.retrip.trip.domain.vo.TripStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface GetTripUseCase {
    Page<TripResponse> getTrips(Pageable page);
    TripDetailResponse getTripDetail(UUID memberId, UUID tripId);
    Page<MyTripResponse> getMyTrips(UUID memberId, TripStatus tripStatus, Pageable page);
}