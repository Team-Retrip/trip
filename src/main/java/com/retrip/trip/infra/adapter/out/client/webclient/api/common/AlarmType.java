package com.retrip.trip.infra.adapter.out.client.webclient.api.common;

import com.retrip.trip.application.out.client.model.CallAlarmType;

public enum AlarmType {
    DEMAND,
    INVITATION,
    TRIP_CHANGE,
    TRIP_CONFIRM,
    KICK,
    DELETE;

    public static AlarmType from(CallAlarmType type) {
        return switch (type) {
            case DEMAND -> DEMAND;
            case INVITATION -> INVITATION;
            case CHANGE -> TRIP_CHANGE;
            case CONFIRM -> TRIP_CONFIRM;
            case KICK -> KICK;
            case DELETE -> DELETE;
        };
    }
}
