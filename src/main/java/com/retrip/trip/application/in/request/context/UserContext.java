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

        String hex = "99999999999999999999999999999991";

// 바로 변환
        UUID uuid = UUID.fromString(hex.replaceFirst("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5"));

        return new UserContext(
                uuid,
                "Tester",
                "test@naver.com",
                "홍길동",
                Gender.MAN,
                20
        );
    }
}
