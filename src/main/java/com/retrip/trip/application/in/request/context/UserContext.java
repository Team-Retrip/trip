package com.retrip.trip.application.in.request.context;

import java.util.UUID;

public record UserContext(
        UUID memberId,
        String nickName,
        String email, //이메일
        String name, //실명 이름
        Gender gender,
        int age
) {
    private enum Gender {
        MAN,
        FEMALE,
    }

    public static UserContext mockOf() {
        return new UserContext(
                UUID.randomUUID(),
                "Tester",
                "test@naver.com",
                "홍길동",
                Gender.MAN,
                20
        );
    }
}
