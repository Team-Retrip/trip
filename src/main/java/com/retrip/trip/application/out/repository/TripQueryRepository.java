package com.retrip.trip.application.out.repository;

import com.retrip.trip.application.in.response.MyTripResponse;
import com.retrip.trip.domain.entity.Trip;

import com.retrip.trip.domain.entity.TripHashTag;

import com.retrip.trip.domain.vo.TripStatus;
import java.util.List;
import java.util.Optional;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TripQueryRepository {
    List<Trip> findTrips(Pageable page);

    Page<MyTripResponse> findMyTrips(UUID memberId, TripStatus tripStatus, Pageable page);

    Optional<Trip> findByIdWithItineraries(UUID tripId);

    List<TripHashTag> findHashTags(List<Trip> trips);
}
