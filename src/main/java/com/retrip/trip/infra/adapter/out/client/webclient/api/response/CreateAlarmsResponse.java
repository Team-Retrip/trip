package com.retrip.trip.infra.adapter.out.client.webclient.api.response;

import com.retrip.trip.infra.adapter.out.client.webclient.api.common.AlarmType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record CreateAlarmsResponse(
        List<CreateAlarmResponse> alarms
) {
    private record CreateAlarmResponse(
            UUID id,
            UUID senderId,
            UUID receiverId,
            String title,
            String body,
            AlarmType type,
            Boolean isRead,
            LocalDateTime createdAt

    ) {
    }
}
