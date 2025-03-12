package com.retrip.trip.infra.adapter.in.presentation.rest;

import com.retrip.trip.application.in.request.ItinerariesCreateRequest;
import com.retrip.trip.application.in.request.ItinerariesUpdateRequest;
import com.retrip.trip.application.in.response.ItinerariesCreateResponse;
import com.retrip.trip.application.in.response.ItinerariesUpdateResponse;
import com.retrip.trip.application.in.usecase.CreateItinerariesUseCase;
import com.retrip.trip.application.in.usecase.UpdateItinerariesUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping("/trips/itinerary")
@RestController
public class ItineraryController {
    private final CreateItinerariesUseCase createItinerariesUseCase;
    private final UpdateItinerariesUseCase updateItinerariesUseCase;

    @PostMapping
    public ResponseEntity<ItinerariesCreateResponse> createItineraries(@RequestBody ItinerariesCreateRequest request) {
        ItinerariesCreateResponse itineraries = createItinerariesUseCase.createItineraries(request);
        return ResponseEntity.created(URI.create("/trips/" + itineraries.tripId() + "/itineraries")).body(itineraries);
    }

    @PutMapping
    public ResponseEntity<ItinerariesUpdateResponse> updateItineraries(
            @RequestBody ItinerariesUpdateRequest request
    ) {
        ItinerariesUpdateResponse itineraries = updateItinerariesUseCase.updateItineraries(request);
        return ResponseEntity.ok().body(itineraries);
    }
}
