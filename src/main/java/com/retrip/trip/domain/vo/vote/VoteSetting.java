package com.retrip.trip.domain.vo.vote;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@Getter
@Embeddable
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED, force = true)
public class VoteSetting {
    private final boolean anonymous;
    private final int maxSelections;
    private final boolean allowAddOption;
}
