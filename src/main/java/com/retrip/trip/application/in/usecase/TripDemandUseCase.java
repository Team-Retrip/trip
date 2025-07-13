package com.retrip.trip.application.in.usecase;

import com.retrip.trip.application.in.request.TripDemandRequest;
import com.retrip.trip.application.in.response.TripDemandApproveResponse;
import com.retrip.trip.application.in.response.TripDemandRejectResponse;
import com.retrip.trip.application.in.response.TripDemandResponse;
import java.util.List;
import java.util.UUID;

public interface TripDemandUseCase {
    TripDemandResponse tripDemand(UUID tripId, TripDemandRequest request);

    TripDemandApproveResponse approve(UUID memberId, UUID tripId, UUID tripDemandId);

    TripDemandRejectResponse reject(UUID memberId, UUID tripId, UUID tripDemandId);

    void banMembers(UUID memberId, UUID tripId, List<UUID> memberIds);
}
