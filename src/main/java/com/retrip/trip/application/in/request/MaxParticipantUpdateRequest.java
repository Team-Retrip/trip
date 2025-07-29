package com.retrip.trip.application.in.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "여행 최대 참여자 수정 Request")
public record MaxParticipantUpdateRequest(
        @Schema(description = "여행 최대 참여자") int maxParticipants,
        @Schema(description = "참여자 수정 ID") UUID memberId) {}
