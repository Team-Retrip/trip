package com.retrip.trip.infra.adapter.out.persistence.mysql.query;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.retrip.trip.application.in.response.MyTripResponse;
import com.retrip.trip.application.out.repository.TripQueryRepository;
import com.retrip.trip.domain.entity.QTripHashTag;
import com.retrip.trip.domain.entity.QTripParticipant;
import com.retrip.trip.domain.entity.Trip;
import com.retrip.trip.domain.entity.TripHashTag;
import com.retrip.trip.domain.vo.TripStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

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
    public Page<Trip> findTrips(List<TripStatus> tripStatuses, List<String> genders, List<String> ages, Pageable page) {
        BooleanExpression tagFilter = hashTagFilter(genders, ages);

        JPAQuery<Trip> contentQuery = query.selectFrom(trip);
        JPAQuery<Long> countQuery = query.select(trip.countDistinct()).from(trip);

        if (tagFilter != null) {
            contentQuery.leftJoin(trip.hashTags.values, tripHashTag).where(tagFilter);
            countQuery.leftJoin(trip.hashTags.values, tripHashTag).where(tagFilter);
        }

        List<Trip> content = contentQuery
                .where(tripStatusCondition(tripStatuses))
                .distinct()
                .orderBy(trip.createdAt.desc())
                .offset(page.getOffset())
                .limit(page.getPageSize())
                .fetch();

        countQuery.where(tripStatusCondition(tripStatuses));
        return PageableExecutionUtils.getPage(content, page, countQuery::fetchOne);
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
    public Page<MyTripResponse> findMyTrips(UUID memberId,
                                            List<TripStatus> tripStatuses,
                                            List<String> genders,
                                            List<String> ages,
                                            Pageable page) {
        QTripParticipant me = new QTripParticipant("me");
        QTripParticipant participant = new QTripParticipant("participant");
        QTripHashTag hashTag = QTripHashTag.tripHashTag;

        List<MyTripResponse> content =
                query
                        .select(
                                Projections.constructor(
                                        MyTripResponse.class,
                                        trip.id,
                                        trip.title.value,
                                        trip.imageUrl,
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
                        // 해시태그 필터링을 위한 조인
                        .leftJoin(trip.hashTags.values, hashTag)
                        .where(
                                tripStatusCondition(tripStatuses),
                                hashTagFilter(genders, ages)
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

    private BooleanExpression tripStatusCondition(List<TripStatus> tripStatuses) {
        //TODO: 정책 확인해봐야함
//        if (tripStatuses == null || tripStatuses.isEmpty()) {
//            return trip.status.ne(TripStatus.COMPLETED);
//        }
        if (tripStatuses == null || tripStatuses.isEmpty()) {
            return null;
        }
        return trip.status.in(tripStatuses);
    }

    private BooleanExpression hashTagFilter(List<String> genders, List<String> ages) {
        List<String> allTags = Stream.of(
                        Optional.ofNullable(genders).orElse(List.of()),
                        Optional.ofNullable(ages).orElse(List.of())
                )
                .flatMap(List::stream)
                .toList();

        return allTags.isEmpty() ? null : QTripHashTag.tripHashTag.name.in(allTags);
    }
}
