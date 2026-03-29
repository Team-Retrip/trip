package com.retrip.trip.application.out.gateway;


import com.retrip.trip.application.out.gateway.model.LocationDetail;

import java.util.List;
import java.util.UUID;

public interface MapGateway {
    List<LocationDetail> findAll(List<UUID> locationDetailIds);
}
