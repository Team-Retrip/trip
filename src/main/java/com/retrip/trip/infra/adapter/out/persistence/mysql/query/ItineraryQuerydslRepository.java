package com.retrip.trip.infra.adapter.out.persistence.mysql.query;

import static com.retrip.trip.domain.entity.QItinerary.itinerary;
import static com.retrip.trip.domain.entity.QItineraryDetail.itineraryDetail;
import static com.retrip.trip.infra.adapter.util.PaginationUtils.checkEndPage;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.retrip.trip.application.in.response.ItineraryResponse;
import com.retrip.trip.application.out.repository.ItineraryQueryRepository;
import com.retrip.trip.domain.entity.Itinerary;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class ItineraryQuerydslRepository implements ItineraryQueryRepository {

    private final JPAQueryFactory query;

    @Override
    public Page<ItineraryResponse> findItineraries(UUID tripId, Pageable page) {
        List<Itinerary> result =
            query
                .selectFrom(itinerary)
                .leftJoin(itinerary.itineraryDetails.values, itineraryDetail)
                .fetchJoin()
                .where(itinerary.trip.id.eq(tripId))
                .offset(page.getOffset())
                .limit(page.getPageSize())
                .orderBy(itinerary.date.desc())
                .fetch();

        // Transform 필요
        List<ItineraryResponse> itineraries =
            result.stream()
                .map(
                    i ->
                        new ItineraryResponse(
                            i.getId(),
                            i.getDate(),
                            i.getName(),
                            i.getItineraryDetails() != null
                                ? i.getItineraryDetails().getValues().stream()
                                .map(
                                    id ->
                                        new ItineraryResponse.ItineraryDetailResponse(
                                            id.getId(),
                                            id.getDescription().getValue(),
                                            id.getPrice().getValue(),
                                            id.getLocationId()))
                                .toList()
                                : new ArrayList<>()))
                .toList();
        return checkEndPage(page, itineraries);
    }
}
