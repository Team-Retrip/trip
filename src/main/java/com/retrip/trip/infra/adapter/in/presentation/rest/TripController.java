package com.retrip.trip.infra.adapter.in.presentation.rest;

import com.retrip.trip.application.in.TripService;
import com.retrip.trip.application.in.request.ItinerariesCreateRequest;
import com.retrip.trip.application.in.request.TripCreateRequest;
import com.retrip.trip.application.in.request.TripDemandRequest;
import com.retrip.trip.application.in.response.*;
import com.retrip.trip.application.in.usecase.CreateItinerariesUseCase;
import com.retrip.trip.application.in.usecase.CreateTripUseCase;
import com.retrip.trip.application.in.usecase.GetTripUseCase;
import com.retrip.trip.application.in.usecase.TripDemandUseCase;
import com.retrip.trip.domain.vo.TripCategory;
import com.retrip.trip.infra.adapter.in.presentation.rest.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RequiredArgsConstructor
@RequestMapping("/trips")
@RestController
public class TripController {
    private final CreateTripUseCase createTripUseCase;
    private final GetTripUseCase getTripUseCase;
    private final CreateItinerariesUseCase createItinerariesUseCase;
    private final TripDemandUseCase tripDemandUseCase;
    private final TripService tripService;




    @GetMapping("/categories")
    public ApiResponse<List<TripCategoryResponse>> getTripCategories() {
        List<TripCategoryResponse> response = Arrays.stream(TripCategory.values())
                .map(TripCategoryResponse::of)
                .toList();
        return ApiResponse.ok(response);
    }

    @PostMapping
    public ApiResponse<TripCreateResponse> createTrip(@RequestBody TripCreateRequest request) {
        TripCreateResponse trip = createTripUseCase.createTrip(request);
        return ApiResponse.created(trip);
    }

    @PostMapping("/regular")
    public ApiResponse<TripCreateResponse> createTripWithItineraries(@RequestBody TripCreateRequest request) {
        TripCreateResponse trip = createTripUseCase.createTripWithItineraries(request);
        return ApiResponse.created(trip);
    }

    @PostMapping("/itineraries")
    public ApiResponse<ItinerariesCreateResponse> createItineraries(@RequestBody ItinerariesCreateRequest request) {
        ItinerariesCreateResponse itineraries = createItinerariesUseCase.createItineraries(request);
        return ApiResponse.created(itineraries);
    }

    @GetMapping
    public ApiResponse<Page<TripResponse>> getTrips(@PageableDefault(size = 10, page = 0) Pageable page) {
        Page<TripResponse> trips = getTripUseCase.getTrips(page);
        return ApiResponse.ok(trips);
    }

    @PostMapping("/{tripId}/demand")
    public ApiResponse<TripDemandResponse> joinTrip(
            @PathVariable("tripId") UUID tripId,
            @RequestBody TripDemandRequest request) {
        TripDemandResponse response = tripDemandUseCase.tripDemand(tripId, request);
        return ApiResponse.ok(response);
    }

    @PutMapping("/{tripId}/demand/{tripDemandId}/approve")
    public ApiResponse<TripDemandApproveResponse> approveJoinRequest(
            @PathVariable("tripId") UUID tripId,
            @PathVariable("tripDemandId") UUID tripDemandId) {
        TripDemandApproveResponse response = tripService.approve(tripId, tripDemandId);
        return ApiResponse.ok(response);
    }

    @PutMapping("/{tripId}/demand/{tripDemandId}/reject")
    public ApiResponse<TripDemandRejectResponse> rejectJoinRequest(
            @PathVariable("tripId") UUID tripId,
            @PathVariable("tripDemandId") UUID tripDemandId) {
        TripDemandRejectResponse response = tripService.reject(tripId, tripDemandId);
        return ApiResponse.ok(response);
    }
}
