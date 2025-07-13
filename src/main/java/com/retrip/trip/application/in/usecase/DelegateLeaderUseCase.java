package com.retrip.trip.application.in.usecase;

import com.retrip.trip.application.in.request.DelegateLeaderRequest;
import com.retrip.trip.application.in.response.DelegateLeaderResponse;
import java.util.UUID;

public interface DelegateLeaderUseCase {
    DelegateLeaderResponse delegateLeader(UUID tripId, DelegateLeaderRequest request);
}