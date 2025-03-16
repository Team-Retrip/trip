package com.retrip.trip.infra.adapter.in.presentation.rest;

import com.retrip.trip.application.in.request.ItinerariesUpdateRequest;
import com.retrip.trip.application.in.response.ItinerariesUpdateResponse;
import com.retrip.trip.application.in.response.ItineraryResponse;
import com.retrip.trip.application.in.usecase.GetItinerariesUseCase;
import com.retrip.trip.application.in.usecase.UpdateItinerariesUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping("/trips/itinerary")
@RestController
public class ItineraryController {
    private final GetItinerariesUseCase getItinerariesUseCase;
    private final UpdateItinerariesUseCase updateItinerariesUseCase;

    @GetMapping("/{tripId}")
    public ResponseEntity<Page<ItineraryResponse>> getItineraries(@PathVariable UUID tripId, @PageableDefault(size = 10, page = 0) Pageable page) {
        Page<ItineraryResponse> itineraries = getItinerariesUseCase.getItineraries(tripId, page);
        return ResponseEntity.ok().body(itineraries);
    }


    @PutMapping
    public ResponseEntity<ItinerariesUpdateResponse> updateItineraries(
            @RequestBody ItinerariesUpdateRequest request
    ) {
        ItinerariesUpdateResponse itineraries = updateItinerariesUseCase.updateItineraries(request);
        return ResponseEntity.ok().body(itineraries);
    }
}
