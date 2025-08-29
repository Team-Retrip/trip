package com.retrip.trip.application.in.response;

import com.retrip.trip.domain.entity.Trip;
import com.retrip.trip.domain.entity.TripHashTag;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;

@Schema(description = "여행 목록 Response")
public record TripResponse(
        @Schema(description = "여행 ID")
        UUID id,

        @Schema(description = "여행 제목")
        String title,

        @Schema(description = "목적지 ID")
        UUID destinationId,

        @Schema(description = "여행 시작 날짜")
        LocalDate start,

        @Schema(description = "여행 종료 날짜")
        LocalDate end,

        @Schema(description = "여행 공개 여부")
        boolean open,

        @Schema(description = "HashTag 목록")
        List<String> hashTags
) {
    public static List<TripResponse> of(List<Trip> trips, List<TripHashTag> hashTags) {
        Map<UUID, List<String>> tags = hashTags.stream()
                .collect(Collectors.groupingBy(
                        h -> h.getTrip().getId(),
                        Collectors.mapping(TripHashTag::getName, Collectors.toList())
                ));

        return trips.stream()
                .map(trip -> new TripResponse(
                        trip.getId(),
                        trip.getTitle().getValue(),
                        trip.getDestinationId(),
                        trip.getPeriod().getStart(),
                        trip.getPeriod().getEnd(),
                        trip.isOpen(),
                        tags.getOrDefault(trip.getId(), List.of())
                ))
                .toList();

    }
}
