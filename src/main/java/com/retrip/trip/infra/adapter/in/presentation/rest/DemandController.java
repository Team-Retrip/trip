package com.retrip.trip.infra.adapter.in.presentation.rest;

import com.retrip.trip.application.in.request.context.UserContext;
import com.retrip.trip.application.in.request.context.WithUserContext;
import com.retrip.trip.application.in.request.demand.TripDemandRequest;
import com.retrip.trip.application.in.response.MyPageDemandResponse;
import com.retrip.trip.application.in.response.demand.DemandApproveResponse;
import com.retrip.trip.application.in.response.demand.DemandRejectResponse;
import com.retrip.trip.application.in.response.demand.DemandResponse;
import com.retrip.trip.application.in.response.demand.DemandsResponse;
import com.retrip.trip.application.in.usecase.DemandManageUseCase;
import com.retrip.trip.domain.exception.annotation.ApiErrorCodeExamples;
import com.retrip.trip.domain.vo.TripCategory;
import com.retrip.trip.domain.vo.TripStatus;
import com.retrip.trip.infra.adapter.in.presentation.rest.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static com.retrip.trip.domain.exception.common.ErrorCode.*;

@RequiredArgsConstructor
@RequestMapping("/trips")
@RestController
@Tag(name = "Demand", description = "여행 참가 신청 관련 API")
public class DemandController {
    private final DemandManageUseCase demandManageUseCase;

    @Operation(
            summary = "여행 참가 신청",
            description = "여행에 참가 신청을 하는 API"
    )
    @ApiErrorCodeExamples({TRIP_NOT_FOUND, TRIP_MEMBER_BANNED_CANNOT_APPLY, TRIP_DEMAND_NOT_ALLOWED, TRIP_NOT_RECRUITING, TRIP_PARTICIPANTS_IS_FULL})
    @PostMapping("/{tripId}/demand")
    public ApiResponse<DemandResponse> joinTrip(
            @WithUserContext UserContext userContext,
            @PathVariable("tripId") UUID tripId,
            @RequestBody TripDemandRequest request) {
        DemandResponse response = demandManageUseCase.demand(userContext.memberId(), userContext.nickName(), tripId, request);
        return ApiResponse.ok(response);
    }

    @Operation(
            summary = "리더 여행 참가 신청 목록 조회",
            description = "리더가 여행 참가 신청 목록을 조회하는 API"
    )
    @ApiErrorCodeExamples({TRIP_NOT_FOUND, MEMBER_IS_NOT_LEADER})
    @GetMapping("/{tripId}/demand")
    public ApiResponse<List<DemandsResponse>> getTripDemands(@WithUserContext UserContext userContext,
                                                             @PathVariable("tripId") UUID tripId) {
        List<DemandsResponse> response = demandManageUseCase.getDemands(userContext.memberId(), tripId);
        return ApiResponse.ok(response);
    }

    @Operation(
            summary = "여행 참가 신청 승인",
            description = "리더가 여행 참가 신청 승인을 하는 API"
    )
    @ApiErrorCodeExamples({TRIP_NOT_FOUND, DEMAND_NOT_FOUND, MEMBER_IS_NOT_LEADER, TRIP_DEMAND_STATUS_NOT_PENDING, TRIP_FULL})
    @PutMapping("/{tripId}/demand/{demandId}/approve")
    public ApiResponse<DemandApproveResponse> approveRequest(@WithUserContext UserContext userContext,
                                                             @PathVariable("tripId") UUID tripId,
                                                             @PathVariable("demandId") UUID demandId) {
        DemandApproveResponse response = demandManageUseCase.approve(userContext.memberId(), tripId, demandId);
        return ApiResponse.ok(response);
    }

    @Operation(
            summary = "여행 참가 신청 거절",
            description = "리더가 여행 참가 신청 거절을 하는 API"
    )
    @ApiErrorCodeExamples({TRIP_NOT_FOUND, DEMAND_NOT_FOUND, MEMBER_IS_NOT_LEADER, TRIP_DEMAND_STATUS_NOT_PENDING})
    @PutMapping("/{tripId}/demand/{demandId}/reject")
    public ApiResponse<DemandRejectResponse> rejectRequest(@WithUserContext UserContext userContext,
                                                           @PathVariable("tripId") UUID tripId,
                                                           @PathVariable("demandId") UUID demandId) {
        DemandRejectResponse response = demandManageUseCase.reject(userContext.memberId(), tripId, demandId);
        return ApiResponse.ok(response);
    }

    @Operation(
            summary = "여행 참가 신청 취소",
            description = "본인이 신청한 여행 참가 신청을 취소하는 API (대기 상태일 때만 가능)"
    )
    @ApiErrorCodeExamples({DEMAND_NOT_FOUND, DEMAND_CANCEL_NOT_ALLOWED})
    @DeleteMapping("/{tripId}/demand/{demandId}")
    public ApiResponse<Void> cancelDemand(
            @WithUserContext UserContext userContext,
            @PathVariable("tripId") UUID tripId,
            @PathVariable("demandId") UUID demandId) {
        demandManageUseCase.cancelDemand(userContext.memberId(), demandId);
        return ApiResponse.noContent();
    }

    @Operation(
            summary = "마이페이지 신청함 목록 조회",
            description = "로그인한 회원이 신청한 여행 목록을 조회합니다. 여행 상태, 국내/해외, 기간 필터를 지원합니다."
    )
    @GetMapping("/members/my-demands")
    public ApiResponse<Page<MyPageDemandResponse>> getMyPageDemands(
            @WithUserContext UserContext userContext,
            @Parameter(description = "여행 상태 필터 (복수 선택 가능)", example = "RECRUITING")
            @RequestParam(name = "tripStatus", required = false) List<TripStatus> tripStatuses,
            @Parameter(description = "여행 카테고리 필터 (DOMESTIC: 국내, OVERSEAS: 해외)", example = "DOMESTIC")
            @RequestParam(name = "category", required = false) TripCategory category,
            @Parameter(description = "기간 필터 (RECENT_6_MONTHS 또는 연도 예: 2026)", example = "RECENT_6_MONTHS")
            @RequestParam(name = "period", required = false) String period,
            @PageableDefault(size = 10, page = 0) Pageable pageable) {
        Page<MyPageDemandResponse> result =
                demandManageUseCase.getMyPageDemands(userContext.memberId(), tripStatuses, category, period, pageable);
        return ApiResponse.ok(result);
    }
}
