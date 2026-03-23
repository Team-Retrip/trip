package com.retrip.trip.application.in.usecase;

import com.retrip.trip.application.in.request.ItineraryDetailMoveRequest;
import com.retrip.trip.application.in.request.ItineraryDetailReorderRequest;
import com.retrip.trip.application.in.request.ItineraryDetailsBulkCreateRequest;
import com.retrip.trip.application.in.request.ItineraryDetailsCreateRequest;
import com.retrip.trip.application.in.request.ItineraryDetailsUpdateRequest;
import com.retrip.trip.application.in.response.ItineraryDetailsCreateResponse;
import com.retrip.trip.application.in.response.ItineraryDetailsUpdateResponse;

import java.util.List;
import java.util.UUID;

public interface ManageItineraryDetailsUseCase {
    ItineraryDetailsCreateResponse createItineraryDetails(UUID tripId, UUID itineraryId, ItineraryDetailsCreateRequest request);

    List<ItineraryDetailsCreateResponse> bulkCreateItineraryDetails(UUID tripId, UUID itineraryId, ItineraryDetailsBulkCreateRequest request);

    ItineraryDetailsUpdateResponse updateItineraryDetails(UUID tripId, UUID itineraryId, UUID itineraryDetailId, ItineraryDetailsUpdateRequest request);

    void deleteItineraryDetail(UUID tripId, UUID itineraryId, UUID itineraryDetailsId);

    void deleteAllItineraryDetails(UUID tripId, UUID itineraryId);

    void moveItineraryDetail(UUID tripId, UUID itineraryId, UUID itineraryDetailId, ItineraryDetailMoveRequest request);

    void reorderItineraryDetails(UUID tripId, UUID itineraryId, ItineraryDetailReorderRequest request);
}