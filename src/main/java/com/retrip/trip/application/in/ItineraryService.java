package com.retrip.trip.application.in;

import com.retrip.trip.application.in.request.ItineraryDetailMoveRequest;
import com.retrip.trip.application.in.request.ItineraryDetailReorderRequest;
import com.retrip.trip.application.in.request.ItineraryDetailsBulkCreateRequest;
import com.retrip.trip.application.in.request.ItineraryDetailsCreateRequest;
import com.retrip.trip.application.in.request.ItineraryDetailsUpdateRequest;
import com.retrip.trip.application.in.response.ItineraryDetailsCreateResponse;
import com.retrip.trip.application.in.response.ItineraryDetailsUpdateResponse;
import com.retrip.trip.application.in.response.ItineraryResponse;
import com.retrip.trip.application.in.usecase.GetItinerariesUseCase;
import com.retrip.trip.application.in.usecase.ManageItineraryDetailsUseCase;
import com.retrip.trip.application.out.repository.TripItineraryQueryRepository;
import com.retrip.trip.domain.entity.Itinerary;
import com.retrip.trip.domain.entity.ItineraryDetail;
import com.retrip.trip.domain.exception.common.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static com.retrip.trip.domain.exception.common.ErrorCode.ITINERARY_NOT_FOUND;

@RequiredArgsConstructor
@Transactional
@Service
public class ItineraryService implements ManageItineraryDetailsUseCase, GetItinerariesUseCase {
    private final TripItineraryQueryRepository tripItineraryQueryRepository;

    @Override
    public ItineraryDetailsCreateResponse createItineraryDetails(UUID tripId,
                                                                 UUID itineraryId,
                                                                 ItineraryDetailsCreateRequest request) {
        Itinerary itinerary = findItineraryWithDetails(itineraryId);
        int sortOrder = itinerary.getItineraryDetails() != null ? itinerary.getItineraryDetails().nextSortOrder() : 0;
        ItineraryDetail itineraryDetail = request.to(itinerary, sortOrder);
        itinerary.addItineraryDetail(itineraryDetail);
        return ItineraryDetailsCreateResponse.of(itineraryDetail);
    }

    @Override
    public List<ItineraryDetailsCreateResponse> bulkCreateItineraryDetails(UUID tripId,
                                                                           UUID itineraryId,
                                                                           ItineraryDetailsBulkCreateRequest request) {
        Itinerary itinerary = findItineraryWithDetails(itineraryId);
        List<ItineraryDetail> details = request.items().stream()
                .map(item -> item.to(itinerary))
                .toList();
        return itinerary.addItineraryDetails(details).stream()
                .map(ItineraryDetailsCreateResponse::of)
                .toList();
    }

    @Override
    public void deleteAllItineraryDetails(UUID tripId, UUID itineraryId) {
        Itinerary itinerary = findItineraryWithDetails(itineraryId);
        itinerary.removeAllItineraries();
    }

    @Override
    public ItineraryDetailsUpdateResponse updateItineraryDetails(UUID tripId, UUID itineraryId,
                                                                 UUID itineraryDetailId,
                                                                 ItineraryDetailsUpdateRequest request) {
        Itinerary itinerary = findItineraryWithDetails(itineraryId);
        itinerary.updateItineraryDetail(itineraryDetailId, request.memo(), request.time(), request.locationId());
        ItineraryDetail updated = itinerary.getItineraryDetails().getValues().stream()
                .filter(detail -> detail.getId().equals(itineraryDetailId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException(ITINERARY_NOT_FOUND));
        return ItineraryDetailsUpdateResponse.of(updated);
    }

    @Override
    public void deleteItineraryDetail(UUID tripId,
                                      UUID itineraryId,
                                      UUID itineraryDetailsId) {
        Itinerary itinerary = tripItineraryQueryRepository.findByIdWithItineraryDetail(itineraryId, itineraryDetailsId)
                .orElseThrow(() -> new EntityNotFoundException(ITINERARY_NOT_FOUND));
        itinerary.removeItineraryDetail(itineraryDetailsId);
    }

    @Override
    public void moveItineraryDetail(UUID tripId,
                                    UUID itineraryId,
                                    UUID itineraryDetailId,
                                    ItineraryDetailMoveRequest request) {
        Itinerary source = findItineraryWithDetails(itineraryId);
        Itinerary target = findItineraryWithDetails(request.targetItineraryId());

        ItineraryDetail detail = source.removeDetailForMove(itineraryDetailId);
        target.acceptMovedDetail(detail, request.targetSortOrder());
    }

    @Override
    public void reorderItineraryDetails(UUID tripId,
                                        UUID itineraryId,
                                        ItineraryDetailReorderRequest request) {
        Itinerary itinerary = findItineraryWithDetails(itineraryId);
        itinerary.reorderItineraryDetails(request.orderedIds());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItineraryResponse> getItineraries(UUID tripId) {
        return tripItineraryQueryRepository.findItineraries(tripId);
    }

    private Itinerary findItineraryWithDetails(UUID itineraryId) {
        return tripItineraryQueryRepository.findByIdWithItineraryDetails(itineraryId)
                .orElseThrow(() -> new EntityNotFoundException(ITINERARY_NOT_FOUND));
    }
}