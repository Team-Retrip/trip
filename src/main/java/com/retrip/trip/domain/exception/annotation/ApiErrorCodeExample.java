package com.retrip.trip.domain.exception.annotation;

import com.retrip.trip.domain.exception.common.ErrorCode;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ApiErrorCodeExample {

    ErrorCode value();
}
