package com.retrip.trip.domain.entity.vote;

import com.retrip.trip.domain.vo.vote.VotePeriod;
import com.retrip.trip.domain.vo.vote.VoteSetting;
import com.retrip.trip.domain.vo.vote.VoteStatus;
import com.retrip.trip.domain.vo.vote.VoteSummary;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED, force = true)
@Entity
public class Vote {
    @Version
    private long version;

    @Id
    @Column(columnDefinition = "varbinary(16)")
    private UUID id;
    private UUID tripId;

    @Embedded
    private VoteSummary summary;

    @Embedded
    private VoteSetting setting;

    @Embedded
    private VotePeriod period;

    @Enumerated(EnumType.STRING)
    private VoteStatus status;

    @Embedded
    private VoteOptions options;

    @Builder
    public Vote(UUID tripId, VoteSummary summary, VoteSetting setting,
                VotePeriod period, VoteOptions voteOptions) {
        this.id = UUID.randomUUID();
        this.tripId = tripId;
        this.summary = summary;
        this.setting = setting;
        this.period = period;
        this.status = period.getVoteStatus(Instant.now());
        this.options = voteOptions;
        this.options.registerVote(this);
    }

    public boolean isClosable(Instant now) {
        return this.status != VoteStatus.ENDED && period.isClosable(now);
    }

    public void close() {
        this.status = VoteStatus.ENDED;
    }
}
