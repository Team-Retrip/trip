package com.retrip.trip.domain.vo;

import com.retrip.trip.domain.exception.InvalidTripPasswordException;
import org.junit.jupiter.api.Test;

import static com.retrip.trip.domain.vo.TripPassword.PASSWORD_MAX_LENGTH;
import static com.retrip.trip.domain.vo.TripPassword.PASSWORD_MIN_LENGTH;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class TripPasswordTest {
    @Test
    void 비밀번호가_최소길이_미만이면_발생한다() {
        assertThatThrownBy(() -> new TripPassword("a".repeat(PASSWORD_MIN_LENGTH - 1), "passwordHash"))
                .isExactlyInstanceOf(InvalidTripPasswordException.class);
    }

    @Test
    void 비밀번호가_최대길이_초과면_예외가_발생한다() {
        assertThatThrownBy(() -> new TripPassword("a".repeat(PASSWORD_MAX_LENGTH + 1), "passwordHash"))
                .isExactlyInstanceOf(InvalidTripPasswordException.class);
    }
}