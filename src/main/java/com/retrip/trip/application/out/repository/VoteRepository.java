package com.retrip.trip.application.out.repository;

import com.retrip.trip.domain.entity.vote.Vote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface VoteRepository extends JpaRepository<Vote, UUID> {
}
