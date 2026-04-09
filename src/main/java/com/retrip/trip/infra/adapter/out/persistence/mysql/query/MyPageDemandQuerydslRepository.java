package com.retrip.trip.infra.adapter.out.persistence.mysql.query;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.retrip.trip.application.out.repository.MyPageDemandQueryRepository;
import com.retrip.trip.domain.entity.demand.Demand;
import com.retrip.trip.domain.vo.TripCategory;
import com.retrip.trip.domain.vo.TripStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.retrip.trip.domain.entity.QTrip.trip;
import static com.retrip.trip.domain.entity.demand.QDemand.demand;

@RequiredArgsConstructor
@Repository
public class MyPageDemandQuerydslRepository implements MyPageDemandQueryRepository {

    private final JPAQueryFactory query;

    @Override
    public Page<Demand> findMyPageDemands(
            UUID memberId,
            List<TripStatus> tripStatuses,
            TripCategory category,
            String period,
            Pageable pageable
    ) {
        List<Demand> content = query
                .selectFrom(demand)
                .join(trip).on(demand.tripId.eq(trip.id))
                .where(
                        demand.memberId.eq(memberId),
                        tripStatusCondition(tripStatuses),
                        tripCategoryCondition(category),
                        periodCondition(period)
                )
                .orderBy(demand.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = query
                .select(demand.count())
                .from(demand)
                .join(trip).on(demand.tripId.eq(trip.id))
                .where(
                        demand.memberId.eq(memberId),
                        tripStatusCondition(tripStatuses),
                        tripCategoryCondition(category),
                        periodCondition(period)
                );

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    private BooleanExpression tripStatusCondition(List<TripStatus> tripStatuses) {
        if (tripStatuses == null || tripStatuses.isEmpty()) {
            return null;
        }
        return trip.status.in(tripStatuses);
    }

    private BooleanExpression tripCategoryCondition(TripCategory category) {
        if (category == null) {
            return null;
        }
        return trip.category.eq(category);
    }

    private BooleanExpression periodCondition(String period) {
        if (period == null || period.isBlank()) {
            return null;
        }
        if ("RECENT_6_MONTHS".equalsIgnoreCase(period)) {
            return demand.createdAt.goe(LocalDateTime.now().minusMonths(6));
        }
        try {
            int year = Integer.parseInt(period);
            return demand.createdAt.year().eq(year);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
