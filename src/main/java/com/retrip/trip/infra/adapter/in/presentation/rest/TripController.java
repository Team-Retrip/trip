package com.retrip.trip.infra.adapter.in.presentation.rest;

import com.retrip.trip.application.in.request.*;
import com.retrip.trip.application.in.request.context.UserContext;
import com.retrip.trip.application.in.request.context.WithUserContext;
import com.retrip.trip.application.in.response.*;
import com.retrip.trip.application.in.usecase.*;
import com.retrip.trip.application.in.usecase.GetTripUseCase;
import com.retrip.trip.application.in.usecase.TripConfirmationUseCase;
import com.retrip.trip.application.in.usecase.TripPeriodUseCase;
import com.retrip.trip.domain.vo.TripCategory;
import com.retrip.trip.infra.adapter.in.presentation.rest.common.ApiResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping("/trips")
@RestController
@Tag(name = "Trip", description = "여행 관련 API")
public class TripController {
    private final TripManageUseCase tripManageUseCase;
    private final GetTripUseCase getTripUseCase;
    private final TripPeriodUseCase tripPeriodUseCase;
    private final LeaveTripUseCase leaveTripUseCase;
    private final DelegateLeaderUseCase delegateLeaderUseCase;
    private final TripConfirmationUseCase tripConfirmationUseCase;

    @GetMapping("/categories")
    @Schema(description = "여행 카테고리 목록 조회")
    public ApiResponse<List<TripCategoryResponse>> getTripCategories() {
        List<TripCategoryResponse> response =
                Arrays.stream(TripCategory.values()).map(TripCategoryResponse::of).toList();
        return ApiResponse.ok(response);
    }

    @PostMapping
    @Schema(description = "여행 생성")
    public ApiResponse<TripCreateResponse> createTrip(
            @WithUserContext UserContext userContext,
            @RequestBody TripCreateRequest request) {
        TripCreateResponse trip = tripManageUseCase.createTrip(userContext.memberId(), request);
        return ApiResponse.created(trip);
    }

    @PostMapping("/regular")
    @Schema(description = "일정이 포함된 여행 생성")
    public ApiResponse<TripCreateResponse> createTripWithItineraries(
            @WithUserContext UserContext userContext,
            @RequestBody TripCreateRequest request) {
        TripCreateResponse trip = tripManageUseCase.createTripWithItineraries(userContext.memberId(), request);
        return ApiResponse.created(trip);
    }

    @PutMapping("/{tripId}")
    @Schema(description = "여행 공개 여부 변경")
    public ApiResponse<TripUpdateVisibilityResponse> updateTripVisibility(
            @PathVariable UUID tripId, @RequestBody TripUpdateVisibilityRequest request) {
        TripUpdateVisibilityResponse trip = tripManageUseCase.updateTripVisibility(tripId, request);
        return ApiResponse.created(trip);
    }

    @GetMapping
    @Schema(description = "여행 목록 조회")
    public ApiResponse<Page<TripResponse>> getTrips(
            @PageableDefault(size = 10, page = 0) Pageable page) {
        Page<TripResponse> trips = getTripUseCase.getTrips(page);
        return ApiResponse.ok(trips);
    }

    @GetMapping("/{tripId}")
    @Schema(description = "여행 상세 조회")
    public ApiResponse<TripDetailResponse> getTripDetail(@WithUserContext UserContext userContext,
                                                         @PathVariable UUID tripId) {
        TripDetailResponse tripDetail = getTripUseCase.getTripDetail(userContext.memberId(), tripId);
        return ApiResponse.ok(tripDetail);
    }

    @PutMapping("/{tripId}/period")
    @Schema(description = "여행 기간 수정")
    public ResponseEntity<PeriodUpdateResponse> updatePeriod(
            @WithUserContext UserContext userContext, @PathVariable UUID tripId, @RequestBody PeriodUpdateRequest request) {
        PeriodUpdateResponse period = tripPeriodUseCase.updatePeriod(userContext.memberId(), tripId, request);
        return ResponseEntity.ok().body(period);
    }

    @GetMapping("/my")
    @Schema(description = "나의 여행 목록 조회")
    public ApiResponse<Page<TripResponse>> getMyTrips(
            @WithUserContext UserContext userContext,
            @PageableDefault(size = 10, page = 0) Pageable page) {
        Page<TripResponse> trips = getTripUseCase.getMyTrips(userContext.memberId(), page);
        return ApiResponse.ok(trips);
    }

    @DeleteMapping("/{tripId}/participants")
    @Schema(description = "여행 나가기")
    public ApiResponse<Void> leaveTrip(
            @PathVariable UUID tripId,
            @WithUserContext UserContext userContext) {
        leaveTripUseCase.leaveTrip(tripId, userContext.memberId());
        return ApiResponse.noContent();
    }

    @PutMapping("/{tripId}/delegate-leader")
    @Schema(description = "여행 리더 위임")
    public ApiResponse<DelegateLeaderResponse> delegateLeader(
            @PathVariable UUID tripId,
            @RequestBody DelegateLeaderRequest request) {
        DelegateLeaderResponse response = delegateLeaderUseCase.delegateLeader(tripId, request);
        return ApiResponse.ok(response);
    }

    @DeleteMapping("/{tripId}/members/ban")
    @Schema(description = "여행 멤버 리스트 강퇴")
    public ApiResponse<?> banMembers(@WithUserContext UserContext userContext,
                                     @PathVariable("tripId") UUID tripId,
                                     @RequestBody TripMemberBanRequest request) {
        tripManageUseCase.banMembers(userContext.memberId(), tripId, request.memberIds());
        return ApiResponse.noContent();
    }

    @PostMapping("/{tripId}/confirm/demand")
    @Schema(description = "여행 확정 요청")
    public ResponseEntity<?> demandTripConfirmation(@WithUserContext UserContext userContext,
                                                    @PathVariable UUID tripId,
                                                    @RequestBody TripConfirmationDemandRequest request) {
        tripConfirmationUseCase.demandTripConfirmation(userContext.memberId(), tripId, request);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{tripId}/confirm/{confirmationDemandId}/re-demand")
    @Schema(description = "여행 확정 재요청")
    public ResponseEntity<?> demandAgainTripConfirmation(@WithUserContext UserContext userContext,
                                                         @PathVariable UUID tripId,
                                                         @PathVariable UUID confirmationDemandId,
                                                         @RequestBody TripConfirmationDemandRequest request) {
        tripConfirmationUseCase.demandAgainTripConfirmation(userContext.memberId(), tripId, confirmationDemandId, request);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{tripId}/confirm/{confirmationDemandId}/accept")
    @Schema(description = "여행 확정 요청 수락")
    public ResponseEntity<?> acceptConfirmationRequest(@WithUserContext UserContext userContext,
                                                       @PathVariable UUID tripId,
                                                       @PathVariable UUID confirmationDemandId) {
        ConfirmationDemandAcceptResponse response = tripConfirmationUseCase.acceptConfirmationDemand(userContext.memberId(), tripId, confirmationDemandId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{tripId}/confirm/{confirmationDemandId}/reject")
    @Schema(description = "여행 확정 요청 거절")
    public ResponseEntity<?> rejectConfirmationRequest(@WithUserContext UserContext userContext,
                                                       @PathVariable UUID tripId,
                                                       @PathVariable UUID confirmationDemandId) {
        tripConfirmationUseCase.rejectConfirmationDemand(userContext.memberId(), tripId, confirmationDemandId);
        return ResponseEntity.noContent().build();
    }
}
