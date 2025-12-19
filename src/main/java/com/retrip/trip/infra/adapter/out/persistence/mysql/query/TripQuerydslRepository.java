package com.retrip.trip.infra.adapter.out.persistence.mysql.query;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.retrip.trip.application.in.response.MyTripResponse;
import com.retrip.trip.application.out.repository.TripQueryRepository;
import com.retrip.trip.domain.entity.QTrip;
import com.retrip.trip.domain.entity.QTripParticipant;
import com.retrip.trip.domain.entity.Trip;
import com.retrip.trip.domain.entity.TripHashTag;
import com.retrip.trip.domain.vo.TripStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.retrip.trip.domain.entity.QItinerary.itinerary;
import static com.retrip.trip.domain.entity.QTrip.trip;
import static com.retrip.trip.domain.entity.QTripHashTag.tripHashTag;
import static com.retrip.trip.domain.vo.ParticipantRole.LEADER;
import static com.retrip.trip.domain.vo.ParticipantStatus.ACTIVE;
import static com.retrip.trip.infra.adapter.util.PaginationUtils.checkEndPage;

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
    public Page<MyTripResponse> findMyTrips(UUID memberId, TripStatus tripStatus, Pageable page) {
        QTripParticipant me = new QTripParticipant("me");
        QTripParticipant participant = new QTripParticipant("participant");

        List<MyTripResponse> content =
                query
                        .select(
                                Projections.constructor(
                                        MyTripResponse.class,
                                        trip.id,
                                        trip.title.value,
                                        Expressions.nullExpression(String.class), // imageUrl (추후 확장)
                                        trip.status,
                                        participant.id.count().intValue(),
                                        trip.tripParticipants.maxParticipants,
                                        trip.period.start,
                                        trip.period.end,
                                        me.role.eq(LEADER)
                                )
                        )
                        .from(trip)
                        // 내가 참여한 여행만 필터
                        .join(trip.tripParticipants.values, me).on(me.memberId.eq(memberId), me.status.eq(ACTIVE))
                        // 전체 참가자 집계용
                        .join(trip.tripParticipants.values, participant).on(participant.status.eq(ACTIVE))
                        .where(
                                tripStatusCondition(tripStatus)
                        )
                        .groupBy(
                                trip.id,
                                trip.title.value,
                                trip.status,
                                trip.tripParticipants.maxParticipants,
                                trip.period.start,
                                trip.period.end,
                                me.role
                        )
                        .orderBy(trip.createdAt.desc())
                        .offset(page.getOffset())
                        .limit(page.getPageSize())
                        .fetch();
        return checkEndPage(page, content);
    }

    private BooleanExpression tripStatusCondition(TripStatus tripStatus) {
        if (tripStatus == null) {
            return trip.status.ne(TripStatus.COMPLETED);
        }
        return trip.status.eq(tripStatus);
    }
}
