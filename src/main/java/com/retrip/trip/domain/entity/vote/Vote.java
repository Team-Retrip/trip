package com.retrip.trip.domain.entity.vote;

import com.retrip.trip.domain.exception.common.IllegalStateException;
import com.retrip.trip.domain.exception.common.InvalidValueException;
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

import static com.retrip.trip.domain.vo.vote.VoteStatus.*;
import static com.retrip.trip.domain.vo.vote.VoteStatus.START;
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
    private UUID createdBy;

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
    public Vote(UUID tripId, UUID createdBy, VoteSummary summary,
                VoteSetting setting, VotePeriod period, VoteOptions voteOptions) {
        this.id = UUID.randomUUID();
        this.tripId = tripId;
        this.createdBy = createdBy;
        this.summary = summary;
        this.setting = setting;
        this.period = period;
        this.status = period.getVoteStatus(Instant.now());
        this.options = voteOptions;
        this.options.registerVote(this);
    }

    public boolean isClosable(Instant now) {
        return this.status != ENDED && period.isClosable(now);
    }

    public void open() {
        this.status = START;
    }

    public void close() {
        this.status = ENDED;
    }

    public void update(VoteSummary voteSummary,
                       VoteSetting voteSetting,
                       VotePeriod votePeriod,
                       VoteOptions voteOptions,
                       UUID memberId) {
        validateToUpdate(memberId);
        this.summary = voteSummary;
        this.setting = voteSetting;
        this.period = votePeriod;
        this.options = voteOptions;
    }

    private void validateToUpdate(UUID memberId) {
        if (this.createdBy != memberId) {
            throw new InvalidValueException("투표를 만든 사람이 아니면 수정할 수 없습니다.");
        }

        if (this.status.isImmutable()) {
            throw new IllegalStateException("투표를 수정할 수 없는 상태입니다.");
        }
    }
}
