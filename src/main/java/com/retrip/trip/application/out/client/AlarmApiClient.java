package com.retrip.trip.application.out.client;

import com.retrip.trip.application.out.client.model.CallAlarmType;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface AlarmApiClient {

    //todo: parameter Map으로 변경 필요
    void sendAlarms(UUID senderId, List<UUID> receiverIds,
            Map<String, Object> parameters, CallAlarmType type);
}
