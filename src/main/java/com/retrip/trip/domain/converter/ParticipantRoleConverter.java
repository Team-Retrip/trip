package com.retrip.trip.domain.converter;

import com.retrip.trip.domain.vo.ParticipantRole;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ParticipantRoleConverter implements AttributeConverter<ParticipantRole, String> {

    @Override
    public String convertToDatabaseColumn(ParticipantRole participantRole) {
        if(participantRole == null){
            throw new NullPointerException("participantRole을 DB 칼럼으로 변경하는 과정에서 null이 포함되었습니다.");
        }
        return participantRole.getCode();
    }

    @Override
    public ParticipantRole convertToEntityAttribute(String dbData) {
        if(dbData == null){
            throw new NullPointerException("TripParticipant 테이블의 role 값이 null입니다.");
        }
        return ParticipantRole.codeOf(dbData);
    }
}
