package com.retrip.trip.domain.entity.vote;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;

import static lombok.AccessLevel.PROTECTED;

@Getter
@Embeddable
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED, force = true)
public class VoteOptions {
    @OneToMany(mappedBy = "vote", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<VoteOption> values;

    public void registerVote(Vote vote) {
        Objects.requireNonNull(values).forEach(option -> option.registerVote(vote));
    }
}
