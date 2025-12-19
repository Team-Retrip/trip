package com.retrip.trip.infra.adapter.in.presentation.rest;

import com.retrip.trip.application.in.request.context.UserContext;
import com.retrip.trip.application.in.request.context.WithUserContext;
import com.retrip.trip.application.in.request.demand.TripDemandRequest;
import com.retrip.trip.application.in.response.demand.DemandApproveResponse;
import com.retrip.trip.application.in.response.demand.DemandRejectResponse;
import com.retrip.trip.application.in.response.demand.DemandResponse;
import com.retrip.trip.application.in.response.demand.DemandsResponse;
import com.retrip.trip.application.in.usecase.DemandManageUseCase;
import com.retrip.trip.infra.adapter.in.presentation.rest.common.ApiResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping("/trips")
@RestController
public class DemandController {
    private final DemandManageUseCase demandManageUseCase;

    @PostMapping("/{tripId}/demand")
    @Schema(description = "여행 참가 신청")
    public ApiResponse<DemandResponse> joinTrip(
            @WithUserContext UserContext userContext,
            @PathVariable("tripId") UUID tripId,
            @RequestBody TripDemandRequest request) {
        DemandResponse response = demandManageUseCase.demand(userContext.memberId(), tripId, request);
        return ApiResponse.ok(response);
    }

    @GetMapping("/{tripId}/demand")
    @Schema(description = "리더 여행 참가 신청 목록 조회")
    public ApiResponse<List<DemandsResponse>> getTripDemands(@WithUserContext UserContext userContext,
                                                             @PathVariable("tripId") UUID tripId) {
        List<DemandsResponse> response = demandManageUseCase.getDemands(userContext.memberId(), tripId);
        return ApiResponse.ok(response);
    }

    @PutMapping("/{tripId}/demand/{demandId}/approve")
    @Schema(description = "여행 참가 신청 승인")
    public ApiResponse<DemandApproveResponse> approveRequest(@WithUserContext UserContext userContext,
                                                             @PathVariable("tripId") UUID tripId,
                                                             @PathVariable("demandId") UUID demandId) {
        DemandApproveResponse response = demandManageUseCase.approve(userContext.memberId(), tripId, demandId);
        return ApiResponse.ok(response);
    }

    @PutMapping("/{tripId}/demand/{demandId}/reject")
    @Schema(description = "여행 참가 신청 거절")
    public ApiResponse<DemandRejectResponse> rejectRequest(@WithUserContext UserContext userContext,
                                                           @PathVariable("tripId") UUID tripId,
                                                           @PathVariable("demandId") UUID demandId) {
        DemandRejectResponse response = demandManageUseCase.reject(userContext.memberId(), tripId, demandId);
        return ApiResponse.ok(response);
    }
}
