package com.retrip.trip.infra.adapter.out.persistence.mysql.query;

import static com.querydsl.jpa.JPAExpressions.selectFrom;
import static com.retrip.trip.domain.entity.QItinerary.itinerary;
import static com.retrip.trip.domain.entity.QItineraryDetail.itineraryDetail;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.retrip.trip.application.out.repository.TripItineraryQueryRepository;
import com.retrip.trip.domain.entity.Itinerary;

import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.BatchSize;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class TripItineraryQuerydslRepository implements TripItineraryQueryRepository {
    private final JPAQueryFactory query;

    @Override
    @BatchSize(size = 31)
    public List<Itinerary> findByIdsWithItineraryDetails(List<UUID> ids) {
        return query
                .selectFrom(itinerary)
                .leftJoin(itineraryDetail)
                .on(itineraryDetail.itinerary.eq(itinerary))
                .fetchJoin()
                .where(itinerary.id.in(ids))
                .fetch();
    }
}
