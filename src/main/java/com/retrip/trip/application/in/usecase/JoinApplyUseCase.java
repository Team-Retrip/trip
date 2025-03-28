package com.retrip.trip.application.in.usecase;

import com.retrip.trip.application.in.request.TripJoinApplyRequest;
import com.retrip.trip.application.in.response.TripJoinResponse;

public interface JoinApplyUseCase {
    TripJoinResponse JoinApply(TripJoinApplyRequest request);
}
