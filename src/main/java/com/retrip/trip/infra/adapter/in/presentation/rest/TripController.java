package com.retrip.trip.infra.adapter.in.presentation.rest;

import com.retrip.trip.application.in.request.*;
import com.retrip.trip.application.in.request.context.UserContext;
import com.retrip.trip.application.in.request.context.WithUserContext;
import com.retrip.trip.application.in.response.*;
import com.retrip.trip.application.in.usecase.*;
import com.retrip.trip.domain.exception.annotation.ApiErrorCodeExample;
import com.retrip.trip.domain.exception.annotation.ApiErrorCodeExamples;
import com.retrip.trip.domain.vo.TripCategory;
import com.retrip.trip.domain.vo.TripStatus;
import com.retrip.trip.infra.adapter.in.presentation.rest.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

import static com.retrip.trip.domain.exception.common.ErrorCode.*;

@Tag(name = "Trip", description = "여행 관련 API")
@RequiredArgsConstructor
@RequestMapping("/trips")
@RestController
public class TripController {
    private final TripManageUseCase tripManageUseCase;
    private final GetTripUseCase getTripUseCase;
    private final TripPeriodUseCase tripPeriodUseCase;
    private final LeaveTripUseCase leaveTripUseCase;
    private final DelegateLeaderUseCase delegateLeaderUseCase;
//    private final TripConfirmationUseCase tripConfirmationUseCase;

    @Operation(
            summary = "여행 카테고리 목록 조회",
            description = "여행 카테고리 목록을 조회하는 API"
    )
    @GetMapping("/categories")
    public ApiResponse<List<TripCategoryResponse>> getTripCategories() {
        List<TripCategoryResponse> response =
                Arrays.stream(TripCategory.values()).map(TripCategoryResponse::of).toList();
        return ApiResponse.ok(response);
    }

    @Operation(
            summary = "여행 생성",
            description = "일정이 포함된 여행을 생성하는 API"
    )
    @ApiErrorCodeExamples({TRIP_DAY_MUST_BE_POSITIVE, INVALID_MAX_PARTICIPANTS_VALUE, INVALID_HASHTAG_LENGTH, PRIVATE_TRIP_PASSWORD_REQUIRED, TRIP_PASSWORD_INVALID})
    @PostMapping
    public ApiResponse<TripCreateResponse> createTrip(
            @WithUserContext UserContext userContext,
            @RequestBody TripCreateRequest request) {
        TripCreateResponse trip = tripManageUseCase.createTripWithItineraries(userContext.memberId(), request);
        return ApiResponse.created(trip);
    }

    @Operation(
            summary = "여행 수정",
            description = "여행을 수정하는 API"
    )
    @PutMapping("/{tripId}")
    public ApiResponse<TripUpdateResponse> updateTrip(
            @WithUserContext UserContext userContext,
            @PathVariable UUID tripId,
            @RequestBody TripUpdateRequest request) {
        TripUpdateResponse trip = tripManageUseCase.updateTrip(userContext.memberId(), tripId, request);
        return ApiResponse.ok(trip);
    }

    @Operation(
            summary = "여행 공개 여부 변경",
            description = "여행 공개 여부를 변경하는 API"
    )
    @ApiErrorCodeExamples({TRIP_NOT_FOUND, PRIVATE_TRIP_PASSWORD_REQUIRED, TRIP_PASSWORD_INVALID})
    @PutMapping("/open/{tripId}")
    public ApiResponse<TripUpdateVisibilityResponse> updateTripVisibility(
            @WithUserContext UserContext userContext,
            @PathVariable UUID tripId, @RequestBody TripUpdateVisibilityRequest request) {
        TripUpdateVisibilityResponse trip = tripManageUseCase.updateTripVisibility(userContext.memberId(), tripId, request);
        return ApiResponse.created(trip);
    }

    @Operation(
            summary = "여행 목록 조회",
            description = "여행 목록을 조회하는 API"
    )
    @GetMapping
    public ApiResponse<Page<TripResponse>> getTrips(
            @Parameter(description = "여행 상태 필터 (복수 선택 가능)",
                    schema = @Schema(type = "array", allowableValues = {"RECRUITING", "RECRUITMENT_CLOSED", "IN_PROGRESS", "COMPLETED"}))
            @RequestParam(required = false) List<TripStatus> tripStatuses,
            @Parameter(description = "성별 필터 (복수 선택 가능)",
                    schema = @Schema(type = "array", allowableValues = {"남자", "여자", "혼성"}))
            @RequestParam(required = false) List<String> genders,
            @Parameter(description = "연령대 필터 (복수 선택 가능)",
                    schema = @Schema(type = "array", allowableValues = {"10대", "20대", "30대", "40대", "50대", "60대이상", "상관없음"}))
            @RequestParam(required = false) List<String> ages,
            @PageableDefault(size = 10, page = 0) Pageable page) {
        Page<TripResponse> trips = getTripUseCase.getTrips(tripStatuses, genders, ages, page);
        return ApiResponse.ok(trips);
    }

