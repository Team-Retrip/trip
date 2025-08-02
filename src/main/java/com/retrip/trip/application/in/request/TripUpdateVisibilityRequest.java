package com.retrip.trip.application.in.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "여행 공개 여부 변경 Request")
public record TripUpdateVisibilityRequest(
        @Schema(description = "여행 공개 여부")
        boolean open,

        @Schema(description = "여행 참여 비밀번호")
        String password
) {

}
