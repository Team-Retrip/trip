package com.retrip.trip.application.in.request;

import com.retrip.trip.domain.entity.Trip;
import com.retrip.trip.domain.entity.TripDestinations;
import com.retrip.trip.domain.entity.TripHashTags;
import com.retrip.trip.domain.vo.*;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Schema(description = "여행 수정 Request")
public record TripUpdateRequest(
        @Schema(description = "여행 제목", example = "유럽 배낭여행")
        @NotNull
        String title,

        @Schema(description = "여행 설명", example = "파리, 런던, 로마를 여행하는 일정입니다.")
        String description,

        @Schema(description = "여행 위치 ID", example = "550e8400-e29b-41d4-a716-446655440001")
        @NotNull
        List<UUID> destinationIds,

        @Schema(description = "여행 시작 날짜", example = "2025-06-15")
        @FutureOrPresent
        LocalDate start,

        @Schema(description = "여행 종료 날짜", example = "2025-06-25")
        @FutureOrPresent
        LocalDate end,

        @Schema(description = "여행 최대 참가 인원")
        Integer maxParticipants,

        @Schema(description = "HashTags")
        List<HashTagInput> hashTags,

        @Schema(description = "여행 대표 이미지 URL")
        String imageUrl,

        @Schema(description = "여행 카테고리")
        TripCategory category
) {
    @Schema(description = "해시태그 입력")
    public record HashTagInput(
            @Schema(description = "해시태그 값", example = "10대")
            String tag,

            @Schema(description = "정렬 순서", example = "1")
            int order
    ) {
    }

    public TripDestinations toTripDestinations(Trip trip) {
        if (destinationIds == null || destinationIds.isEmpty()) {
            return null;
        }
        return new TripDestinations(trip, destinationIds);
    }

    public TripTitle toTripTitle() {
        if (title != null) {
            return new TripTitle(title);
        }
        return null;
    }

    public TripDescription toTripDescription() {
        if (description != null) {
            return new TripDescription(description);
        }
        return null;
    }

    public TripPeriod toTripPeriod() {
        if (start != null && end != null) {
            return new TripPeriod(start, end);
        }
        return null;
    }

    public TripHashTags toHashTags(Trip trip) {
        if (hashTags == null || hashTags.isEmpty()) return null;
        return new TripHashTags(trip, hashTags.stream()
                .map(h -> new HashTagInfo(h.tag(), h.order()))
                .toList());
    }

}
