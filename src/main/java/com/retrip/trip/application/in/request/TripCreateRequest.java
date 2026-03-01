package com.retrip.trip.application.in.request;

import com.retrip.trip.domain.entity.Trip;
import com.retrip.trip.domain.vo.HashTagInfo;
import com.retrip.trip.domain.vo.TripCategory;
import com.retrip.trip.domain.vo.TripDescription;
import com.retrip.trip.domain.vo.TripPeriod;
import com.retrip.trip.domain.vo.TripStatus;
import com.retrip.trip.domain.vo.TripTitle;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Schema(description = "여행 생성 Request")
public record TripCreateRequest(

        @Schema(description = "여행 위치 ID 목록")
        @NotNull
        List<UUID> locationIds,

        @Schema(description = "여행 제목", example = "유럽 배낭여행")
        @NotNull
        String title,

        @Schema(description = "여행 대표 이미지 URL")
        String imageUrl,

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

        @Schema(description = "여행 참여 비밀번호")
        String password,

        @Schema(description = "여행 최대 참가 인원")
        int maxParticipants,

        @Schema(description = "HashTag 목록")
        List<HashTagInput> hashTags,

        @Schema(description = "여행 카테고리")
        TripCategory category

) {
    @Schema(description = "해시태그 입력")
    public record HashTagInput(
            @Schema(description = "해시태그 값", example = "10대")
            String tag,

            @Schema(description = "정렬 순서", example = "1")
            int order
    ) {}

    private List<HashTagInfo> toHashTagInfos() {
        if (hashTags == null) return List.of();
        return hashTags.stream()
                .map(h -> new HashTagInfo(h.tag(), h.order()))
                .toList();
    }

    public Trip to(UUID memberId) {
        return Trip.create(
                memberId,
                locationIds,
                new TripTitle(title),
                imageUrl,
                new TripDescription(description),
                new TripPeriod(start, end),
                open,
                maxParticipants,
                toHashTagInfos(),
                category,
                TripStatus.RECRUITING
        );
    }

    public Trip toWithItineraries(UUID memberId) {
        return Trip.createWithItineraries(
                memberId,
                locationIds,
                new TripTitle(title),
                imageUrl,
                new TripDescription(description),
                new TripPeriod(start, end),
                open,
                maxParticipants,
                toHashTagInfos(),
                category,
                TripStatus.RECRUITING
        );
    }
}
