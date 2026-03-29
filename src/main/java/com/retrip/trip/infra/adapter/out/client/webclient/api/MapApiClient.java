package com.retrip.trip.infra.adapter.out.client.webclient.api;

import com.retrip.trip.infra.adapter.out.client.webclient.api.response.CreateAlarmsResponse;

import com.retrip.trip.infra.adapter.out.client.webclient.api.response.LocationDetailsResponse;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;

public interface MapApiClient {
    @GetExchange("/location-details")
    LocationDetailsResponse findAll(@RequestParam("locationDetailIds") List<UUID> locationDetailIds);
}
