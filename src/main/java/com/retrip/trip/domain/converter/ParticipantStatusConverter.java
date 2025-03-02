package com.retrip.trip.domain.converter;

import com.retrip.trip.domain.vo.ParticipantRole;
import com.retrip.trip.domain.vo.ParticipantStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ParticipantStatusConverter implements AttributeConverter<ParticipantStatus, String> {

    @Override
    public String convertToDatabaseColumn(ParticipantStatus participantStatus) {
        if(participantStatus == null){
            throw new NullPointerException("participantStatus을 DB 칼럼으로 변경하는 과정에서 null이 포함되었습니다.");
        }
        return participantStatus.getCode();
    }

    @Override
    public ParticipantStatus convertToEntityAttribute(String dbData) {
        if(dbData == null){
            throw new NullPointerException("TripParticipant 테이블의 status 값이 null입니다.");
        }
        return ParticipantStatus.codeOf(dbData);
    }
}
