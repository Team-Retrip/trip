package com.retrip.trip.application.in.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

@Schema(description = "여행 초대 요청")
public record TripInvitationsCreateRequest(
        @Schema(description = "초대할 회원 ID 목록 (최소 1명)", example = "[\"uuid1\", \"uuid2\"]")
        @Size(min = 1)
        List<UUID> memberIds
) {
}
