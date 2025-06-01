package com.retrip.trip.application.in.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.UUID;

@Schema(description = "여행 목록 Response")
public record TripResponse(
        @Schema(description = "여행 ID", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "여행 제목", example = "파리 여행")
        String title,

        @Schema(description = "목적지 ID", example = "550e8400-e29b-41d4-a716-446655440001")
        UUID destinationId,

        @Schema(description = "여행 시작 날짜", example = "2025-06-15")
        LocalDate start,

        @Schema(description = "여행 종료 날짜", example = "2025-06-20")
        LocalDate end,

        @Schema(description = "여행 공개 여부", example = "true")
        boolean open
) {}
