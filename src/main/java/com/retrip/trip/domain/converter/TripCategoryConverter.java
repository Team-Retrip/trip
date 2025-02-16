package com.retrip.trip.domain.converter;

import com.retrip.trip.domain.vo.TripCategory;
import com.retrip.trip.domain.vo.TripStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class TripCategoryConverter implements AttributeConverter<TripCategory, String> {

    @Override
    public String convertToDatabaseColumn(TripCategory tripCategory) {
        if(tripCategory == null){
            throw new NullPointerException("tripCategory를 DB 칼럼으로 변경하는 과정에서 null이 포함되었습니다.");
        }
        return tripCategory.getCode();
    }

    @Override
    public TripCategory convertToEntityAttribute(String dbData) {
        if(dbData == null){
            throw new NullPointerException("Trip 테이블의 category 값이 null입니다.");
        }
        return TripCategory.codeOf(dbData);
    }
}
