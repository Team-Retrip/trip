package com.retrip.trip.application.in.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Schema(description = "여행 일정 Response")
public record ItineraryResponse(
        @Schema(description = "일정 ID")
        UUID id,

        @Schema(description = "일정 날짜")
        LocalDate date,

        @Schema(description = "일정 이름")
        String name,

        @Schema(description = "일정 세부사항 목록 (sortOrder 오름차순)")
        List<ItineraryDetailResponse> itineraryDetails
) {
    @Schema(description = "일정 세부사항 Response")
    public record ItineraryDetailResponse(
            @Schema(description = "세부사항 ID")
            UUID id,

            @Schema(description = "위치 ID")
            UUID locationId,

            @Schema(description = "장소명 (TODO: map service API 연동 후 locationId로 조회)")
            String locationName,

            @Schema(description = "여행 Category")
            LocationDetailCategory category,

            @Schema(description = "일정 시간")
            LocalDateTime time,

            @Schema(description = "메모")
            String memo,

            @Schema(description = "정렬 순서")
            int sortOrder
    ) {
        public enum LocationDetailCategory {
            UNKNOWN,
            RESTAURANT,
            CAFE,
            SHOPPING,
            LEISURE,
            LANDMARK,
            PARK,
            ZOO,
            SEA,
            TRANSPORT,
            ACCOMMODATION,
            FLIGHT,
            ETC;

            public static LocationDetailCategory of(String value) {
                return Arrays.stream(LocationDetailCategory.values())
                        .filter(ldc -> ldc.name().equals(value))
                        .findFirst().orElse(LocationDetailCategory.UNKNOWN);
            }
        }
    }
}
