package com.retrip.trip.application.in.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.UUID;

@Schema(description = "여행 멤버 강퇴 Request")
public record TripMemberBanRequest(
        @Schema(description = "강퇴할 멤버 ID 리스트 ")
        @Size(min = 1)
        List<UUID> memberIds
) {
}


