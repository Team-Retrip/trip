package com.retrip.trip.infra.adapter.in.presentation.rest;

import com.retrip.trip.application.in.request.ItineraryDetailMoveRequest;
import com.retrip.trip.application.in.request.ItineraryDetailReorderRequest;
import com.retrip.trip.application.in.request.ItineraryDetailsBulkCreateRequest;
import com.retrip.trip.application.in.request.ItineraryDetailsCreateRequest;
import com.retrip.trip.application.in.request.ItineraryDetailsUpdateRequest;
import com.retrip.trip.application.in.response.ItineraryDetailsCreateResponse;
import com.retrip.trip.application.in.response.ItineraryDetailsUpdateResponse;
import com.retrip.trip.application.in.response.ItineraryResponse;
import com.retrip.trip.application.in.usecase.GetItinerariesUseCase;
import com.retrip.trip.application.in.usecase.ManageItineraryDetailsUseCase;
import com.retrip.trip.domain.exception.annotation.ApiErrorCodeExample;
import com.retrip.trip.domain.exception.annotation.ApiErrorCodeExamples;
import com.retrip.trip.infra.adapter.in.presentation.rest.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

import static com.retrip.trip.domain.exception.common.ErrorCode.*;

@Tag(name = "Itinerary", description = "여행 일정 관리 API")
@RequiredArgsConstructor
@RequestMapping("/trips")
@RestController
public class ItineraryController {
    private final ManageItineraryDetailsUseCase manageItineraryDetailsUseCase;
    private final GetItinerariesUseCase getItinerariesUseCase;

    @Operation(summary = "여행 일정 목록 조회", description = "날짜 오름차순, 세부일정은 sortOrder 오름차순 정렬")
    @GetMapping("/{tripId}/itineraries")
    public ApiResponse<List<ItineraryResponse>> getItineraries(@PathVariable UUID tripId) {
        return ApiResponse.ok(getItinerariesUseCase.getItineraries(tripId));
    }

    @Operation(summary = "세부일정 추가", description = "장소(locationId)만 필수. 시간/메모는 이후 수정 API로 추가")
    @ApiErrorCodeExamples({INVITATION_NOT_FOUND, INVALID_INPUT_VALUE, ITINERARY_NOT_FOUND})
    @PostMapping("/{tripId}/itineraries/{itineraryId}/itineraryDetails")
    public ApiResponse<ItineraryDetailsCreateResponse> createItineraryDetails(
            @PathVariable UUID tripId,
            @PathVariable UUID itineraryId,
            @RequestBody ItineraryDetailsCreateRequest request) {
        return ApiResponse.created(manageItineraryDetailsUseCase.createItineraryDetails(tripId, itineraryId, request));
    }

    @Operation(summary = "세부일정 수정", description = "time/memo/locationId 모두 선택 사항 (null 전송 시 해당 필드 미변경)")
    @ApiErrorCodeExamples({INVITATION_NOT_FOUND, INVALID_INPUT_VALUE, ENTITY_NOT_FOUND, ITINERARY_NOT_FOUND})
    @PutMapping("/{tripId}/itineraries/{itineraryId}/itineraryDetails/{itineraryDetailId}")
    public ApiResponse<ItineraryDetailsUpdateResponse> updateItineraryDetails(
            @PathVariable UUID tripId,
            @PathVariable UUID itineraryId,
            @PathVariable UUID itineraryDetailId,
            @RequestBody ItineraryDetailsUpdateRequest request) {
        return ApiResponse.ok(manageItineraryDetailsUseCase.updateItineraryDetails(tripId, itineraryId, itineraryDetailId, request));
    }

    @Operation(summary = "세부일정 일괄 생성", description = "삭제된 하루 일정 복구(실행취소)용. items 순서대로 sortOrder 부여 (프론트가 삭제 전 데이터를 메모리에 보관한 걸 보내면 됨)")
    @ApiErrorCodeExamples({INVITATION_NOT_FOUND, INVALID_INPUT_VALUE, ITINERARY_NOT_FOUND})
    @PostMapping("/{tripId}/itineraries/{itineraryId}/itineraryDetails/bulk")
    public ApiResponse<List<ItineraryDetailsCreateResponse>> bulkCreateItineraryDetails(
            @PathVariable UUID tripId,
            @PathVariable UUID itineraryId,
            @Valid @RequestBody ItineraryDetailsBulkCreateRequest request) {
        return ApiResponse.created(manageItineraryDetailsUseCase.bulkCreateItineraryDetails(tripId, itineraryId, request));
    }

    @Operation(summary = "하루 세부일정 전체 삭제", description = "해당 날짜(itinerary)의 세부일정을 모두 삭제. 일자 자체는 삭제되지 않음")
    @ApiErrorCodeExamples({INVITATION_NOT_FOUND, ITINERARY_NOT_FOUND})
    @DeleteMapping("/{tripId}/itineraries/{itineraryId}/itineraryDetails")
    public ApiResponse<Void> deleteAllItineraryDetails(
            @PathVariable UUID tripId,
            @PathVariable UUID itineraryId) {
        manageItineraryDetailsUseCase.deleteAllItineraryDetails(tripId, itineraryId);
        return ApiResponse.noContent();
    }

    @Operation(summary = "세부일정 삭제")
    @ApiErrorCodeExample(INVITATION_NOT_FOUND)
    @DeleteMapping("/{tripId}/itineraries/{itineraryId}/itineraryDetails/{itineraryDetailsId}")
    public ApiResponse<Void> deleteItineraryDetail(
            @PathVariable UUID tripId,
            @PathVariable UUID itineraryId,
            @PathVariable UUID itineraryDetailsId) {
        manageItineraryDetailsUseCase.deleteItineraryDetail(tripId, itineraryId, itineraryDetailsId);
        return ApiResponse.noContent();
    }

    @Operation(summary = "세부일정 날짜 이동",
            description = "세부일정을 다른 날짜(itinerary)로 이동. 원본 날짜의 sortOrder 재정렬 및 대상 날짜 맨 뒤에 추가")
    @ApiErrorCodeExamples({INVITATION_NOT_FOUND, ENTITY_NOT_FOUND, ITINERARY_NOT_FOUND})
    @PatchMapping("/{tripId}/itineraries/{itineraryId}/itineraryDetails/{itineraryDetailId}/move")
    public ApiResponse<Void> moveItineraryDetail(
            @PathVariable UUID tripId,
            @PathVariable UUID itineraryId,
            @PathVariable UUID itineraryDetailId,
            @RequestBody ItineraryDetailMoveRequest request) {
        manageItineraryDetailsUseCase.moveItineraryDetail(tripId, itineraryId, itineraryDetailId, request);
        return ApiResponse.noContent();
    }

    @Operation(summary = "세부일정 순서 변경", description = "orderedIds 순서대로 sortOrder 재설정")
    @ApiErrorCodeExamples({INVITATION_NOT_FOUND, ENTITY_NOT_FOUND, ITINERARY_NOT_FOUND})
    @PatchMapping("/{tripId}/itineraries/{itineraryId}/itineraryDetails/order")
    public ApiResponse<Void> reorderItineraryDetails(
            @PathVariable UUID tripId,
            @PathVariable UUID itineraryId,
            @RequestBody ItineraryDetailReorderRequest request) {
        manageItineraryDetailsUseCase.reorderItineraryDetails(tripId, itineraryId, request);
        return ApiResponse.noContent();
    }
}