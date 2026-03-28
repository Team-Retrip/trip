package com.retrip.trip.infra.adapter.out.client.webclient.api.request;

import com.retrip.trip.infra.adapter.out.client.webclient.api.common.AlarmType;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public record CreateAlarmsRequest(
        UUID senderId,
        List<UUID> receiverIds,
        Map<String, Object> parameters,
        AlarmType type
) {

}
