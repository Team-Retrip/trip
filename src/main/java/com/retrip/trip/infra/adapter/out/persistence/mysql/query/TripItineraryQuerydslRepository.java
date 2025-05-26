package com.retrip.trip.infra.adapter.out.persistence.mysql.query;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.retrip.trip.application.in.response.ItineraryResponse;
import com.retrip.trip.application.out.repository.TripItineraryQueryRepository;
import com.retrip.trip.domain.entity.Itinerary;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.BatchSize;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.retrip.trip.domain.entity.QItinerary.itinerary;
import static com.retrip.trip.domain.entity.QItineraryDetail.itineraryDetail;
import static com.retrip.trip.domain.entity.QTrip.trip;
import static com.retrip.trip.infra.adapter.util.PaginationUtils.checkEndPage;

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
    public Optional<Itinerary> findByIdWithItineraryDetail(
            UUID itineraryId,
            UUID itineraryDetailsId) {
        return Optional.ofNullable(query
                .selectFrom(itinerary)
                .leftJoin(itineraryDetail)
                .on(itineraryDetail.itinerary.eq(itinerary))
                .fetchJoin()
                .where(itineraryEq(itineraryId), itineraryDetailEq(itineraryDetailsId))
                .fetchOne());
    }

    @Override
    public Optional<Itinerary> findByIdWithItineraryDetails(
            UUID itineraryId) {
        return Optional.ofNullable(query
                .selectFrom(itinerary)
                .leftJoin(itineraryDetail)
                .on(itineraryDetail.itinerary.eq(itinerary))
                .fetchJoin()
                .where(itineraryEq(itineraryId))
                .fetchOne());
    }

    @Override
    public Page<ItineraryResponse> findItineraries(UUID tripId, Pageable page) {
        List<Itinerary> result =
                query.selectDistinct(itinerary)
                        .from(itinerary)
                        .leftJoin(itinerary.itineraryDetails.values, itineraryDetail)
                        .where(itinerary.trip.id.eq(tripId))
                        .offset(page.getOffset())
                        .limit(page.getPageSize())
                        .orderBy(itinerary.date.asc())
                        .fetch();

        // Transform 필요
        List<ItineraryResponse> itineraries =
                result.stream()
                        .map(i ->
                                new ItineraryResponse(
                                        i.getId(),
                                        i.getDate(),
                                        i.getName(),
                                        i.getItineraryDetails() != null
                                                ? i
                                                .getItineraryDetails()
                                                .getValues()
                                                .stream()
                                                .map(
                                                        id ->
                                                                new ItineraryResponse
                                                                        .ItineraryDetailResponse(
                                                                        id.getId(),
                                                                        id.getDescription().getValue(),
                                                                        id.getTime().getValue(),
                                                                        id.getPrice().getValue(),
                                                                        id.getLocationId()
                                                                )
                                                )
                                                .toList()
                                                : new ArrayList<>()))
                        .toList();

        return checkEndPage(page, itineraries);
    }


    private static BooleanExpression tripEq(UUID tripId) {
        return trip.id.eq(tripId);
    }

    private static BooleanExpression itineraryEq(UUID itineraryId) {
        return itinerary.id.eq(itineraryId);
    }

    private static BooleanExpression itineraryDetailEq(UUID itineraryDetailsId) {
        return itineraryDetail.id.eq(itineraryDetailsId);
    }

}
