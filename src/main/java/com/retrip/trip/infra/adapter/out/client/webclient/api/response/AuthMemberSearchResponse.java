package com.retrip.trip.infra.adapter.out.client.webclient.api.response;

import java.util.List;
import java.util.UUID;

public record AuthMemberSearchResponse(
        boolean success,
        int status,
        String message,
        List<MemberResult> data
) {
    public record MemberResult(UUID id, String name, String profileImageUrl, String bio) {}
}
