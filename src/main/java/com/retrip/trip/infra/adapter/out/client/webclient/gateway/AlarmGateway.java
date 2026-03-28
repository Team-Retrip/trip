package com.retrip.trip.infra.adapter.out.client.webclient.gateway;

import com.retrip.trip.application.out.client.AlarmApiClient;
import com.retrip.trip.application.out.client.model.CallAlarmType;
import com.retrip.trip.infra.adapter.out.client.webclient.api.AlarmApi;
import com.retrip.trip.infra.adapter.out.client.webclient.api.common.AlarmType;
import com.retrip.trip.infra.adapter.out.client.webclient.api.fallback.AlarmApiFallback;
import com.retrip.trip.infra.adapter.out.client.webclient.api.request.CreateAlarmsRequest;

import com.retrip.trip.infra.adapter.out.client.webclient.api.response.CreateAlarmsResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@Gateway
@RequiredArgsConstructor
public class AlarmGateway implements AlarmApiClient {

    private final AlarmApi alarmApi;
    private final AlarmApiFallback alarmApiFallback;


    @Override
    @CircuitBreaker(name = "alarmApi.sendAlarms", fallbackMethod = "sendAlarmsFallback")
    public void sendAlarms(UUID senderId, List<UUID> receiverIds,
            Map<String, Object> parameters, CallAlarmType type) {
        AlarmType alarmType = AlarmType.from(type);
        CreateAlarmsRequest request = new CreateAlarmsRequest(senderId, receiverIds, parameters,
                alarmType);
        CreateAlarmsResponse createAlarmsResponse = alarmApi.sendAlarms(request);
        // 성공 여부 반환할지..?
    }


    private void sendAlarmsFallback(UUID senderId, List<UUID> receiverIds,
            Map<String, Object> parameters, CallAlarmType type, Throwable cause) {
        alarmApiFallback.sendAlarms(senderId, receiverIds, parameters, type, cause);
    }
}
