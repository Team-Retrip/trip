package com.retrip.trip.domain.vo;

import java.util.Arrays;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TripCategory {

    DOMESTIC("DOMESTIC", "국내"),
    OVERSEAS("OVERSEAS", "해외")
    ;

    private final String code;
    private final String viewName;

    public static TripCategory codeOf(String code) {
        return Arrays.stream(TripCategory.values())
                .filter(tripCategory -> tripCategory.getCode().equals(code))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 코드입니다."));
    }
}
