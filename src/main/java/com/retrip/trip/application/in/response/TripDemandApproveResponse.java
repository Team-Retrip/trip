package com.retrip.trip.application.in.response;

public record TripDemandApproveResponse(
        String status
) {
    public TripDemandApproveResponse(String status) {
        this.status = status;
    }
}
