package com.retrip.trip.application.out.repository;

import com.retrip.trip.application.in.response.TripResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TripQueryRepository {
    Page<TripResponse> findTrips(Pageable page);
}
