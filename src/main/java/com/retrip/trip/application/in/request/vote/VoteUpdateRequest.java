package com.retrip.trip.application.in.request.vote;

import com.retrip.trip.domain.entity.vote.VoteOption;
import com.retrip.trip.domain.entity.vote.VoteOptions;
import com.retrip.trip.domain.vo.vote.VoteOptionContent;
import com.retrip.trip.domain.vo.vote.VotePeriod;
import com.retrip.trip.domain.vo.vote.VoteSetting;
import com.retrip.trip.domain.vo.vote.VoteSummary;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.time.ZoneId;
import java.util.List;

@Schema(description = "투표 수정 Request")
public record VoteUpdateRequest(
        @Schema(description = "투표 제목")
        String title,

        @Schema(description = "투표 설명")
        String description,

        @Schema(description = "익명 여부")
        boolean anonymous,

        @Schema(description = "복수 선택 최대 개수")
        int maxSelections,

        @Schema(description = "선택항목 추가 허용 여부")
        boolean allowAddOption,

        @Schema(description = "투표 시작 시간")
        Instant startTIme,

        @Schema(description = "투표 종료 시간")
        Instant endTIme,

        @Schema(description = "타임존")
        String timezone,

        @Schema(description = "투표 옵션 항목 목록")
        List<VoteOptionRequest> options
) {

    public VoteSummary toSummary() {
        return new VoteSummary(title, description);
    }

    public VoteSetting toSetting() {
        return new VoteSetting(anonymous, maxSelections, allowAddOption, options.size());
    }

    public VotePeriod toPeriod() {
        return new VotePeriod(Instant.from(startTIme), Instant.from(endTIme), ZoneId.of(timezone));
    }

    public VoteOptions toOptions() {
        return VoteOptionRequest.toList(options);
    }

    @Schema(description = "투표 옵션 항목 Request")
    private record VoteOptionRequest(
            String content
    ) {
        private VoteOption to() {
            return new VoteOption(new VoteOptionContent(content));
        }

        public static VoteOptions toList(List<VoteOptionRequest> options) {
            return new VoteOptions(options.stream()
                    .map(VoteOptionRequest::to)
                    .toList());
        }
    }
}
