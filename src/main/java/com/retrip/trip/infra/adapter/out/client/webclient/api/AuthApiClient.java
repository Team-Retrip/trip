package com.retrip.trip.infra.adapter.out.client.webclient.api;

import com.retrip.trip.infra.adapter.out.client.webclient.api.response.AuthMemberSearchResponse;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;

import java.util.List;
import java.util.UUID;

public interface AuthApiClient {
    @GetExchange("/users/search")
    AuthMemberSearchResponse searchMembers(@RequestParam String name);

    @GetExchange("/users/members")
    AuthMemberSearchResponse getMembersByIds(@RequestParam List<UUID> ids);
}
