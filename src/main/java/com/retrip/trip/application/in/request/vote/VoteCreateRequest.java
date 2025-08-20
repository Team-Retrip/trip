package com.retrip.trip.application.in.request.vote;

import com.retrip.trip.domain.entity.vote.Vote;
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
import java.util.UUID;

@Schema(description = "투표 생성 Request")
public record VoteCreateRequest(
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

    public Vote to(UUID tripId, UUID memberId) {
        VoteSummary voteSummary = new VoteSummary(title, description);
        VoteSetting voteSetting = new VoteSetting(anonymous, maxSelections, allowAddOption, options.size());
        VotePeriod votePeriod = new VotePeriod(Instant.from(startTIme), Instant.from(endTIme), ZoneId.of(timezone));
        VoteOptions voteOptions = VoteOptionRequest.toList(options);
        return new Vote(tripId, memberId, voteSummary, voteSetting, votePeriod, voteOptions);
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
