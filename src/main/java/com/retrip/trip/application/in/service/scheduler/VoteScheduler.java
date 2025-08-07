package com.retrip.trip.application.in.service.scheduler;

import com.retrip.trip.application.out.repository.VoteRepository;
import com.retrip.trip.domain.entity.vote.Vote;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

import static com.retrip.trip.domain.vo.vote.VoteStatus.OPEN;

@Component
@RequiredArgsConstructor
public class VoteScheduler {
    private final VoteRepository voteRepository;

    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void closeExpiredVotes() {
        Instant now = Instant.now();
        List<Vote> votesToClose = voteRepository.findClosableVotes(OPEN, now);
        votesToClose.stream()
                .filter(vote -> vote.isClosable(now))
                .forEach(Vote::close);
    }
}
