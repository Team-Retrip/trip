package com.retrip.trip.infra.adapter.out.persistence.mysql.query;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.retrip.trip.application.in.response.TripResponse;
import com.retrip.trip.application.out.repository.TripQueryRepository;
import com.retrip.trip.domain.entity.Trip;

import java.time.LocalDate;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.retrip.trip.domain.entity.QItinerary.itinerary;
import static com.retrip.trip.domain.entity.QItineraryDetail.itineraryDetail;
import static com.retrip.trip.domain.entity.QTrip.trip;

@RequiredArgsConstructor
@Repository
public class TripQuerydslRepository implements TripQueryRepository {
    private final JPAQueryFactory query;

    @Override
    public Page<TripResponse> findTrips(Pageable page) {
        List<TripResponse> trips =
                query.select(
                                Projections.constructor(
                                        TripResponse.class,
                                        trip.id,
                                        trip.title.value,
                                        trip.destinationId,
                                        trip.period.start,
                                        trip.period.end,
                                        trip.open))
                        .from(trip)
                        .offset(page.getOffset())
                        .limit(page.getPageSize())
                        .orderBy(trip.createdAt.desc())
                        .fetch();
        return new PageImpl<>(trips, page, trips.size());
    }

    @Override
    public Optional<Trip> findByIdWithItineraries(UUID tripId) {
        return Optional.ofNullable(
                query.selectFrom(trip)
                        .leftJoin(itinerary)
                        .on(itinerary.trip.eq(trip))
                        .fetchJoin()
                        .where(trip.id.eq(tripId))
                        .orderBy(itinerary.date.desc())
                        .fetchOne());
    }

    @Override
    public Optional<Trip> findByTripIdAndDates(UUID tripId, List<LocalDate> dates) {
        return Optional.ofNullable(
                query.selectFrom(trip)
                        .leftJoin(trip.itineraries.values, itinerary)
                        .fetchJoin()
                        .leftJoin(itinerary.itineraryDetails.values, itineraryDetail)
                        .fetchJoin()
                        .where(itinerary.trip.id.eq(tripId), itinerary.date.in(dates))
                        .fetchOne());
    }
}
