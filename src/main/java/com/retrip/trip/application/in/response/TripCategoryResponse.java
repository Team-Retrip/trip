package com.retrip.trip.application.in.response;

import com.retrip.trip.domain.vo.TripCategory;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "여행 카테고리 Response")
public record TripCategoryResponse(
        @Schema(description = "카테고리 코드")
        String code,

        @Schema(description = "카테고리 영문명")
        String engName,

        @Schema(description = "카테고리 한글명")
        String korName
) {
    public static TripCategoryResponse of(TripCategory categoryType) {
        return new TripCategoryResponse(
                categoryType.getCode(),
                categoryType.name().toLowerCase(),
                categoryType.getViewName()
        );
    }
}
