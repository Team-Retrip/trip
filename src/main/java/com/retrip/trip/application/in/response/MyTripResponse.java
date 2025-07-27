package com.retrip.trip.application.in.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.UUID;

@Schema(description = "여행 목록 Response")
public record MyTripResponse(
        @Schema(description = "여행 ID") UUID id,
        @Schema(description = "여행 제목") String title,
        @Schema(description = "목적지 ID") UUID destinationId,
        @Schema(description = "여행 시작 날짜") LocalDate start,
        @Schema(description = "여행 종료 날짜") LocalDate end,
        @Schema(description = "여행 최대 참여자") int maxParticipants,
        @Schema(description = "여행 공개 여부") boolean open) {}
