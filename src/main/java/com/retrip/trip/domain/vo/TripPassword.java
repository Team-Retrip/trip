package com.retrip.trip.domain.vo;

import com.retrip.trip.domain.exception.InvalidTripPasswordException;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@Getter
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = PROTECTED, force = true)
public class TripPassword {
    public static final int PASSWORD_MIN_LENGTH = 8;
    public static final int PASSWORD_MAX_LENGTH = 20;
    private String passwordHash;

    public TripPassword(String userPassword, String passwordHash) {
        validatePasswordLength(userPassword);
        this.passwordHash = passwordHash;
    }

    public void validatePasswordLength(String password) {
        int length = password != null ? password.trim().length() : 0;
        if (length < PASSWORD_MIN_LENGTH || length > PASSWORD_MAX_LENGTH) {
            throw new InvalidTripPasswordException(
                    String.format("비밀번호는 %d자 이상 %d자 이하로 입력해야 합니다.",
                            PASSWORD_MIN_LENGTH, PASSWORD_MAX_LENGTH
                    )
            );
        }
    }
}
