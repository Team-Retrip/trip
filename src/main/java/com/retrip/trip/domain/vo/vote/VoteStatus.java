package com.retrip.trip.domain.vo.vote;

public enum VoteStatus {
    CREATED, START, ENDED;

    public boolean isImmutable() {
        return this == START || this == ENDED;
    }
}
