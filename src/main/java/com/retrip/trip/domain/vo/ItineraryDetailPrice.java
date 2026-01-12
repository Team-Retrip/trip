package com.retrip.trip.domain.vo;

import com.retrip.trip.domain.exception.common.InvalidValueException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
public class ItineraryDetailPrice {

  private static final int MAX_PRICE = 1_000_000_000;

  @Column(name = "price", length = MAX_PRICE)
  private Long value;

  public ItineraryDetailPrice(Long value) {
    validate(value);
    this.value = value;
  }

  private void validate(Long value) {
    if (value != null && value > MAX_PRICE) {
      throw new InvalidValueException("여행 상세 금액은 " + MAX_PRICE + "를 넘을 수 없습니다.");
    }
  }
}
