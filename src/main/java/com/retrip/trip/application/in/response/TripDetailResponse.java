package com.retrip.trip.application.in.response;

import com.retrip.trip.domain.entity.Trip;
import com.retrip.trip.domain.entity.TripParticipant;
import com.retrip.trip.domain.vo.ParticipantRole;
import com.retrip.trip.domain.vo.TripStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.Builder;

@Builder
@Schema(description = "여행 상세 Response")
public record TripDetailResponse(
        @Schema(description = "여행 ID")
        UUID id,

        @Schema(description = "리더 여부")
        Boolean isLeader,

        @Schema(description = "여행 참가자 여부")
        Boolean isParticipant,

        @Schema(description = "여행 제목")
        String title,

        @Schema(description = "여행 개설일")
        LocalDateTime createdAt,

        @Schema(description = "현재 여행 참가자 수")
        Integer participantCount,

        @Schema(description = "수용 가능 참가자 수")
        Integer maxParticipantCount,

        @Schema(description = "여행 상태")
        TripStatus tripStatus,

        @Schema(description = "여행 상태 명")
        String tripStatusName,

        // TODO: 해당 부분은 아직 Trip 쪽에서 구현이 안되어있어서 추후 해당 응답에 값 담을 예정
        @Schema(description = "여행지")
        String tripLocation,

        @Schema(description = "여행 소개")
        String description,

        @Schema(description = "해쉬태그 목록")
        List<String> hashTags,

        @Schema(description = "여행 참가자 목록")
        List<TripParticipantResponse> participants
) {

    public static TripDetailResponse of(UUID memberId, Trip trip) {
        return TripDetailResponse.builder()
                .id(trip.getId())
                .isLeader(trip.getTripParticipants().isLeader(memberId))
                .isParticipant(trip.getTripParticipants().isParticipant(memberId))
                .title(trip.getTitle().getValue())
                .createdAt(trip.getCreatedAt())
                .participantCount(trip.getTripParticipants().getCurrentCount())
                .maxParticipantCount(trip.getTripParticipants().getMaxParticipants())
                .tripStatus(trip.getStatus())
                .tripStatusName(trip.getStatus().getViewName())
                .tripLocation("") //TODO : 구현 되면 채워 넣을 예정
                .description(trip.getDescription().getValue())
                .hashTags(trip.getHashTags().getHashTagNames())
                .participants(TripParticipantResponse.toList(trip.getTripParticipants().getValues()))
                .build();
    }

    @Builder
    public record TripParticipantResponse (

            @Schema(description = "여행 참가자 고유 id")
            UUID participantId,

            @Schema(description = "여행 참가자 회원 고유 id")
            UUID memberId,

            ParticipantRole role
    ){
        public static List<TripParticipantResponse> toList(List<TripParticipant> tripParticipants) {
            return tripParticipants.stream()
                    .map(TripParticipantResponse::of)
                    .toList();
        }

        private static TripParticipantResponse of(TripParticipant participant) {
            return TripParticipantResponse.builder()
                    .participantId(participant.getId())
                    .memberId(participant.getMemberId())
                    .role(participant.getRole())
                    .build();
        }
    }
}
