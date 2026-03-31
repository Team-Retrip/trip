package com.retrip.trip.application.in.response;

import com.retrip.trip.application.out.gateway.MemberGateway;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "초대 가능한 회원 정보")
public record SearchableMemberResponse(
        @Schema(description = "회원 ID")
        UUID id,
        @Schema(description = "회원 닉네임")
        String name,
        @Schema(description = "프로필 이미지 URL")
        String profileImageUrl,
        @Schema(description = "한줄소개")
        String bio
) {
    public static SearchableMemberResponse of(MemberGateway.MemberInfo memberInfo) {
        return new SearchableMemberResponse(
                memberInfo.id(),
                memberInfo.name(),
                memberInfo.profileImageUrl(),
                memberInfo.bio()
        );
    }
}
