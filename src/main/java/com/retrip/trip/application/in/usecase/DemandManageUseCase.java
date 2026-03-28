package com.retrip.trip.application.in.usecase;

import com.retrip.trip.application.in.request.context.UserContext;
import com.retrip.trip.application.in.request.demand.TripDemandRequest;
import com.retrip.trip.application.in.response.demand.DemandApproveResponse;
import com.retrip.trip.application.in.response.demand.DemandRejectResponse;
import com.retrip.trip.application.in.response.demand.DemandResponse;
import com.retrip.trip.application.in.response.demand.DemandsResponse;

import java.util.List;
import java.util.UUID;

public interface DemandManageUseCase {
    DemandResponse demand(UserContext context, UUID tripId, TripDemandRequest request);

    DemandApproveResponse approve(UUID memberId, UUID tripId, UUID demandId);

    DemandRejectResponse reject(UUID memberId, UUID tripId, UUID demandId);

    List<DemandsResponse> getDemands(UUID memberId, UUID tripId);
}
