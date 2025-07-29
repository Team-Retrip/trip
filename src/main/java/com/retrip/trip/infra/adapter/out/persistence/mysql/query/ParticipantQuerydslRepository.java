package com.retrip.trip.infra.adapter.out.persistence.mysql.query;

import static com.retrip.trip.domain.entity.participant.QParticipant.participant;
import static com.retrip.trip.domain.vo.ParticipantStatus.ACTIVE;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.retrip.trip.application.out.repository.ParticipantQueryRepository;
import com.retrip.trip.domain.entity.participant.Participant;

import java.util.Optional;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Repository
public class ParticipantQuerydslRepository implements ParticipantQueryRepository {
    private final JPAQueryFactory query;

    @Override
    public Long findByMemberId(UUID memberId) {
        return query.select(participant.count())
                .from(participant)
                .where(
                        participant.memberId.eq(memberId), participant.status.eq(ACTIVE) // 수정된 부분
                        )
                .fetchOne();
    }

    @Override
    public List<Participant> findByMemberId(UUID memberId, Pageable page) {
        return query.select(participant)
                .from(participant)
                .where(
                        participant.memberId.eq(memberId), participant.status.eq(ACTIVE) // 수정된 부분
                        )
                .offset(page.getOffset())
                .limit(page.getPageSize())
                .orderBy(participant.createdAt.desc())
                .fetch();
    }

    @Override
    public Long findByTripIdCount(UUID tripId) {
        return query.select(participant.count())
                .from(participant)
                .where(participant.tripId.eq(tripId))
                .fetchOne();
    }

    @Override
    public Optional<Participant> findByTripIdAndMemberId(UUID tripId, UUID memberId) {
        return Optional.ofNullable(
                query.select(participant)
                        .from(participant)
                        .where(
                                participant.tripId.eq(tripId),
                                participant.memberId.eq(memberId),
                                participant.status.eq(ACTIVE) // 수정된 부분
                                )
                        .fetchOne());
    }

    @Override
    public Optional<Participant> findByTripIdAndMemberIdAndAllStatus(UUID tripId, UUID memberId) {
        return Optional.ofNullable(
                query.select(participant)
                        .from(participant)
                        .where(participant.tripId.eq(tripId), participant.memberId.eq(memberId))
                        .fetchOne());
    }
}
