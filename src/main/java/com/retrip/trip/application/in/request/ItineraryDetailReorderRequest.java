package com.retrip.trip.application.in.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;
import java.util.UUID;

@Schema(description = "세부일정 순서 변경 Request")
public record ItineraryDetailReorderRequest(
        @NotEmpty
        @Schema(description = "변경할 순서대로 정렬된 세부일정 ID 목록")
        List<UUID> orderedIds
) {}