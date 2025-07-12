package com.retrip.trip.infra.adapter.in.presentation.rest;

import com.retrip.trip.application.in.request.PeriodUpdateRequest;
import com.retrip.trip.application.in.request.TripCreateRequest;
import com.retrip.trip.application.in.request.TripDemandRequest;
import com.retrip.trip.application.in.request.TripMemberBanRequest;
import com.retrip.trip.application.in.response.*;
import com.retrip.trip.application.in.usecase.CreateTripUseCase;
import com.retrip.trip.application.in.usecase.GetTripUseCase;
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
    private final CreateTripUseCase createTripUseCase;
    private final GetTripUseCase getTripUseCase;
    private final TripDemandUseCase tripDemandUseCase;
    private final TripPeriodUseCase tripPeriodUseCase;

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
        TripCreateResponse trip = createTripUseCase.createTrip(request);
        return ApiResponse.created(trip);
    }

    @PostMapping("/regular")
    @Schema(description = "일정이 포함된 여행 생성")
    public ApiResponse<TripCreateResponse> createTripWithItineraries(
            @RequestBody TripCreateRequest request) {
        TripCreateResponse trip = createTripUseCase.createTripWithItineraries(request);
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

    @DeleteMapping("/{tripId}/members/ban")
    @Schema(description = "여행 멤버 리스트 강퇴")
    public ApiResponse<TripDemandRejectResponse> banMembers(@RequestParam("memberId") UUID memberId, //TODO: 추후 로그인 구현되면 이부분은 바뀔 에정
                                                           @PathVariable("tripId") UUID tripId,
                                                           @RequestBody TripMemberBanRequest request) {
        tripDemandUseCase.banMembers(memberId, tripId, request.memberIds());
        return ApiResponse.noContent();
    }
}
