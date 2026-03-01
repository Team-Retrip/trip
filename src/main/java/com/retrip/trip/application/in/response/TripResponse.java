package com.retrip.trip.application.in.response;

import com.retrip.trip.domain.entity.Trip;
import com.retrip.trip.domain.entity.TripHashTag;
import com.retrip.trip.domain.vo.TripStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Schema(description = "여행 목록 Response")
public record TripResponse(
        @Schema(description = "여행 ID")
        UUID id,

        @Schema(description = "여행 제목")
        String title,

        @Schema(description = "목적지 ID 목록")
        List<UUID> destinationIds,

        @Schema(description = "목적지 명 (예: 파리, 제주)")
        String destinationName, // TODO: 추후 QueryDSL Join으로 데이터 채우기 구현 필요

        @Schema(description = "여행 대표 이미지 URL")
        String imageUrl,

        @Schema(description = "여행 상태")
        TripStatus status,

        @Schema(description = "여행 시작 날짜")
        LocalDate start,

        @Schema(description = "여행 종료 날짜")
        LocalDate end,

        @Schema(description = "현재 참가자 수")
        int currentParticipantCount,

        @Schema(description = "최대 참가자 수")
        int maxParticipantCount,

        @Schema(description = "여행 공개 여부")
        boolean open,

        @Schema(description = "HashTag 목록")
        List<HashTagResponse> hashTags
) {

    @Schema(description = "해시태그 응답")
    public record HashTagResponse(
            @Schema(description = "해시태그 값")
            String tag,

            @Schema(description = "정렬 순서")
            int order
    ) {}

    public static List<TripResponse> of(List<Trip> trips, List<TripHashTag> hashTags) {
        Map<UUID, List<TripHashTag>> tagMap = hashTags.stream()
                .collect(Collectors.groupingBy(h -> h.getTrip().getId()));

        return trips.stream()
                .map(trip -> new TripResponse(
                        trip.getId(),
                        trip.getTitle().getValue(),
                        trip.getDestinations().getDestinationIds(),
                        "", // destinationName: 현재 Location 정보가 없으므로 빈 값 또는 추후 구현
                        trip.getImageUrl(),
                        trip.getStatus(),
                        trip.getPeriod().getStart(),
                        trip.getPeriod().getEnd(),
                        trip.getTripParticipants().getCurrentCount(),
                        trip.getTripParticipants().getMaxParticipants(),
                        trip.isOpen(),
                        tagMap.getOrDefault(trip.getId(), List.of()).stream()
                                .sorted(Comparator.comparingInt(TripHashTag::getTagOrder))
                                .map(h -> new HashTagResponse(h.getName(), h.getTagOrder()))
                                .toList()
                ))
                .toList();
    }
}