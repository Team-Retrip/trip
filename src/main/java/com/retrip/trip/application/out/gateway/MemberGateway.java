package com.retrip.trip.application.out.gateway;

import java.util.List;
import java.util.UUID;

public interface MemberGateway {
    List<MemberInfo> searchMembersByName(String name);
    List<MemberInfo> getMembersByIds(List<UUID> memberIds);

    record MemberInfo(UUID id, String name, String profileImageUrl, String bio) {}
}
