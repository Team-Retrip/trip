package com.retrip.trip.application.out.gateway.model;

import java.util.UUID;

public record LocationDetail(
        UUID locationDetailId,
        String name,
        String category
) {
}
