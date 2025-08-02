package com.retrip.trip.application.out.crypto;

public interface TripPasswordEncoder {
    String encode(String rawPassword);
    boolean matches(String rawPassword, String encodedPassword);
}
