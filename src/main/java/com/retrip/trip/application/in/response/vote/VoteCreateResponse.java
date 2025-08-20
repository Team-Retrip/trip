package com.retrip.trip.application.in.response.vote;

import com.retrip.trip.domain.entity.vote.Vote;
import com.retrip.trip.domain.entity.vote.VoteOption;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.List;

@Schema(description = "투표 생성 Response")
public record VoteCreateResponse(
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

    public static VoteCreateResponse of(Vote vote) {
        return new VoteCreateResponse(
                vote.getSummary().getTitle(),
                vote.getSummary().getDescription(),
                vote.getSetting().isAnonymous(),
                vote.getSetting().getMaxSelections(),
                vote.getSetting().isAllowAddOption(),
                vote.getPeriod().getStartTIme(),
                vote.getPeriod().getEndTime(),
                vote.getPeriod().getTimezone().toString(),
                VoteOptionRequest.toList(vote.getOptions().getValues())
        );
    }

    @Schema(description = "투표 옵션 항목 Response")
    private record VoteOptionRequest(
            String content
    ) {

        private static VoteOptionRequest of(VoteOption voteOption) {
            return new VoteOptionRequest(voteOption.getContent().getValue());
        }

        public static List<VoteOptionRequest> toList(List<VoteOption> voteOptions) {
            return voteOptions.stream()
                    .map(VoteOptionRequest::of)
                    .toList();
        }
    }
}
