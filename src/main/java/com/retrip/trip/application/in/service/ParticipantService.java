package com.retrip.trip.application.in.service;

import com.retrip.trip.application.in.request.TripJoinWithPasswordRequest;
import com.retrip.trip.application.in.response.TripJoinWithPasswordResponse;
import com.retrip.trip.application.in.usecase.ParticipantManageUseCase;
import com.retrip.trip.application.out.crypto.TripPasswordEncoder;
import com.retrip.trip.application.out.repository.ParticipantRepository;
import com.retrip.trip.application.out.repository.TripRepository;
import com.retrip.trip.domain.entity.Trip;
import com.retrip.trip.domain.entity.participant.Participant;
import com.retrip.trip.domain.exception.TripNotFoundException;
import com.retrip.trip.domain.exception.common.InvalidValueException;
import com.retrip.trip.domain.vo.TripPassword;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ParticipantService implements ParticipantManageUseCase {
    private final TripRepository tripRepository;
    private final ParticipantRepository participantRepository;
    private final TripPasswordEncoder tripPasswordEncoder;

    @Override
    public TripJoinWithPasswordResponse joinTripWithPassword(UUID tripId, TripJoinWithPasswordRequest request) {
        Trip trip = findTrip(tripId);
        TripPassword tripPassword = trip.getTripPassword();
        verifyPassword(request.password(), tripPassword);
        Participant participant = new Participant(tripId, request.memberId());
        participantRepository.save(participant);
        return TripJoinWithPasswordResponse.of(trip, participant);
    }

    private void verifyPassword(String password, TripPassword tripPassword) {
        String passwordHash = tripPassword.getPasswordHash();
        if (!tripPasswordEncoder.matches(password, passwordHash)) {
            throw new InvalidValueException("여행 비밀번호가 일치하지 않습니다.");
        }
    }

    private Trip findTrip(UUID tripId) {
        return tripRepository.findById(tripId)
                .orElseThrow(TripNotFoundException::new);
    }
}
