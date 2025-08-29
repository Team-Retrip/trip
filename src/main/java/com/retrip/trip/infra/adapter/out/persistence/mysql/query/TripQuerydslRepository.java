package com.retrip.trip.infra.adapter.out.persistence.mysql.query;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.retrip.trip.application.in.response.TripResponse;
import com.retrip.trip.application.out.repository.TripQueryRepository;
import com.retrip.trip.domain.entity.QTrip;
import com.retrip.trip.domain.entity.Trip;
import com.retrip.trip.domain.entity.TripHashTag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.retrip.trip.domain.entity.QItinerary.itinerary;
import static com.retrip.trip.domain.entity.QTrip.trip;
import static com.retrip.trip.domain.entity.QTripHashTag.tripHashTag;
import static com.retrip.trip.domain.entity.QTripParticipant.tripParticipant;
import static com.retrip.trip.domain.vo.ParticipantStatus.ACTIVE;

@RequiredArgsConstructor
@Repository
public class TripQuerydslRepository implements TripQueryRepository {
    private final JPAQueryFactory query;

    @Override
    public List<Trip> findTrips(Pageable page) {
        return query
                .selectFrom(trip)
                .offset(page.getOffset())
                .limit(page.getPageSize())
                .orderBy(trip.createdAt.desc())
                .fetch();
    }

    @Override
    public List<TripHashTag> findHashTags(List<Trip> trips) {
        return query
                .selectFrom(tripHashTag)
                .join(tripHashTag.trip, trip)
                .where(trip.in(trips))
                .orderBy(trip.createdAt.desc())
                .fetch();
    }

    @Override
    public Optional<Trip> findByIdWithItineraries(UUID tripId) {
        return Optional.ofNullable(
                query
                        .selectFrom(trip)
                        .leftJoin(itinerary)
                        .on(itinerary.trip.eq(trip))
                        .fetchJoin()
                        .where(trip.id.eq(tripId))
                        .orderBy(itinerary.date.desc())
                        .fetchOne());
    }

    @Override
    public Page<TripResponse> findMyTrips(UUID memberId, Pageable page) {
        List<TripResponse> trips =
                query
                        .select(
                                Projections.fields(
                                        TripResponse.class,
                                        trip.id.as("id"),
                                        trip.title.value.as("title"),
                                        trip.destinationId.as("destinationId"),
                                        trip.period.start.as("start"),
                                        trip.period.end.as("end"),
                                        trip.open.as("open")))
                        .from(trip)
                        .join(trip.tripParticipants.values, tripParticipant)
                        .where(
                                tripParticipant.memberId.eq(memberId),
                                tripParticipant.status.eq(ACTIVE) // 수정된 부분
                        )
                        .offset(page.getOffset())
                        .limit(page.getPageSize())
                        .orderBy(trip.createdAt.desc())
                        .fetch();

        Long total = query
                .select(trip.count())
                .from(trip)
                .join(trip.tripParticipants.values, tripParticipant)
                .where(
                        tripParticipant.memberId.eq(memberId),
                        tripParticipant.status.eq(ACTIVE) // 수정된 부분
                )
                .fetchOne();

        return new PageImpl<>(trips, page, total == null ? 0 : total);
    }
}
