package com.retrip.trip.infra.adapter.out.persistence.mysql.query;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.retrip.trip.application.in.response.ItineraryResponse;
import com.retrip.trip.application.out.repository.ItineraryQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.types.Projections.list;
import static com.querydsl.core.types.dsl.Expressions.set;
import static com.retrip.trip.domain.entity.QItinerary.itinerary;
import static com.retrip.trip.domain.entity.QItineraryDetail.itineraryDetail;
import static com.retrip.trip.domain.entity.QTrip.trip;

@RequiredArgsConstructor
@Repository
public class ItineraryQuerydslRepository implements ItineraryQueryRepository {
    private final JPAQueryFactory query;

    @Override
    public Page<ItineraryResponse> findItineraries(UUID tripId, Pageable page) {

        List<ItineraryResponse> itineraries = query.selectFrom(trip)
                .leftJoin(itinerary).on(itinerary.trip.eq(trip))
                .leftJoin(itineraryDetail).on(itineraryDetail.itinerary.eq(itinerary))
                .where(trip.id.eq(tripId))
                .offset(page.getOffset())
                .limit(page.getPageSize())
                .orderBy(itinerary.date.desc())
                .transform(
                        groupBy(itineraryDetail.itinerary).list( //list로 왜 안나오는지는 해결 후, 변경 예정
                                Projections.constructor(ItineraryResponse.class,
                                        itinerary.id,
                                        itinerary.date,
                                        itinerary.name,
                                        list(Projections.constructor(ItineraryResponse.ItineraryDetailResponse.class,
                                                itineraryDetail.id,
                                                itineraryDetail.description,
                                                itineraryDetail.price,
                                                itineraryDetail.locationId
                                        ))
                                )
                        )
                );
        return new PageImpl<>(itineraries, page, itineraries.size());
    }
}
