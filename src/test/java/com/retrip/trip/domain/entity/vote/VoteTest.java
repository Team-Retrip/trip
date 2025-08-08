package com.retrip.trip.domain.entity.vote;

import com.retrip.trip.domain.exception.common.IllegalStateException;
import com.retrip.trip.domain.exception.common.InvalidValueException;
import com.retrip.trip.domain.vo.vote.VoteStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.test.util.ReflectionTestUtils;

import static com.retrip.trip.domain.fixture.TripFixture.MEMBER_ID;
import static com.retrip.trip.domain.fixture.TripFixture.홍석_ID;
import static com.retrip.trip.domain.fixture.VoteFixture.vote;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.params.provider.EnumSource.Mode.INCLUDE;

class VoteTest {

    @ParameterizedTest
    @EnumSource(mode = INCLUDE, names = {"START", "ENDED"})
    void 투표가_시작되었거나_중료되었으면_수정할_수_없다(VoteStatus status) {
        // given
        Vote vote = vote();
        ReflectionTestUtils.setField(vote, "status", status);

        // when, then
        assertThatThrownBy(() ->
                vote.update(vote.getSummary(), vote.getSetting(), vote.getPeriod(), vote.getOptions(), MEMBER_ID))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void 투표를_만든_사람이_아니면_수정할_수_없다() {
        // given
        Vote vote = vote();

        // when, then
        assertThatThrownBy(() ->
                vote.update(vote.getSummary(), vote.getSetting(), vote.getPeriod(), vote.getOptions(), 홍석_ID))
                .isInstanceOf(InvalidValueException.class);
    }
}