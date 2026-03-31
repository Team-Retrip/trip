package com.retrip.trip.infra.adapter.out.client.webclient.api.fallback;

import com.retrip.trip.application.out.gateway.MemberGateway;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class AuthApiClientFallback {
    public List<MemberGateway.MemberInfo> searchMembersByName(String name, Throwable cause) {
        log.error("Auth API 장애 발생 - Fallback 동작 (name: {}, error: {})", name, cause.getMessage());
        return Collections.emptyList();
    }

    public List<MemberGateway.MemberInfo> getMembersByIds(List<UUID> ids, Throwable cause) {
        log.error("Auth API 장애 발생 - Fallback 동작 (ids: {}, error: {})", ids, cause.getMessage());
        return Collections.emptyList();
    }
}
