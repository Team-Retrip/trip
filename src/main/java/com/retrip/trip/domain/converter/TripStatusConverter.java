package com.retrip.trip.domain.converter;

import com.retrip.trip.domain.vo.TripStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class TripStatusConverter implements AttributeConverter<TripStatus, String> {

    @Override
    public String convertToDatabaseColumn(TripStatus tripStatus) {
        if(tripStatus == null){
            throw new NullPointerException("tripStatus를 DB 칼럼으로 변경하는 과정에서 null이 포함되었습니다.");
        }
        return tripStatus.getCode();
    }

    @Override
    public TripStatus convertToEntityAttribute(String dbData) {
        if(dbData == null){
            throw new NullPointerException("Trip 테이블의 status 값이 null입니다.");
        }
        return TripStatus.codeOf(dbData);
    }
}
