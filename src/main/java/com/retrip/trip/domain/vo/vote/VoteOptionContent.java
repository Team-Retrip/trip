package com.retrip.trip.domain.vo.vote;

import com.retrip.trip.domain.exception.common.InvalidValueException;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.util.Strings;

import static lombok.AccessLevel.PROTECTED;

@Getter
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = PROTECTED, force = true)
public class VoteOptionContent {
    private static final int MIN_LENGTH = 1;
    private static final int MAX_LENGTH = 30;
    private final String value;

    public VoteOptionContent(String value) {
        validation(value);
        this.value = value;
    }

    private void validation(String value) {
        if (Strings.isEmpty(value)) {
            throw new InvalidValueException("투표 항목이 비어있습니다.");
        }

        if (value.length() < MIN_LENGTH || value.length() > MAX_LENGTH) {
            throw new InvalidValueException("투표 항목은 " + MIN_LENGTH + "자 이상, " + MAX_LENGTH + "자 이하여야 합니다.");
        }
    }
}
