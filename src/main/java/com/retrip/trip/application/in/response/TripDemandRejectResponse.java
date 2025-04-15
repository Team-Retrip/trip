package com.retrip.trip.application.in.response;

public record TripDemandRejectResponse(
        String status
) {
    public TripDemandRejectResponse(String status) {
        this.status = status;
    }
}
