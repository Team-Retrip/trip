package com.retrip.trip.application.in.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

@Schema(description = "여행 리더 위임 Request")
public record DelegateLeaderRequest(
        @Schema(description = "현재 리더 멤버 ID")
        @NotNull
        UUID currentLeaderId,

        @Schema(description = "새로운 리더 멤버 ID")
        @NotNull
        UUID newLeaderId
) {
}