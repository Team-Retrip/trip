package com.retrip.trip.infra.adapter.out.client.webclient.gateway;

import com.retrip.trip.application.out.gateway.AlarmGateway;
import com.retrip.trip.application.out.gateway.model.CallAlarmType;
import com.retrip.trip.infra.adapter.out.client.webclient.api.AlarmApiClient;
import com.retrip.trip.infra.adapter.out.client.webclient.api.common.AlarmType;
import com.retrip.trip.infra.adapter.out.client.webclient.api.fallback.AlarmApiClientFallback;
import com.retrip.trip.infra.adapter.out.client.webclient.api.request.CreateAlarmsRequest;

import com.retrip.trip.infra.adapter.out.client.webclient.api.response.CreateAlarmsResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

@Gateway
@RequiredArgsConstructor
public class AlarmWebClientGateway implements AlarmGateway {

    private final AlarmApiClient alarmApiClient;
    private final AlarmApiClientFallback alarmApiClientFallback;


    @Override
    @CircuitBreaker(name = "alarmApi.sendAlarms", fallbackMethod = "sendAlarmsFallback")
    public void sendAlarms(UUID senderId, List<UUID> receiverIds,
                           Map<String, Object> parameters, CallAlarmType type) {
        AlarmType alarmType = AlarmType.from(type);
        CreateAlarmsRequest request = new CreateAlarmsRequest(senderId, receiverIds, parameters,
                alarmType);
        CreateAlarmsResponse createAlarmsResponse = alarmApiClient.sendAlarms(request);
        // 성공 여부 반환할지..?
    }


    private void sendAlarmsFallback(UUID senderId, List<UUID> receiverIds,
                                    Map<String, Object> parameters, CallAlarmType type, Throwable cause) {
        alarmApiClientFallback.sendAlarms(senderId, receiverIds, parameters, type, cause);
    }
}
