package com.retrip.trip.domain.vo;

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
public class ItineraryDetailDescription {

  private static final int MAX_SIZE = 100;

  @Column(name = "description", length = MAX_SIZE)
  private String value;

  public ItineraryDetailDescription(String value) {
    validate(value);
    this.value = value;
  }

  private void validate(String value) {
    if (value != null && value.length() > MAX_SIZE) {
      throw new IllegalArgumentException("여행 상세 내용은 " + MAX_SIZE + "를 넘을 수 없습니다.");
    }
  }
}
