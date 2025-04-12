package com.retrip.trip.infra.adapter.in.presentation.rest;

import com.retrip.trip.application.in.request.ItinerariesUpdateRequest;
import com.retrip.trip.application.in.request.ItineraryDetailsUpdateRequest;
import com.retrip.trip.application.in.response.ItinerariesUpdateResponse;
import com.retrip.trip.application.in.response.ItineraryDetailDeleteResponse;
import com.retrip.trip.application.in.response.ItineraryDetailsUpdateResponse;
import com.retrip.trip.application.in.response.ItineraryResponse;
import com.retrip.trip.application.in.usecase.GetItinerariesUseCase;
import com.retrip.trip.application.in.usecase.ManageItinerariesUseCase;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping("/trips")
@RestController
public class ItineraryController {

    private final GetItinerariesUseCase getItinerariesUseCase;
    private final ManageItinerariesUseCase manageItinerariesUseCase;

    @PutMapping("/{tripId}/itineraries")
    public ResponseEntity<ItinerariesUpdateResponse> updateItineraries(
            @PathVariable UUID tripId, @RequestBody ItinerariesUpdateRequest request) {
        ItinerariesUpdateResponse itineraries =
                manageItinerariesUseCase.updateItineraries(tripId, request);
        return ResponseEntity.ok().body(itineraries);
    }

    @PutMapping("/{tripId}/itineraries/{itineraryId}")
    public ResponseEntity<ItineraryDetailsUpdateResponse> updateItineraryDetails(
            @PathVariable UUID tripId,
            @PathVariable UUID itineraryId,
            @RequestBody ItineraryDetailsUpdateRequest request) {
        ItineraryDetailsUpdateResponse itineraryDetail =
                manageItinerariesUseCase.updateItineraryDetails(tripId, itineraryId, request);
        return ResponseEntity.ok().body(itineraryDetail);
    }

    @DeleteMapping("/{tripId}/itineraries/{itineraryId}/{itineraryDetailsId}")
    public ResponseEntity<ItineraryDetailDeleteResponse> deleteItineraryDetail(
            @PathVariable UUID tripId,
            @PathVariable UUID itineraryId,
            @PathVariable UUID itineraryDetailsId) {
        ItineraryDetailDeleteResponse itineraryDetail =
                manageItinerariesUseCase.deleteItineraryDetail(
                        tripId, itineraryId, itineraryDetailsId);
        return ResponseEntity.ok().body(itineraryDetail);
    }

    @GetMapping("/{tripId}/itineraries")
    public ResponseEntity<Page<ItineraryResponse>> getItineraries(
            @PathVariable UUID tripId, @PageableDefault(size = 10, page = 0) Pageable page) {
        Page<ItineraryResponse> itineraries = getItinerariesUseCase.getItineraries(tripId, page);
        return ResponseEntity.ok().body(itineraries);
    }
}
