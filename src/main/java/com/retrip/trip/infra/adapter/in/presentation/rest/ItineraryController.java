package com.retrip.trip.infra.adapter.in.presentation.rest;

import com.retrip.trip.application.in.request.ItineraryDetailsUpdateRequest;
import com.retrip.trip.application.in.request.ItineraryDetailsCreateRequest;
import com.retrip.trip.application.in.response.ItineraryDetailsCreateResponse;
import com.retrip.trip.application.in.response.ItineraryDetailsUpdateResponse;
import com.retrip.trip.application.in.response.ItineraryResponse;
import com.retrip.trip.application.in.usecase.GetItinerariesUseCase;
import com.retrip.trip.application.in.usecase.ManageItineraryDetailsUseCase;

import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/trips")
@RestController
public class ItineraryController {
    private final ManageItineraryDetailsUseCase manageItineraryDetailsUseCase;
    private final GetItinerariesUseCase getItinerariesUseCase;

    @PostMapping("/{tripId}/itineraries/{itineraryId}/itineraryDetails")
    public ResponseEntity<ItineraryDetailsCreateResponse> createItineraryDetails(
            @PathVariable UUID tripId,
            @PathVariable UUID itineraryId,
            @RequestBody ItineraryDetailsCreateRequest request) {
        ItineraryDetailsCreateResponse itineraryDetail =
                manageItineraryDetailsUseCase.createItineraryDetails(tripId, itineraryId, request);
        return ResponseEntity.ok().body(itineraryDetail);
    }

    @PutMapping("/{tripId}/itineraries/{itineraryId}/itineraryDetails/{itineraryDetailId}")
    public ResponseEntity<ItineraryDetailsUpdateResponse> updateItineraryDetails(
            @PathVariable UUID tripId,
            @PathVariable UUID itineraryId,
            @PathVariable UUID itineraryDetailId,
            @RequestBody ItineraryDetailsUpdateRequest request) {
        ItineraryDetailsUpdateResponse itineraryDetail =
                manageItineraryDetailsUseCase.updateItineraryDetails(tripId, itineraryId, itineraryDetailId, request);
        return ResponseEntity.ok().body(itineraryDetail);
    }

    @DeleteMapping("/{tripId}/itineraries/{itineraryId}/itineraryDetails/{itineraryDetailsId}")
    public ResponseEntity<Void> deleteItineraryDetail(
            @PathVariable UUID tripId,
            @PathVariable UUID itineraryId,
            @PathVariable UUID itineraryDetailsId) {
        manageItineraryDetailsUseCase.deleteItineraryDetail(tripId, itineraryId, itineraryDetailsId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{tripId}/itineraries")
    public ResponseEntity<Page<ItineraryResponse>> getItineraries(
            @PathVariable UUID tripId, @PageableDefault(size = 10, page = 0) Pageable page) {
        Page<ItineraryResponse> itineraries = getItinerariesUseCase.getItineraries(tripId, page);
        return ResponseEntity.ok().body(itineraries);
    }
}
