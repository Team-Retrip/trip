package com.retrip.trip.application.in.response;

import com.retrip.trip.domain.vo.TripCategory;

public record TripCategoryResponse(String code, String engName, String korName) {

    public static TripCategoryResponse of(TripCategory categoryType) {
        return new TripCategoryResponse(
                categoryType.getCode(),
                categoryType.name().toLowerCase(),
                categoryType.getViewName());
    }
}
