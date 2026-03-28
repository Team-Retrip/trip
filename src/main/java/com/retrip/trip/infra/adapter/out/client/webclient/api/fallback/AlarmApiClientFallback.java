package com.retrip.trip.infra.adapter.out.client.webclient.api.fallback;

import com.retrip.trip.application.out.gateway.model.CallAlarmType;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class AlarmApiClientFallback {

    public void sendAlarms(UUID senderId, List<UUID> receiverIds,
                           Map<String, Object> parameters, CallAlarmType type, Throwable cause) {
        log.error(
                "alarm API 장애 발생 - Fallback 동작 (senderId: {}, receiverIds: {}, parameters: {}, type: {}, error: {})",
                senderId,
                receiverIds,
                parameters,
                type,
                cause.getMessage());
    }
}
