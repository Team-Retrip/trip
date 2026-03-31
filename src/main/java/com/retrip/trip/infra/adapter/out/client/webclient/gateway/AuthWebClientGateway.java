package com.retrip.trip.infra.adapter.out.client.webclient.gateway;

import com.retrip.trip.application.out.gateway.MemberGateway;
import com.retrip.trip.infra.adapter.out.client.webclient.api.AuthApiClient;
import com.retrip.trip.infra.adapter.out.client.webclient.api.fallback.AuthApiClientFallback;
import com.retrip.trip.infra.adapter.out.client.webclient.api.response.AuthMemberSearchResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@Gateway
@RequiredArgsConstructor
public class AuthWebClientGateway implements MemberGateway {

    private final AuthApiClient authApiClient;
    private final AuthApiClientFallback authApiClientFallback;

    @Override
    @CircuitBreaker(name = "authApi.searchMembers", fallbackMethod = "searchMembersFallback")
    public List<MemberInfo> searchMembersByName(String name) {
        AuthMemberSearchResponse response = authApiClient.searchMembers(name);
        return response.data().stream()
                .map(m -> new MemberInfo(m.id(), m.name(), m.profileImageUrl(), m.bio()))
                .toList();
    }

    @Override
    @CircuitBreaker(name = "authApi.getMembersByIds", fallbackMethod = "getMembersByIdsFallback")
    public List<MemberInfo> getMembersByIds(List<UUID> memberIds) {
        if (memberIds == null || memberIds.isEmpty()) {
            return List.of();
        }
        AuthMemberSearchResponse response = authApiClient.getMembersByIds(memberIds);
        return response.data().stream()
                .map(m -> new MemberInfo(m.id(), m.name(), m.profileImageUrl(), m.bio()))
                .toList();
    }

    private List<MemberInfo> searchMembersFallback(String name, Throwable cause) {
        return authApiClientFallback.searchMembersByName(name, cause);
    }

    private List<MemberInfo> getMembersByIdsFallback(List<UUID> memberIds, Throwable cause) {
        return authApiClientFallback.getMembersByIds(memberIds, cause);
    }
}
