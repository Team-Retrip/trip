package com.retrip.trip.infra.config;

import com.retrip.trip.application.out.gateway.AlarmGateway;
import com.retrip.trip.application.out.gateway.MemberGateway;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@TestConfiguration
public class GatewayTestConfig {

    @Bean
    public MemberGateway memberGateway() {
        return new MemberGateway() {
            @Override
            public List<MemberInfo> searchMembersByName(String name) {
                return Collections.emptyList();
            }

            @Override
            public List<MemberInfo> getMembersByIds(List<UUID> memberIds) {
                return Collections.emptyList();
            }
        };
    }

    @Bean
    public AlarmGateway alarmGateway() {
        return (senderId, receiverIds, parameters, type) -> {};
    }
}
