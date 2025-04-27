package com.retrip.trip.infra.adapter.in.presentation.rest;

import com.retrip.trip.application.in.request.ItinerariesCreateRequest;
import com.retrip.trip.application.in.response.ItinerariesCreateResponse;
import com.retrip.trip.application.in.usecase.ManageItinerariesUseCase;
import com.retrip.trip.infra.adapter.in.presentation.rest.common.ApiResponse;

import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/trips/itineraries")
@RestController
public class ItineraryController {
  private final ManageItinerariesUseCase manageItinerariesUseCase;

  @PostMapping("/{tripId}")
  public ApiResponse<ItinerariesCreateResponse> createItineraries(
      @PathVariable UUID tripId, @RequestBody ItinerariesCreateRequest request) {
    ItinerariesCreateResponse itineraries =
        manageItinerariesUseCase.createItineraries(tripId, request);
    return ApiResponse.created(itineraries);
  }
}
