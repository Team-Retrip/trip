package com.retrip.trip.infra.adapter.in.presentation.rest;

import com.retrip.trip.application.in.request.ItinerariesCreateRequest;
import com.retrip.trip.application.in.request.TripCreateRequest;
import com.retrip.trip.application.in.request.TripJoinRequest;
import com.retrip.trip.application.in.response.ItinerariesCreateResponse;
import com.retrip.trip.application.in.response.TripCategoryResponse;
import com.retrip.trip.application.in.response.TripCreateResponse;
import com.retrip.trip.application.in.response.TripJoinResponse;
import com.retrip.trip.application.in.response.TripResponse;
import com.retrip.trip.application.in.usecase.CreateItinerariesUseCase;
import com.retrip.trip.application.in.usecase.CreateTripUseCase;
import com.retrip.trip.application.in.usecase.GetTripUseCase;
import com.retrip.trip.application.in.usecase.JoinTripUseCase;
import com.retrip.trip.domain.entity.TripParticipant;
import com.retrip.trip.domain.vo.TripCategory;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
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
    private final JoinTripUseCase joinTripUseCase;
    private final com.retrip.trip.application.in.TripService tripService;

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

    @PostMapping("/{tripId}/joinRequest")
    public ResponseEntity<TripJoinResponse> joinTrip(
            @PathVariable("tripId") UUID tripId,
            @RequestBody TripJoinRequest request) {
        if (!tripId.equals(request.tripId())) {
            throw new IllegalArgumentException("Trip ID in path and request body must match");
        }
        TripJoinResponse response = joinTripUseCase.joinTrip(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{tripId}/joinRequest/{joinRequestId}/approve")
    public ResponseEntity<TripJoinResponse> approveJoinRequest(
            @PathVariable("tripId") UUID tripId,
            @PathVariable("joinRequestId") UUID joinRequestId) {
        TripParticipant approvedParticipant = tripService.approveJoinRequest(tripId, joinRequestId);
        return ResponseEntity.ok(TripJoinResponse.of(approvedParticipant));
    }

    @PostMapping("/{tripId}/joinRequest/{joinRequestId}/reject")
    public ResponseEntity<TripJoinResponse> rejectJoinRequest(
            @PathVariable("tripId") UUID tripId,
            @PathVariable("joinRequestId") UUID joinRequestId) {
        TripJoinResponse response = tripService.rejectJoinRequest(tripId, joinRequestId);
        return ResponseEntity.ok(response);
    }
}
