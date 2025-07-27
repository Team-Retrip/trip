package com.retrip.trip.domain.fixture;

import com.retrip.trip.domain.entity.participant.Participant;
import com.retrip.trip.domain.vo.*;

import java.util.UUID;

public class ParticipantFixture {
    public static final UUID LEADER_ID = UUID.fromString("caec62d1-f29d-477d-9743-292f48cc66bb");
    public static final UUID 정수_ID = UUID.fromString("a7f7215b-081a-42f4-b3e2-f06393de2f8b");
    public static final UUID 홍석_ID = UUID.fromString("bf97d20b-d1f7-46a9-8362-11b9fa02d67d");
    public static final UUID 준호_ID = UUID.fromString("8b9b67fd-1d88-4b30-bfea-cd8f89fc10d9");
    public static final UUID 지수_ID = UUID.fromString("de3b60d2-5672-464d-8769-bf5c9de5eaff");
    public static final UUID 혁진_ID = UUID.fromString("42880aaf-4b97-4b0c-8a8a-72df4bb592f6");

    public static Participant createLeaderParticipant(UUID tripId, UUID leaderId) {
        return Participant.create(tripId, leaderId, ParticipantRole.LEADER);
    }

    public static Participant createParticipant(UUID tripId, UUID leaderId) {
        return Participant.create(tripId, leaderId, ParticipantRole.PARTICIPANT);
    }
}
