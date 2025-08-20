package com.retrip.trip.domain.fixture;

import com.retrip.trip.domain.entity.vote.Vote;
import com.retrip.trip.domain.entity.vote.VoteOption;
import com.retrip.trip.domain.entity.vote.VoteOptions;
import com.retrip.trip.domain.vo.vote.VoteOptionContent;
import com.retrip.trip.domain.vo.vote.VotePeriod;
import com.retrip.trip.domain.vo.vote.VoteSetting;
import com.retrip.trip.domain.vo.vote.VoteSummary;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;

import static com.retrip.trip.domain.fixture.TripFixture.MEMBER_ID;
import static com.retrip.trip.domain.fixture.TripFixture.TRIP_ID;

public class VoteFixture {

    public static Vote vote() {
        int optionsSize = 5;
        VoteSummary voteSummary = new VoteSummary("점심 투표", "선택하세요");
        VoteSetting voteSetting = new VoteSetting(false, 1, false, optionsSize);
        VotePeriod votePeriod = new VotePeriod(
                Instant.now().plus(Duration.ofDays(1)),
                Instant.now().plus(Duration.ofDays(2)),
                ZoneId.of("Asia/Seoul")
        );

        ArrayList<VoteOption> optionList = new ArrayList<>(optionsSize);
        optionList.add(new VoteOption(new VoteOptionContent("굽네")));
        optionList.add(new VoteOption(new VoteOptionContent("BBQ")));
        optionList.add(new VoteOption(new VoteOptionContent("교촌")));
        optionList.add(new VoteOption(new VoteOptionContent("푸라닭")));
        optionList.add(new VoteOption(new VoteOptionContent("BHC")));

        return new Vote(TRIP_ID, MEMBER_ID, voteSummary, voteSetting, votePeriod, new VoteOptions(optionList));
    }
}
