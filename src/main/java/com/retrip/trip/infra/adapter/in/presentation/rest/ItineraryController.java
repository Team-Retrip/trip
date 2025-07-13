package com.retrip.trip.infra.adapter.in.presentation.rest;

import com.retrip.trip.application.in.request.ItineraryDetailsUpdateRequest;
import com.retrip.trip.application.in.request.ItineraryDetailsCreateRequest;
import com.retrip.trip.application.in.response.ItineraryDetailsCreateResponse;
import com.retrip.trip.application.in.response.ItineraryDetailsUpdateResponse;
import com.retrip.trip.application.in.response.ItineraryResponse;
import com.retrip.trip.application.in.usecase.GetItinerariesUseCase;
import com.retrip.trip.application.in.usecase.ManageItineraryDetailsUseCase;
import com.retrip.trip.infra.adapter.in.presentation.rest.common.ApiResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping("/trips")
@RestController
@Tag(name = "Itinerary", description = "여행 일정 관리 API")
public class ItineraryController {
    private final ManageItineraryDetailsUseCase manageItineraryDetailsUseCase;
    private final GetItinerariesUseCase getItinerariesUseCase;

    @PostMapping("/{tripId}/itineraries/{itineraryId}/itineraryDetails")
    @Schema(description = "여행 일정 세부사항 생성")
    public ApiResponse<ItineraryDetailsCreateResponse> createItineraryDetails(
            @PathVariable UUID tripId,
            @PathVariable UUID itineraryId,
            @RequestBody ItineraryDetailsCreateRequest request) {
        ItineraryDetailsCreateResponse itineraryDetail =
                manageItineraryDetailsUseCase.createItineraryDetails(tripId, itineraryId, request);
        return ApiResponse.created(itineraryDetail);
    }

    @PutMapping("/{tripId}/itineraries/{itineraryId}/itineraryDetails/{itineraryDetailId}")
    @Schema(description = "여행 일정 세부사항 수정")
    public ApiResponse<ItineraryDetailsUpdateResponse> updateItineraryDetails(
            @PathVariable UUID tripId,
            @PathVariable UUID itineraryId,
            @PathVariable UUID itineraryDetailId,
            @RequestBody ItineraryDetailsUpdateRequest request) {
        ItineraryDetailsUpdateResponse itineraryDetail =
                manageItineraryDetailsUseCase.updateItineraryDetails(tripId, itineraryId, itineraryDetailId, request);
        return ApiResponse.ok(itineraryDetail);
    }

    @DeleteMapping("/{tripId}/itineraries/{itineraryId}/itineraryDetails/{itineraryDetailsId}")
    @Schema(description = "여행 일정 세부사항 삭제")
    public ApiResponse<Void> deleteItineraryDetail(
            @PathVariable UUID tripId,
            @PathVariable UUID itineraryId,
            @PathVariable UUID itineraryDetailsId) {
        manageItineraryDetailsUseCase.deleteItineraryDetail(tripId, itineraryId, itineraryDetailsId);
        return ApiResponse.noContent();
    }

    @GetMapping("/{tripId}/itineraries")
    @Schema(description = "여행 일정 목록 조회")
    public ApiResponse<Page<ItineraryResponse>> getItineraries(
            @PathVariable UUID tripId, @PageableDefault(size = 10, page = 0) Pageable page) {
        Page<ItineraryResponse> itineraries = getItinerariesUseCase.getItineraries(tripId, page);
        return ApiResponse.ok(itineraries);
    }
}
