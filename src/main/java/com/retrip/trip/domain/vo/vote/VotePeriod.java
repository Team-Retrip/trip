package com.retrip.trip.domain.vo.vote;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Objects;

import static lombok.AccessLevel.PROTECTED;

@Getter
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = PROTECTED, force = true)
public class VotePeriod {
    @Column(name = "open_time", columnDefinition = "TIMESTAMP(3)")
    private final Instant openTIme;
    @Column(name = "end_time", columnDefinition = "TIMESTAMP(3)")
    private final Instant endTime;
    private final ZoneId timezone;

    public VotePeriod(Instant openTIme, Instant endTime, ZoneId timezone) {
        this.openTIme = openTIme;
        this.endTime = endTime;
        this.timezone = timezone;
    }

    public VoteStatus getVoteStatus(Instant now) {
        if (now.isBefore(Objects.requireNonNull(openTIme))) {
            return VoteStatus.CREATED;
        }

        if (!now.isAfter(Objects.requireNonNull(endTime))) {
            return VoteStatus.OPEN;
        }

        return VoteStatus.ENDED;
    }

    public boolean isClosable(Instant now) {
        return now.isAfter(Objects.requireNonNull(this.endTime));
    }
}
