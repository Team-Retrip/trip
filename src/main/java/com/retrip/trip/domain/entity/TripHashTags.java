package com.retrip.trip.domain.entity;

import static com.retrip.trip.domain.exception.common.ErrorCode.*;
import static lombok.AccessLevel.PROTECTED;

import com.retrip.trip.domain.exception.common.BusinessException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.OneToMany;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import java.util.stream.Collectors;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = PROTECTED, force = true)
@Embeddable
public class TripHashTags {
    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<TripHashTag> values = new ArrayList<>();

    public TripHashTags(Trip trip, List<String> hashTags) {
        validate(hashTags);
        values.addAll(hashTags.stream().map(m -> TripHashTag.of(trip, m)).toList());
    }

    private void validate(List<String> hashTags) {
        if (hashTags != null && !hashTags.isEmpty()) {
            new HashSet<>(hashTags).forEach(hashTag -> {
                if (hashTag.length() > 10 || hashTag.isEmpty()) {
                    throw new BusinessException(INVALID_HASHTAG_LENGTH);
                }
            });
        }
    }

    public List<String> getHashTagNames() {
        return this.values.stream()
                .map(TripHashTag::getName)
                .collect(Collectors.toList());
    }
}