    @Operation(
            summary = "여행 상세 조회",
            description = "tripId를 이용하여 여행 상세 정보를 조회하는 API"
    )
    @ApiErrorCodeExample(TRIP_NOT_FOUND)
    @GetMapping("/{tripId}")
    public ApiResponse<TripDetailResponse> getTripDetail(@WithUserContext UserContext userContext,
                                                         @PathVariable UUID tripId) {
        TripDetailResponse tripDetail = getTripUseCase.getTripDetail(userContext.memberId(), tripId);
        return ApiResponse.ok(tripDetail);
    }

    @Operation(
            summary = "여행 기간 수정",
            description = "여행 기간을 수정하는 API"
    )
    @ApiErrorCodeExamples({TRIP_NOT_FOUND, TRIP_START_DATE_IN_PAST, TRIP_END_DATE_BEFORE_START, TRIP_DURATION_EXCEEDS_LIMIT, PERIOD_UPDATE_FAIL, TRIP_DAY_MUST_BE_POSITIVE})
    @PutMapping("/{tripId}/period")
    public ResponseEntity<PeriodUpdateResponse> updatePeriod(
            @WithUserContext UserContext userContext, @PathVariable UUID tripId, @RequestBody PeriodUpdateRequest request) {
        PeriodUpdateResponse period = tripPeriodUseCase.updatePeriod(userContext.memberId(), tripId, request);
        return ResponseEntity.ok().body(period);
    }

    @Operation(
            summary = "나의 여행 목록 조회",
            description = "나의 여행 목록 조회는"
    )
    @GetMapping("/my")
    public ApiResponse<Page<MyTripResponse>> getMyTrips(@WithUserContext UserContext userContext,
                                                        @Parameter(description = "여행 상태 필터 (복수 선택 가능)",
                                                                schema = @Schema(type = "array", allowableValues = {"RECRUITING", "RECRUITMENT_CLOSED", "IN_PROGRESS", "COMPLETED"}))
                                                        @RequestParam(required = false) List<TripStatus> tripStatuses,
                                                        @Parameter(description = "성별 필터 (복수 선택 가능)", example = "[\"남자\", \"여자\", \"혼성\"]", schema = @Schema(type = "array", allowableValues = {"남자", "여자", "혼성"}))
                                                        @RequestParam(required = false) List<String> genders,
                                                        @Parameter(description = "연령대 필터 (복수 선택 가능)", example = "[\"20대\", \"30대\"]", schema = @Schema(type = "array", allowableValues = {"10대", "20대", "30대", "40대", "50대", "60대 이상", "상관없음"}))
                                                        @RequestParam(required = false) List<String> ages,
                                                        @PageableDefault(size = 10, page = 0) Pageable page) {
        Page<MyTripResponse> trips = getTripUseCase.getMyTrips(userContext.memberId(), tripStatuses, genders, ages, page);
        return ApiResponse.ok(trips);
    }

    @Operation(
            summary = "여행 나가기",
            description = "여행 나가기 API"
    )
    @ApiErrorCodeExamples({TRIP_NOT_FOUND, TRIP_NOT_READY, NOT_PARTICIPANT, LEADER_CANNOT_LEAVE})
    @DeleteMapping("/{tripId}/participants")
    public ApiResponse<Void> leaveTrip(
            @PathVariable UUID tripId,
            @WithUserContext UserContext userContext) {
        leaveTripUseCase.leaveTrip(tripId, userContext.memberId());
        return ApiResponse.noContent();
    }

    @Operation(
            summary = "여행 리더 위임",
            description = "리더가 해당 여행의 리더를 위임하는 API"
    )
    @ApiErrorCodeExamples({TRIP_NOT_FOUND, TRIP_NOT_READY, MEMBER_IS_NOT_LEADER, CANNOT_DELEGATE_LEADER_TO_SELF, NOT_PARTICIPANT})
    @PutMapping("/{tripId}/delegate-leader")
    public ApiResponse<DelegateLeaderResponse> delegateLeader(
            @WithUserContext UserContext userContext,
            @PathVariable UUID tripId,
            @RequestBody DelegateLeaderRequest request) {
        DelegateLeaderResponse response = delegateLeaderUseCase.delegateLeader(tripId, userContext.memberId(), request);
        return ApiResponse.ok(response);
    }

