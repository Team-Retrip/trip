package com.retrip.trip.infra.adapter.in.presentation.rest;

import com.retrip.trip.application.in.request.ItinerariesCreateRequest;
import com.retrip.trip.application.in.request.TripCreateRequest;
import com.retrip.trip.application.in.response.ItinerariesCreateResponse;
import com.retrip.trip.application.in.response.TripCategoryResponse;
import com.retrip.trip.application.in.response.TripCreateResponse;
import com.retrip.trip.application.in.response.TripResponse;
import com.retrip.trip.application.in.usecase.CreateItinerariesUseCase;
import com.retrip.trip.application.in.usecase.CreateTripUseCase;
import com.retrip.trip.application.in.usecase.GetTripUseCase;
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

@RequiredArgsConstructor
@RequestMapping("/trips")
@RestController
public class TripController {
    private final CreateTripUseCase createTripUseCase;
    private final GetTripUseCase getTripUseCase;
    private final CreateItinerariesUseCase createItinerariesUseCase;

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

    @PostMapping("/itineraries")
    public ResponseEntity<ItinerariesCreateResponse> createItineraries(@RequestBody ItinerariesCreateRequest request) {
        ItinerariesCreateResponse itineraries = createItinerariesUseCase.createItineraries(request);
        return ResponseEntity.created(URI.create("/trips/" + itineraries.tripId() + "/itineraries")).body(itineraries);
    }

    @GetMapping
    public ResponseEntity<Page<TripResponse>> getTrips(@PageableDefault(size = 10, page = 0) Pageable page) {
        Page<TripResponse> trips = getTripUseCase.getTrips(page);
        return ResponseEntity.ok().body(trips);
    }
}
