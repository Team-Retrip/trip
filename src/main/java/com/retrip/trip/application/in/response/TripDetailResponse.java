package com.retrip.trip.application.in.response;

import com.retrip.trip.application.out.gateway.MemberGateway;
import com.retrip.trip.domain.entity.Trip;
import com.retrip.trip.domain.entity.TripParticipant;
import com.retrip.trip.domain.vo.ParticipantRole;
import com.retrip.trip.domain.vo.TripStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.Builder;

@Builder
@Schema(description = "여행 상세 Response")
public record TripDetailResponse(
        @Schema(
                description = "여행 고유 ID",
                example = "550e8400-e29b-41d4-a716-446655440000"
        )
        UUID id,

        @Schema(
                description = "요청한 회원이 여행 리더인지 여부",
                example = "true"
        )
        Boolean isLeader,

        @Schema(
                description = "요청한 회원이 여행 참가자인지 여부",
                example = "true"
        )
        Boolean isParticipant,

        @Schema(
                description = "요청한 회원이 참여 신청 대기 중인지 여부",
                example = "false"
        )
        Boolean isPendingDemand,

        @Schema(
                description = "요청한 회원이 초대받은 상태인지 여부",
                example = "false"
        )
        Boolean isInvited,

        @Schema(
                description = "대기 중인 초대장 ID (isInvited=true일 때만 존재)",
                example = "550e8400-e29b-41d4-a716-446655440000"
        )
        UUID pendingInvitationId,

        @Schema(
                description = "여행 제목",
                example = "부산 2박 3일 여행"
        )
        String title,

        @Schema(description = "여행 시작 날짜")
        LocalDate start,

        @Schema(description = "여행 종료 날짜")
        LocalDate end,

        @Schema(
                description = "여행 개설 일시",
                example = "2025-01-10T14:30:00"
        )
        LocalDateTime createdAt,

        @Schema(
                description = "현재 여행 참가자 수",
                example = "3"
        )
        Integer participantCount,

        @Schema(
                description = "최대 참가 가능 인원",
                example = "5"
        )
        Integer maxParticipantCount,

        @Schema(
                description = "여행 상태",
                example = "RECRUITING"
        )
        TripStatus tripStatus,

        @Schema(
                description = "여행 상태 표시명",
                example = "모집중"
        )
        String tripStatusName,

        @Schema(description = "여행 목적지 명 목록(아직 빈값 내려갈 예정)")
        List<String> destinationNames,

        @Schema(description = "여행 목적지 ID 목록")
        List<UUID> destinationIds,

        @Schema(
                description = "여행 대표 이미지 URL",
                example = "https://cdn.retrip.com/trips/main-image.jpg"
        )
        String imageUrl,

        @Schema(
                description = "여행 소개글",
                example = "바다 보면서 힐링할 분들 모집합니다."
        )
        String description,

        @Schema(description = "여행 해시태그 목록")
        List<HashTagResponse> hashTags,

        @Schema(description = "여행 참가자 목록")
        List<TripParticipantResponse> participants
) {
    @Schema(description = "해시태그 응답")
    public record HashTagResponse(
            @Schema(description = "해시태그 값", example = "맛집투어")
            String tag,

            @Schema(description = "정렬 순서", example = "1")
            int order
    ) {}

    public static TripDetailResponse of(UUID memberId, Trip trip, Map<UUID, MemberGateway.MemberInfo> memberInfoMap,
                                        boolean isPendingDemand, UUID pendingInvitationId) {
        return TripDetailResponse.builder()
                .id(trip.getId())
                .isLeader(trip.getTripParticipants().isLeader(memberId))
                .isParticipant(trip.getTripParticipants().isParticipant(memberId))
                .isPendingDemand(isPendingDemand)
                .isInvited(pendingInvitationId != null)
                .pendingInvitationId(pendingInvitationId)
                .title(trip.getTitle().getValue())
                .start(trip.getPeriod().getStart())
                .end(trip.getPeriod().getEnd())
                .createdAt(trip.getCreatedAt())
                .participantCount(trip.getTripParticipants().getCurrentCount())
                .maxParticipantCount(trip.getTripParticipants().getMaxParticipants())
                .tripStatus(trip.getStatus())
                .tripStatusName(trip.getStatus().getViewName())
                .destinationNames(List.of()) // TODO : Location 구현 시 채울 예정
                .destinationIds(trip.getDestinations().getDestinationIds())
                .imageUrl(trip.getImageUrl())
                .description(trip.getDescription().getValue())
                .hashTags(trip.getHashTags().getValues().stream()
                        .map(h -> new HashTagResponse(h.getName(), h.getTagOrder()))
                        .toList())
                .participants(TripParticipantResponse.toList(trip.getTripParticipants().getValues(), memberInfoMap))
                .build();
    }

    @Builder
    public record TripParticipantResponse (
            @Schema(
                    description = "여행 참가자 고유 ID",
                    example = "a3f1c0e2-4b6d-4a3c-bf2e-1b8c12345678"
            )
            UUID participantId,

            @Schema(
                    description = "회원 고유 ID",
                    example = "b7e9f0c1-1234-4d2b-a123-abcdef123456"
            )
            UUID memberId,

            @Schema(
                    description = "한줄소개",
                    example = "안녕하세요"
            )
            String introduction,

            @Schema(
                    description = "닉네임",
                    example = "박정수"
            )
            String nickName,

            @Schema(
                    description = "참가자 프로필 이미지 url",
                    example = "http://ww~~~~"
            )
            String imageUrl,

            @Schema(
                    description = "참가자 역할 (LEADER / MEMBER)",
                    example = "LEADER"
            )
            ParticipantRole role
    ){
        public static List<TripParticipantResponse> toList(List<TripParticipant> tripParticipants, Map<UUID, MemberGateway.MemberInfo> memberInfoMap) {
            return tripParticipants.stream()
                    .map(p -> TripParticipantResponse.of(p, memberInfoMap.get(p.getMemberId())))
                    .toList();
        }

        private static TripParticipantResponse of(TripParticipant participant, MemberGateway.MemberInfo memberInfo) {
            return TripParticipantResponse.builder()
                    .participantId(participant.getId())
                    .memberId(participant.getMemberId())
                    .introduction(memberInfo != null ? memberInfo.bio() : null)
                    .nickName(memberInfo != null ? memberInfo.name() : null)
                    .imageUrl(memberInfo != null ? memberInfo.profileImageUrl() : null)
                    .role(participant.getRole())
                    .build();
        }
    }
}