    @Operation(
            summary = "여행 멤버 리스트 강퇴",
            description = "여행 멤버 여러명을 강퇴할 수 있는 API"
    )
    @ApiErrorCodeExamples({TRIP_NOT_FOUND, TRIP_NOT_RECRUITING, NOT_TRIP_LEADER, TRIP_MEMBER_NOT_IN_TRIP})
    @DeleteMapping("/{tripId}/members/ban")
    public ApiResponse<?> banMembers(@WithUserContext UserContext userContext,
                                     @PathVariable("tripId") UUID tripId,
                                     @RequestBody TripMemberBanRequest request) {
        tripManageUseCase.banMembers(userContext.memberId(), tripId, request.memberIds());
        return ApiResponse.noContent();
    }

//    @Operation(
//            summary = "여행 확정 요청",
//            description = "리더가 여행 확정 요청을 하는 API"
//    )
//    @ApiErrorCodeExamples({TRIP_NOT_FOUND, NOT_TRIP_LEADER, NOT_TRIP_READY_STATUS, NOT_FOUND_PARTICIPANTS, TRIP_CONFIRMATION_START_AFTER_END, TRIP_CONFIRMATION_PERIOD_OUT_OF_RANGE})
//    @PostMapping("/{tripId}/confirm/demand")
//    public ResponseEntity<?> demandTripConfirmation(@WithUserContext UserContext userContext,
//                                                    @PathVariable UUID tripId,
//                                                    @RequestBody TripConfirmationDemandRequest request) {
//        tripConfirmationUseCase.demandTripConfirmation(userContext.memberId(), tripId, request);
//        return ResponseEntity.noContent().build();
//    }

//    @Operation(
//            summary = "여행 확정 재요청",
//            description = "리더가 여행 확정 재요청을 하는 API"
//    )
//    @ApiErrorCodeExamples({PARTICIPATION_CONFIRM_REQUEST_NOT_FOUND, NOT_TRIP_LEADER, NOT_TRIP_READY_STATUS, NOT_FOUND_PARTICIPANTS, TRIP_CONFIRMATION_START_AFTER_END, TRIP_CONFIRMATION_PERIOD_OUT_OF_RANGE})
//    @PutMapping("/{tripId}/confirm/{confirmationDemandId}/re-demand")
//    public ResponseEntity<?> demandAgainTripConfirmation(@WithUserContext UserContext userContext,
//                                                         @PathVariable UUID tripId,
//                                                         @PathVariable UUID confirmationDemandId,
//                                                         @RequestBody TripConfirmationDemandRequest request) {
//        tripConfirmationUseCase.demandAgainTripConfirmation(userContext.memberId(), tripId, confirmationDemandId, request);
//        return ResponseEntity.noContent().build();
//    }

//    @Operation(
//            summary = "여행 확정 요청 수락",
//            description = "여행 확정 요청을 수락하는 API"
//    )
//    @ApiErrorCodeExamples({PARTICIPATION_CONFIRM_REQUEST_NOT_FOUND, TARGET_ENTITY_NOT_FOUND})
//    @PutMapping("/{tripId}/confirm/{confirmationDemandId}/accept")
//    public ResponseEntity<?> acceptConfirmationRequest(@WithUserContext UserContext userContext,
//                                                       @PathVariable UUID tripId,
//                                                       @PathVariable UUID confirmationDemandId) {
//        ConfirmationDemandAcceptResponse response = tripConfirmationUseCase.acceptConfirmationDemand(userContext.memberId(), tripId, confirmationDemandId);
//        return ResponseEntity.ok(response);
//    }

//    @Operation(
//            summary = "여행 확정 요청 거절",
//            description = "여행 확정 요청을 거절하는 API"
//    )
//    @ApiErrorCodeExamples({PARTICIPATION_CONFIRM_REQUEST_NOT_FOUND, TARGET_ENTITY_NOT_FOUND})
//    @PutMapping("/{tripId}/confirm/{confirmationDemandId}/reject")
//    public ResponseEntity<?> rejectConfirmationRequest(@WithUserContext UserContext userContext,
//                                                       @PathVariable UUID tripId,
//                                                       @PathVariable UUID confirmationDemandId) {
//        tripConfirmationUseCase.rejectConfirmationDemand(userContext.memberId(), tripId, confirmationDemandId);
//        return ResponseEntity.noContent().build();
//    }
}
