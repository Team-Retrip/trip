package com.retrip.trip.infra.adapter.in.presentation.rest;

import com.retrip.trip.application.in.request.ItinerariesUpdateRequest;
import com.retrip.trip.application.in.response.ItinerariesUpdateResponse;
import com.retrip.trip.application.in.usecase.UpdateItinerariesUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping("/trips/itinerary")
@RestController
public class ItineraryController {
    private final UpdateItinerariesUseCase updateItinerariesUseCase;

    @PutMapping("/{tripId}")
    public ResponseEntity<ItinerariesUpdateResponse> updateItineraries(
            @PathVariable UUID tripId,
            @RequestBody ItinerariesUpdateRequest request
    ) {
        ItinerariesUpdateResponse itineraries = updateItinerariesUseCase.updateItineraries(tripId, request);
        return ResponseEntity.ok().body(itineraries);
    }
}
