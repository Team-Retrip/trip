package com.retrip.trip.infra.adapter.out.client.webclient.api;

import com.retrip.trip.infra.adapter.out.client.webclient.api.request.CreateAlarmsRequest;
import com.retrip.trip.infra.adapter.out.client.webclient.api.response.CreateAlarmsResponse;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.PostExchange;

public interface AlarmApi {
    @PostExchange("/alarms/send")
    CreateAlarmsResponse sendAlarms(@RequestBody CreateAlarmsRequest request);
}
