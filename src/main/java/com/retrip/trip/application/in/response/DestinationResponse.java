package com.retrip.trip.application.in.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.UUID;

@Schema(description = "여행지 응답")
public record DestinationResponse(
        @Schema(description = "여행지 ID")
        UUID destinationId,
        @Schema(description = "여행지 이름 (미구현, null 반환)")
        String destinationName
) {
    public static DestinationResponse of(UUID destinationId) {
        return new DestinationResponse(destinationId, null);
    }

    public static List<DestinationResponse> ofIds(List<UUID> destinationIds) {
        return destinationIds.stream()
                .map(DestinationResponse::of)
                .toList();
    }
}
