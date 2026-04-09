package com.retrip.trip.application.in.response;

import com.retrip.trip.domain.entity.Trip;
import com.retrip.trip.domain.entity.demand.Demand;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Schema(description = "마이페이지 신청함 응답")
public record MyPageDemandResponse(
        @Schema(description = "신청 ID")
        UUID demandId,
        @Schema(description = "여행 ID")
        UUID tripId,
        @Schema(description = "신청 상태 코드", example = "PENDING")
        String demandStatus,
        @Schema(description = "신청 상태 표시명", example = "대기")
        String demandStatusName,
        @Schema(description = "신청 일시", example = "2026-01-23T00:00:00")
        LocalDateTime appliedAt,
        @Schema(description = "여행 제목")
        String tripTitle,
        @Schema(description = "여행 대표 이미지 URL")
        String tripImageUrl,
        @Schema(description = "현재 참가자 수", example = "4")
        int currentParticipants,
        @Schema(description = "최대 참가자 수", example = "6")
        int maxParticipants,
        @Schema(description = "여행 상태 코드", example = "RECRUITING")
        String tripStatus,
        @Schema(description = "여행 카테고리 코드", example = "DOMESTIC")
        String tripCategory,
        @Schema(description = "여행지 ID 목록")
        List<UUID> destinationIds
) {
    public static MyPageDemandResponse of(Demand demand, Trip trip) {
        return new MyPageDemandResponse(
                demand.getId(),
                demand.getTripId(),
                demand.getStatus().name(),
                demand.getStatus().getViewName(),
                demand.getCreatedAt(),
                trip != null ? trip.getTitle().getValue() : null,
                trip != null ? trip.getImageUrl() : null,
                trip != null ? trip.getTripParticipants().getCurrentCount() : 0,
                trip != null ? trip.getTripParticipants().getMaxParticipants() : 0,
                trip != null ? trip.getStatus().name() : null,
                trip != null ? trip.getCategory().name() : null,
                trip != null ? trip.getDestinations().getDestinationIds() : List.of()
        );
    }
}
