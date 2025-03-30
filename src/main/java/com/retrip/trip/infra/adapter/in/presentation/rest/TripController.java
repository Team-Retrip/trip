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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/trips")
@RestController
@Tag(name = "Trip", description = "여행 관련 API")
public class TripController {
    private final CreateTripUseCase createTripUseCase;
    private final GetTripUseCase getTripUseCase;
    private final CreateItinerariesUseCase createItinerariesUseCase;

    @Operation(summary = "여행 카테고리 조회", description = "사용 가능한 여행 카테고리를 반환합니다.")
    @ApiResponse(responseCode = "200", description = "카테고리 목록 반환 성공")
    @GetMapping("/categories")
    public ResponseEntity<List<TripCategoryResponse>> getTripCategories() {
        List<TripCategoryResponse> response = Arrays.stream(TripCategory.values())
                .map(TripCategoryResponse::of)
                .toList();
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "여행 생성", description = "새로운 여행을 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "여행 생성 성공", content = @Content(schema = @Schema(implementation = TripCreateResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @PostMapping
    public ResponseEntity<TripCreateResponse> createTrip(@RequestBody TripCreateRequest request) {
        TripCreateResponse trip = createTripUseCase.createTrip(request);
        return ResponseEntity.created(URI.create("/trips/" + trip.id())).body(trip);
    }

    @Operation(summary = "일정이 포함된 여행 생성", description = "여행과 함께 일정도 함께 생성합니다.")
    @ApiResponse(responseCode = "201", description = "여행 및 일정 생성 성공")
    @PostMapping("/regular")
    public ResponseEntity<TripCreateResponse> createTripWithItineraries(@RequestBody TripCreateRequest request) {
        TripCreateResponse trip = createTripUseCase.createTripWithItineraries(request);
        return ResponseEntity.created(URI.create("/trips/" + trip.id())).body(trip);
    }

    @Operation(summary = "여행 일정 생성", description = "여행 일정(Itineraries)을 생성합니다.")
    @ApiResponse(responseCode = "201", description = "여행 일정 생성 성공")
    @PostMapping("/itineraries")
    public ResponseEntity<ItinerariesCreateResponse> createItineraries(@RequestBody ItinerariesCreateRequest request) {
        ItinerariesCreateResponse itineraries = createItinerariesUseCase.createItineraries(request);
        return ResponseEntity.created(URI.create("/trips/" + itineraries.tripId() + "/itineraries")).body(itineraries);
    }

    @Operation(summary = "여행 목록 조회", description = "페이징 처리된 여행 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "여행 목록 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @GetMapping
    public ResponseEntity<Page<TripResponse>> getTrips(
            @Parameter(description = "페이지 정보", example = "0")
            @PageableDefault(size = 10, page = 0) Pageable page) {
        Page<TripResponse> trips = getTripUseCase.getTrips(page);
        return ResponseEntity.ok().body(trips);
    }
}
