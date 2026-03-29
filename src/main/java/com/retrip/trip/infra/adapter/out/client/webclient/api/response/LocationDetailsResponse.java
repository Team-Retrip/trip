package com.retrip.trip.infra.adapter.out.client.webclient.api.response;

import java.util.List;
import java.util.UUID;

public record LocationDetailsResponse(
        List<LocationDetailResponse> responses
) {
    public record LocationDetailResponse(
            UUID id,
            String name,
            String category,
            String description,
            String telephone,
            String address,
            String roadAddress,
            Double latitude,
            Double longitude
    ) {
    }
}
