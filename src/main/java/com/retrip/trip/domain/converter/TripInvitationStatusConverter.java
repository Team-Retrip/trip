package com.retrip.trip.domain.converter;

import com.retrip.trip.domain.vo.InvitationStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class TripInvitationStatusConverter implements AttributeConverter<InvitationStatus, String> {

    @Override
    public String convertToDatabaseColumn(InvitationStatus status) {
        if(status == null){
            throw new NullPointerException("tripInvitationStatus DB 칼럼으로 변경하는 과정에서 null이 포함되었습니다.");
        }
        return status.getCode();
    }

    @Override
    public InvitationStatus convertToEntityAttribute(String dbData) {
        if(dbData == null){
            throw new NullPointerException("TripInvitationStatus 테이블의 status 값이 null입니다.");
        }
        return InvitationStatus.codeOf(dbData);
    }
}
