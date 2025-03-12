package com.retrip.trip.infra.adapter.in.presentation.rest;

import com.retrip.trip.application.in.request.ItinerariesCreateRequest;
import com.retrip.trip.application.in.request.PeriodUpdateRequest;
import com.retrip.trip.application.in.request.TripCreateRequest;
import com.retrip.trip.application.in.response.*;
import com.retrip.trip.application.in.usecase.CreateItinerariesUseCase;
import com.retrip.trip.application.in.usecase.CreateTripUseCase;
import com.retrip.trip.application.in.usecase.GetTripUseCase;
import com.retrip.trip.application.in.usecase.UpdatePeriodUseCase;
import com.retrip.trip.domain.vo.TripCategory;

import java.util.Arrays;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping("/trips")
@RestController
public class TripController {
    private final CreateTripUseCase createTripUseCase;
    private final GetTripUseCase getTripUseCase;
    private final UpdatePeriodUseCase updatePeriodUseCase;

    @GetMapping("/categories")
    public ResponseEntity<List<TripCategoryResponse>> getTripCategories() {
        List<TripCategoryResponse> response = Arrays.stream(TripCategory.values())
                .map(TripCategoryResponse::of)
                .toList();
        return ResponseEntity.ok().body(response);
    }

    @PostMapping
    public ResponseEntity<TripCreateResponse> createTrip(@RequestBody TripCreateRequest request) {
        TripCreateResponse trip = createTripUseCase.createTrip(request);
        return ResponseEntity.created(URI.create("/trips/" + trip.id())).body(trip);
    }

    @PostMapping("/regular")
    public ResponseEntity<TripCreateResponse> createTripWithItineraries(@RequestBody TripCreateRequest request) {
        TripCreateResponse trip = createTripUseCase.createTripWithItineraries(request);
        return ResponseEntity.created(URI.create("/trips/" + trip.id())).body(trip);
    }

    @PutMapping("/period/{tripId}")
    public ResponseEntity<PeriodUpdateResponse> updatePeriod(
            @PathVariable UUID tripId,
            @RequestBody PeriodUpdateRequest request
    ) {
        PeriodUpdateResponse period = updatePeriodUseCase.updatePeriodUseCase(tripId, request);
        return ResponseEntity.ok().body(period);
    }

    @GetMapping
    public ResponseEntity<Page<TripResponse>> getTrips(@PageableDefault(size = 10, page = 0) Pageable page) {
        Page<TripResponse> trips = getTripUseCase.getTrips(page);
        return ResponseEntity.ok().body(trips);
    }
}
