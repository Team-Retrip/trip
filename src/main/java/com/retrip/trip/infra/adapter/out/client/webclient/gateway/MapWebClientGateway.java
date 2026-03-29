package com.retrip.trip.infra.adapter.out.client.webclient.gateway;

import com.retrip.trip.application.out.gateway.AlarmGateway;
import com.retrip.trip.application.out.gateway.MapGateway;
import com.retrip.trip.application.out.gateway.model.CallAlarmType;
import com.retrip.trip.application.out.gateway.model.LocationDetail;
import com.retrip.trip.infra.adapter.out.client.webclient.api.AlarmApiClient;
import com.retrip.trip.infra.adapter.out.client.webclient.api.MapApiClient;
import com.retrip.trip.infra.adapter.out.client.webclient.api.common.AlarmType;
import com.retrip.trip.infra.adapter.out.client.webclient.api.fallback.AlarmApiClientFallback;
import com.retrip.trip.infra.adapter.out.client.webclient.api.fallback.MapApiClientFallback;
import com.retrip.trip.infra.adapter.out.client.webclient.api.request.CreateAlarmsRequest;
import com.retrip.trip.infra.adapter.out.client.webclient.api.response.CreateAlarmsResponse;
import com.retrip.trip.infra.adapter.out.client.webclient.api.response.LocationDetailsResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Gateway
@RequiredArgsConstructor
public class MapWebClientGateway implements MapGateway {

    private final MapApiClient mapApiClient;
    private final MapApiClientFallback mapApiClientFallback;

    @Override
    @CircuitBreaker(name = "mapApi.findAll", fallbackMethod = "findAllFallback")
    public List<LocationDetail> findAll(List<UUID> locationDetailIds) {
        if (locationDetailIds == null) {
            return null;
        }
        LocationDetailsResponse response = mapApiClient.findAll(locationDetailIds);
        return response.responses().stream().map(r ->
                new LocationDetail(
                        r.id(),
                        r.name(),
                        r.category()
                )
        ).toList();
    }

    private List<LocationDetail> findAllFallback(List<UUID> locationDetailIds, Throwable cause) {
        return mapApiClientFallback.findAll(locationDetailIds, cause);
    }


}
