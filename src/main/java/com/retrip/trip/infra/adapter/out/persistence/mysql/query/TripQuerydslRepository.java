package com.retrip.trip.infra.adapter.out.persistence.mysql.query;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.retrip.trip.application.in.response.TripResponse;
import com.retrip.trip.application.out.repository.TripQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.retrip.trip.domain.entity.QTrip.trip;

@RequiredArgsConstructor
@Repository
public class TripQuerydslRepository implements TripQueryRepository {
    private final JPAQueryFactory query;

    @Override
    public Page<TripResponse> findTrips(Pageable page) {
        List<TripResponse> trips = query.select(
                        Projections.constructor(TripResponse.class,
                                trip.id,
                                trip.leaderId,
                                trip.title,
                                trip.destinationId,
                                trip.period.start,
                                trip.period.end,
                                trip.open
                        )
                ).from(trip)
                .offset(page.getOffset())
                .limit(page.getPageSize())
                .orderBy(trip.createdAt.desc())
                .fetch();
        return new PageImpl<>(trips, page, trips.size());
    }
}
