package com.retrip.trip.domain.converter;

import com.retrip.trip.domain.vo.DemandStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class TripDemandStatusConverter implements AttributeConverter<DemandStatus, String> {

    @Override
    public String convertToDatabaseColumn(DemandStatus tripDemandStatus) {
        if(tripDemandStatus == null){
            throw new NullPointerException("tripDemandStatus을 DB 칼럼으로 변경하는 과정에서 null이 포함되었습니다.");
        }
        return tripDemandStatus.getCode();
    }

    @Override
    public DemandStatus convertToEntityAttribute(String dbData) {
        if(dbData == null){
            throw new NullPointerException("TripDemand 테이블의 status 값이 null입니다.");
        }
        return DemandStatus.codeOf(dbData);
    }
}
