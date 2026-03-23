package com.retrip.trip.infra.adapter.out.persistence.mysql.query;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.retrip.trip.application.in.response.ItineraryResponse;
import com.retrip.trip.application.out.repository.TripItineraryQueryRepository;
import com.retrip.trip.domain.entity.Itinerary;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.BatchSize;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.retrip.trip.domain.entity.QItinerary.itinerary;
import static com.retrip.trip.domain.entity.QItineraryDetail.itineraryDetail;

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

    @Override
    public Optional<Itinerary> findByIdWithItineraryDetail(UUID itineraryId, UUID itineraryDetailsId) {
        return Optional.ofNullable(query
                .selectFrom(itinerary)
                .leftJoin(itineraryDetail)
                .on(itineraryDetail.itinerary.eq(itinerary))
                .fetchJoin()
                .where(itineraryEq(itineraryId), itineraryDetailEq(itineraryDetailsId))
                .fetchOne());
    }

    @Override
    public Optional<Itinerary> findByIdWithItineraryDetails(UUID itineraryId) {
        return Optional.ofNullable(query
                .selectFrom(itinerary)
                .leftJoin(itineraryDetail)
                .on(itineraryDetail.itinerary.eq(itinerary))
                .fetchJoin()
                .where(itineraryEq(itineraryId))
                .fetchOne());
    }

    @Override
    public List<ItineraryResponse> findItineraries(UUID tripId) {
        List<Itinerary> result = query.selectDistinct(itinerary)
                .from(itinerary)
                .leftJoin(itinerary.itineraryDetails.values, itineraryDetail)
                .where(itinerary.trip.id.eq(tripId))
                .orderBy(itinerary.date.asc())
                .fetch();

        return result.stream()
                .map(i -> new ItineraryResponse(
                        i.getId(),
                        i.getDate(),
                        i.getName(),
                        i.getItineraryDetails() != null
                                ? i.getItineraryDetails().getValues().stream()
                                        .sorted(Comparator.comparingInt(d -> d.getSortOrder()))
                                        .map(d -> new ItineraryResponse.ItineraryDetailResponse(
                                                d.getId(),
                                                d.getLocationId(),
                                                null, // TODO: map service API 호출하여 locationId → locationName 조회
                                                d.getTimeValue(),
                                                d.getMemoValue(),
                                                d.getSortOrder()
                                        ))
                                        .toList()
                                : new ArrayList<>()
                ))
                .toList();
    }

    private static BooleanExpression itineraryEq(UUID itineraryId) {
        return itinerary.id.eq(itineraryId);
    }

    private static BooleanExpression itineraryDetailEq(UUID itineraryDetailsId) {
        return itineraryDetail.id.eq(itineraryDetailsId);
    }
}