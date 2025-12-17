package com.retrip.trip.application.in.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "비공개 여행 비밀번호로 참여 Request")
public record TripJoinWithPasswordRequest(
        @Schema(description = "여행 참여 비밀번호")
        String password
) {
}
