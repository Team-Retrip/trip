package com.retrip.trip.domain.vo.vote;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@Getter
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = PROTECTED, force = true)
public class VoteSetting {
    private final boolean anonymous;
    private final int maxSelections;
    private final boolean allowAddOption;

    public VoteSetting(boolean anonymous, int maxSelections, boolean allowAddOption, int voteOptionSize) {
        validate(maxSelections, voteOptionSize);
        this.anonymous = anonymous;
        this.maxSelections = maxSelections;
        this.allowAddOption = allowAddOption;
    }

    private void validate(int maxSelections, int voteOptionSize) {

    }
}
