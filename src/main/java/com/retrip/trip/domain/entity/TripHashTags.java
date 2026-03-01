package com.retrip.trip.domain.entity;

import static com.retrip.trip.domain.exception.common.ErrorCode.*;
import static lombok.AccessLevel.PROTECTED;

import com.retrip.trip.domain.exception.common.BusinessException;
import com.retrip.trip.domain.vo.HashTagInfo;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.OneToMany;

import jakarta.persistence.OrderBy;
import java.util.ArrayList;
import java.util.Comparator;
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
    @OrderBy("tagOrder ASC")
    private final List<TripHashTag> values = new ArrayList<>();

    public TripHashTags(Trip trip, List<HashTagInfo> hashTags) {
        validate(hashTags);
        values.addAll(hashTags.stream()
                .sorted(Comparator.comparingInt(HashTagInfo::order))
                .map(h -> TripHashTag.of(trip, h.tag(), h.order()))
                .toList());
    }

    private void validate(List<HashTagInfo> hashTags) {
        if (hashTags != null && !hashTags.isEmpty()) {
            hashTags.forEach(h -> {
                if (h.tag().isEmpty() || h.tag().length() > 10) {
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
