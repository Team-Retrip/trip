package com.retrip.trip.application.in.request;

import com.retrip.trip.domain.entity.Trip;
import com.retrip.trip.domain.vo.TripCategory;
import com.retrip.trip.domain.vo.TripDescription;
import com.retrip.trip.domain.vo.TripPeriod;
import com.retrip.trip.domain.vo.TripTitle;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

@Schema(description = "여행 생성 Request")
public record TripCreateRequest(

        @Schema(description = "여행 멤버 ID", example = "550e8400-e29b-41d4-a716-446655440000")
        @NotNull
        UUID memberId,

        @Schema(description = "여행 위치 ID", example = "550e8400-e29b-41d4-a716-446655440001")
        @NotNull
        UUID locationId,

        @Schema(description = "여행 제목", example = "유럽 배낭여행")
        @NotNull
        String title,

        @Schema(description = "여행 설명", example = "파리, 런던, 로마를 여행하는 일정입니다.")
        String description,

        @Schema(description = "여행 시작 날짜", example = "2025-06-15")
        @FutureOrPresent
        LocalDate start,

        @Schema(description = "여행 종료 날짜", example = "2025-06-25")
        @FutureOrPresent
        LocalDate end,

        @Schema(description = "여행 공개 여부")
        boolean open,

        @Schema(description = "여행 최대 참가 인원")
        int maxParticipants,

        @Schema(description = "여행 카테고리")
        TripCategory category

) {
    public Trip to() {
        return Trip.create(
                memberId,
                locationId,
                new TripTitle(title),
                new TripDescription(description),
                new TripPeriod(start, end),
                open,
                maxParticipants,
                category
        );
    }

    public Trip toWithItineraries() {
        return Trip.createWithItineraries(
                memberId,
                locationId,
                new TripTitle(title),
                new TripDescription(description),
                new TripPeriod(start, end),
                open,
                maxParticipants,
                category
        );
    }
}
