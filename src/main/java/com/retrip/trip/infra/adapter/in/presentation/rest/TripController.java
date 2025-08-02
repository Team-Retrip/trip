package com.retrip.trip.infra.adapter.in.presentation.rest;

import com.retrip.trip.application.in.request.*;
import com.retrip.trip.application.in.response.*;
import com.retrip.trip.application.in.usecase.*;
import com.retrip.trip.application.in.usecase.CreateTripUseCase;
import com.retrip.trip.application.in.usecase.GetTripUseCase;
import com.retrip.trip.application.in.usecase.TripConfirmationUseCase;
import com.retrip.trip.application.in.usecase.TripDemandUseCase;
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
    private final ManageTripUseCase manageTripUseCase;
    private final GetTripUseCase getTripUseCase;
    private final TripDemandUseCase tripDemandUseCase;
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
    public ApiResponse<TripCreateResponse> createTrip(@RequestBody TripCreateRequest request) {
        TripCreateResponse trip = manageTripUseCase.createTrip(request);
        return ApiResponse.created(trip);
    }

    @PostMapping("/regular")
    @Schema(description = "일정이 포함된 여행 생성")
    public ApiResponse<TripCreateResponse> createTripWithItineraries(
            @RequestBody TripCreateRequest request) {
        TripCreateResponse trip = manageTripUseCase.createTripWithItineraries(request);
        return ApiResponse.created(trip);
    }

    @PutMapping("/{tripId}")
    @Schema(description = "여행 공개 여부 변경")
    public ApiResponse<TripUpdateVisibilityResponse> updateTripVisibility(
            @PathVariable UUID tripId, @RequestBody TripUpdateVisibilityRequest request) {
        TripUpdateVisibilityResponse trip = manageTripUseCase.updateTripVisibility(tripId, request);
        return ApiResponse.created(trip);
    }

    @GetMapping
    @Schema(description = "여행 목록 조회")
    public ApiResponse<Page<TripResponse>> getTrips(
            @PageableDefault(size = 10, page = 0) Pageable page) {
        Page<TripResponse> trips = getTripUseCase.getTrips(page);
        return ApiResponse.ok(trips);
    }

    @PostMapping("/{tripId}/demand")
    @Schema(description = "여행 참가 신청")
    public ApiResponse<TripDemandResponse> joinTrip(
            @PathVariable("tripId") UUID tripId, @RequestBody TripDemandRequest request) {
        TripDemandResponse response = tripDemandUseCase.tripDemand(tripId, request);
        return ApiResponse.ok(response);
    }

    @PutMapping("/{tripId}/period")
    @Schema(description = "여행 기간 수정")
    public ResponseEntity<PeriodUpdateResponse> updatePeriod(
            @PathVariable UUID tripId, @RequestBody PeriodUpdateRequest request) {
        PeriodUpdateResponse period = tripPeriodUseCase.updatePeriod(tripId, request);
        return ResponseEntity.ok().body(period);
    }

    @PutMapping("/{tripId}/demand/{tripDemandId}/approve")
    @Schema(description = "여행 참가 신청 승인")
    public ApiResponse<TripDemandApproveResponse> approveRequest(@RequestParam("memberId") UUID memberId, //TODO: 추후 로그인 구현되면 이부분은 바뀔 에정
                                                                 @PathVariable("tripId") UUID tripId,
                                                                 @PathVariable("tripDemandId") UUID tripDemandId) {
        TripDemandApproveResponse response = tripDemandUseCase.approve(memberId, tripId, tripDemandId);
        return ApiResponse.ok(response);
    }

    @PutMapping("/{tripId}/demand/{tripDemandId}/reject")
    @Schema(description = "여행 참가 신청 거절")
    public ApiResponse<TripDemandRejectResponse> rejectRequest(@RequestParam("memberId") UUID memberId, //TODO: 추후 로그인 구현되면 이부분은 바뀔 에정
                                                               @PathVariable("tripId") UUID tripId,
                                                               @PathVariable("tripDemandId") UUID tripDemandId) {
        TripDemandRejectResponse response = tripDemandUseCase.reject(memberId, tripId, tripDemandId);
        return ApiResponse.ok(response);
    }

    @GetMapping("/my")
    @Schema(description = "나의 여행 목록 조회")
    public ApiResponse<Page<TripResponse>> getMyTrips(
            @RequestParam("memberId") UUID memberId, //TODO: 추후 로그인 구현되면 이부분은 바뀔 에정
            @PageableDefault(size = 10, page = 0) Pageable page) {
        Page<TripResponse> trips = getTripUseCase.getMyTrips(memberId, page);
        return ApiResponse.ok(trips);
    }

    @DeleteMapping("/{tripId}/participants/{memberId}")
    @Schema(description = "여행 나가기")
    public ApiResponse<Void> leaveTrip(
            @PathVariable UUID tripId,
            @PathVariable UUID memberId) { //TODO: 추후 로그인 구현되면 이부분은 바뀔 에정
        leaveTripUseCase.leaveTrip(tripId, memberId);
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
    public ApiResponse<TripDemandRejectResponse> banMembers(@RequestParam("memberId") UUID memberId, //TODO: 추후 로그인 구현되면 이부분은 바뀔 에정
                                                            @PathVariable("tripId") UUID tripId,
                                                            @RequestBody TripMemberBanRequest request) {
        tripDemandUseCase.banMembers(memberId, tripId, request.memberIds());
        return ApiResponse.noContent();
    }

    @PostMapping("/{tripId}/confirm/demand")
    @Schema(description = "여행 확정 요청")
    public ResponseEntity<?> demandTripConfirmation(@RequestParam("memberId") UUID memberId, //TODO: 추후 로그인 구현되면 이부분은 바뀔 에정,
                                                    @PathVariable UUID tripId,
                                                    @RequestBody TripConfirmationDemandRequest request) {
        tripConfirmationUseCase.demandTripConfirmation(memberId, tripId, request);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{tripId}/confirm/{confirmationDemandId}/re-demand")
    @Schema(description = "여행 확정 재요청")
    public ResponseEntity<?> demandAgainTripConfirmation(@RequestParam("memberId") UUID memberId, //TODO: 추후 로그인 구현되면 이부분은 바뀔 에정,
                                                         @PathVariable UUID tripId,
                                                         @PathVariable UUID confirmationDemandId,
                                                         @RequestBody TripConfirmationDemandRequest request) {
        tripConfirmationUseCase.demandAgainTripConfirmation(memberId, tripId, confirmationDemandId, request);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{tripId}/confirm/{confirmationDemandId}/accept")
    @Schema(description = "여행 확정 요청 수락")
    public ResponseEntity<?> acceptConfirmationRequest(@RequestParam("memberId") UUID memberId, //TODO: 추후 로그인 구현되면 이부분은 바뀔 에정,
                                                       @PathVariable UUID tripId,
                                                       @PathVariable UUID confirmationDemandId) {
        ConfirmationDemandAcceptResponse response = tripConfirmationUseCase.acceptConfirmationDemand(memberId, tripId, confirmationDemandId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{tripId}/confirm/{confirmationDemandId}/reject")
    @Schema(description = "여행 확정 요청 거절")
    public ResponseEntity<?> rejectConfirmationRequest(@RequestParam("memberId") UUID memberId, //TODO: 추후 로그인 구현되면 이부분은 바뀔 에정,
                                                       @PathVariable UUID tripId,
                                                       @PathVariable UUID confirmationDemandId) {
        tripConfirmationUseCase.rejectConfirmationDemand(memberId, tripId, confirmationDemandId);
        return ResponseEntity.noContent().build();
    }
}
