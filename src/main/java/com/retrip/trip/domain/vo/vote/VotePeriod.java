package com.retrip.trip.domain.vo.vote;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.ZoneId;

import static lombok.AccessLevel.PROTECTED;

@Getter
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = PROTECTED, force = true)
public class VotePeriod {
    @Column(name = "opend_at", columnDefinition = "TIMESTAMP(3)")
    private final Instant openedAt;
    @Column(name = "expires_at", columnDefinition = "TIMESTAMP(3)")
    private final Instant expiresAt;
    private final ZoneId timezone;

    public VotePeriod(Instant expiresAt, ZoneId timezone) {
        this.expiresAt = expiresAt;
        this.openedAt = Instant.now();
        this.timezone = timezone;
    }
}
