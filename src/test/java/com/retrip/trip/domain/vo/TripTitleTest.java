package com.retrip.trip.domain.vo;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("TripTitle은 ")
class TripTitleTest {
    @Test
    void 최대_제한_길이가_존재한다() {
        //given
        String input = IntStream.range(0, 31)
                .mapToObj(i -> "A") //
                .collect(Collectors.joining(""));
        String expectedMessage = "여행 제목은 30자를 넘을 수 없습니다.";

        //when & then
        assertThatThrownBy(() -> new TripTitle(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(expectedMessage);
    }
}
