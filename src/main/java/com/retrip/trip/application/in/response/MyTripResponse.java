package com.retrip.trip.application.in.response;

import com.retrip.trip.domain.entity.Trip;
import com.retrip.trip.domain.entity.TripHashTag;
import com.retrip.trip.domain.vo.TripStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Schema(description = "가입된 여행 목록 Response")
public record MyTripResponse(
        @Schema(description = "여행 ID")
        UUID id,

        @Schema(description = "여행 제목")
        String title,

        @Schema(description = "여행 이미지 url")
        String imageUrl,

        @Schema(description = "여행 상태")
        TripStatus tripStatus,

        @Schema(description = "현재 모집된 인원")
        Integer joinedParticipantCount,

        @Schema(description = "최대 모집 인원")
        Integer maxParticipantCount,

        @Schema(description = "여행 시작 날짜")
        LocalDate start,

        @Schema(description = "여행 종료 날짜")
        LocalDate end,

        @Schema(description = "해당 여행의 방장인지 아닌지")
        Boolean isLeader
) {
}
