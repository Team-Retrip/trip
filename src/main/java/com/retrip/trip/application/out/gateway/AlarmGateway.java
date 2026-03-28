package com.retrip.trip.application.out.gateway;

import com.retrip.trip.application.out.gateway.model.CallAlarmType;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface AlarmGateway {

    void sendAlarms(UUID senderId, List<UUID> receiverIds,
                    Map<String, Object> parameters, CallAlarmType type);
}
