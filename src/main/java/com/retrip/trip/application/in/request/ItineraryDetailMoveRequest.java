package com.retrip.trip.application.in.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Schema(description = "세부일정 날짜 이동 Request")
public record ItineraryDetailMoveRequest(
        @NotNull
        @Schema(description = "이동할 대상 일정 ID")
        UUID targetItineraryId,

        @NotNull
        @Schema(description = "대상 일정에서 삽입할 위치 (0부터 시작, 해당 위치에 끼워넣기)")
        Integer targetSortOrder
) {}