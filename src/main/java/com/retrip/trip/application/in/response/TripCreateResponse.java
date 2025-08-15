package com.retrip.trip.application.in.response;

import com.retrip.trip.domain.entity.Trip;
import com.retrip.trip.domain.entity.TripHashTag;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Schema(description = "여행 생성 Response")
public record TripCreateResponse(
        @Schema(description = "여행 ID", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "여행 목적지 ID", example = "550e8400-e29b-41d4-a716-446655440001")
        UUID destinationId,

        @Schema(description = "여행 제목", example = "파리 여행")
        String title,

        @Schema(description = "여행 설명")
        String description,

        @Schema(description = "여행 시작 날짜")
        LocalDate start,

        @Schema(description = "여행 종료 날짜")
        LocalDate end,

        @Schema(description = "여행 공개 여부")
        boolean open,

        @Schema(description = "여행 최대 참가 인원")
        int maxParticipants,

        @Schema(description = "여행 카테고리")
        List<String> hashTags,

        @Schema(description = "여행 카테고리")
        String category,

        @Schema(description = "여행 일정 리스트")
        List<ItineraryCreateResponse> itineraries
) {
    public static TripCreateResponse of(Trip trip) {
        return new TripCreateResponse(
                trip.getId(),
                trip.getDestinationId(),
                trip.getTitle().getValue(),
                trip.getDescription().getValue(),
                trip.getPeriod().getStart(),
                trip.getPeriod().getEnd(),
                trip.isOpen(),
                trip.getTripParticipants().getMaxParticipants(),
                trip.getHashTags().getValues().stream().map(TripHashTag::getName).toList(),
                trip.getCategory().getViewName(),
                trip.getItineraries() == null ? new ArrayList<>() :
                        trip.getItineraries().getValues().stream()
                                .map(i -> new ItineraryCreateResponse(i.getId(), i.getName(), i.getDate()))
                                .toList()
        );
    }

    @Schema(description = "여행 일정 Response")
    private record ItineraryCreateResponse(
            @Schema(description = "일정 ID")
            UUID id,

            @Schema(description = "일정 이름")
            String name,

            @Schema(description = "일정 날짜")
            LocalDate date
    ) {
    }
}
