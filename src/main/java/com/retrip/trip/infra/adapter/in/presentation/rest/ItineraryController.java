package com.retrip.trip.infra.adapter.in.presentation.rest;

import com.retrip.trip.application.in.request.ItinerariesUpdateRequest;
import com.retrip.trip.application.in.response.ItinerariesUpdateResponse;
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

    @GetMapping("/{tripId}/itineraries")
    public ResponseEntity<Page<ItineraryResponse>> getItineraries(
            @PathVariable UUID tripId, @PageableDefault(size = 10, page = 0) Pageable page) {
        Page<ItineraryResponse> itineraries = getItinerariesUseCase.getItineraries(tripId, page);
        return ResponseEntity.ok().body(itineraries);
    }
    /*
    @PutMapping("/{tripId}/itineraries")
    public ResponseEntity<ItinerariesUpdateResponse> update2Itineraries(
            @RequestBody ItinerariesUpdate2Request request) {
        ItinerariesUpdateResponse itineraries = manageItinerariesUseCase.updateItineraries(request);
        return ResponseEntity.ok().body(itineraries);
    }
     */
}
