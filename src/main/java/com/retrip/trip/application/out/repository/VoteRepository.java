package com.retrip.trip.application.out.repository;

import com.retrip.trip.domain.entity.vote.Vote;
import com.retrip.trip.domain.vo.vote.VoteStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface VoteRepository extends JpaRepository<Vote, UUID> {
    @Query("SELECT v FROM Vote v WHERE v.status = :status AND v.period.openTIme <= :now")
    List<Vote> findStartableVotes(VoteStatus status, Instant now);

    @Query("SELECT v FROM Vote v WHERE v.status = :status AND v.period.endTime < :now")
    List<Vote> findClosableVotes(VoteStatus status, Instant now);
}
