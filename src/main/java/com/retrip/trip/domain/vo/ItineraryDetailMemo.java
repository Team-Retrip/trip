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
public class ItineraryDetailMemo {

  private static final int MAX_SIZE = 200;

  @Column(name = "memo", length = MAX_SIZE)
  private String value;

  public ItineraryDetailMemo(String value) {
    validate(value);
    this.value = value;
  }

  private void validate(String value) {
    if (value != null && value.length() > MAX_SIZE) {
      throw new IllegalArgumentException("여행 상세 내용은 " + MAX_SIZE + "를 넘을 수 없습니다.");
    }
  }
}
