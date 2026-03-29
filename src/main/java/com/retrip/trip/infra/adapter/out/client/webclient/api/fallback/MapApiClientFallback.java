package com.retrip.trip.infra.adapter.out.client.webclient.api.fallback;

import com.retrip.trip.application.out.gateway.model.CallAlarmType;

import com.retrip.trip.application.out.gateway.model.LocationDetail;
import com.retrip.trip.infra.adapter.out.client.webclient.api.response.LocationDetailsResponse;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class MapApiClientFallback {

    public List<LocationDetail> findAll(List<UUID> locationDetailIds, Throwable cause) {
        log.error(
                "map API 장애 발생 - Fallback 동작 (locationDetailIds: {}, error: {})",
                locationDetailIds,
                cause.getMessage());
        return null;
    }
}
