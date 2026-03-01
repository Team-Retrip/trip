package com.retrip.trip.application.in.response;

import com.retrip.trip.domain.entity.Trip;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Schema(description = "여행 수정 Response")
public record TripUpdateResponse(
        @Schema(description = "여행 ID", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "여행 목적지 ID 들", example = "550e8400-e29b-41d4-a716-446655440001")
        List<UUID> destinationIds,

        @Schema(description = "여행 제목", example = "파리 여행")
        String title,

        @Schema(description = "여행 설명")
        String description,

        @Schema(description = "여행 시작 날짜")
        LocalDate start,

        @Schema(description = "여행 종료 날짜")
        LocalDate end,

        @Schema(description = "여행 최대 참가 인원")
        int maxParticipants,

        @Schema(description = "여행 카테고리")
        List<HashTagResponse> hashTags,

        @Schema(description = "여행 카테고리")
        String category,

        @Schema(description = "여행 대표 이미지 url")
        String imageUrl,

        @Schema(description = "여행 일정 리스트")
        List<ItineraryUpdateResponse> itineraries
) {

    public static TripUpdateResponse of(Trip trip) {
        return new TripUpdateResponse(
                trip.getId(),
                trip.getDestinations().getDestinationIds(),
                trip.getTitle().getValue(),
                trip.getDescription().getValue(),
                trip.getPeriod().getStart(),
                trip.getPeriod().getEnd(),
                trip.getTripParticipants().getMaxParticipants(),
                trip.getHashTags().getValues().stream()
                        .map(hashtag -> new HashTagResponse(hashtag.getName(),
                                hashtag.getTagOrder()))
                        .sorted(Comparator.comparingInt(HashTagResponse::order))
                        .toList(),
                trip.getCategory().getViewName(),
                trip.getImageUrl(),
                trip.getItineraries() == null ? new ArrayList<>() :
                        trip.getItineraries().getValues().stream()
                                .map(i -> new ItineraryUpdateResponse(i.getId(), i.getName(),
                                        i.getDate()))
                                .toList()
        );
    }

    @Schema(description = "해시태그 Response")
    public record HashTagResponse(
            @Schema(description = "해시태그")
            String tag,

            @Schema(description = "정렬 순서")
            int order
    ) {

    }

    @Schema(description = "여행 일정 Response")
    private record ItineraryUpdateResponse(
            @Schema(description = "일정 ID")
            UUID id,

            @Schema(description = "일정 이름")
            String name,

            @Schema(description = "일정 날짜")
            LocalDate date
    ) {

    }
}
